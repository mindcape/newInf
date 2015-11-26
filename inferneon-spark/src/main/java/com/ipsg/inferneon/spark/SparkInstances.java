package com.ipsg.inferneon.spark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.spark.Accumulator;
import org.apache.spark.AccumulatorParam;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.DoubleFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.apache.spark.rdd.RDD;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.Value;
import com.inferneon.core.Value.ValueType;
import com.inferneon.core.ValueComparator;
import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;
import com.inferneon.core.matrices.IMatrix;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.decisiontree.Impurity;
import com.inferneon.supervised.neuralnetwork.MultilayerNeuralNetwork;
import com.ipsg.inferneon.spark.commonfunctions.TransformStringToRDD;
import com.ipsg.inferneon.spark.utils.JavaSparkContextSingleton;
import com.ipsg.inferneon.spark.utils.SparkUtils;

class SimpleNumericValueCounter implements Serializable {
	private Long intVal;
	private Double realVal;
	private boolean isReal;
	
	public boolean isReal(){
		return isReal;
	}
	
	public SimpleNumericValueCounter(){
		intVal = 0L;
		realVal = 0.0;
	}
	
	public SimpleNumericValueCounter(Long intVal){
		this.intVal = intVal;
		isReal = false;
	}
	
	public SimpleNumericValueCounter(Double realVal){
		this.realVal = realVal;
		isReal = true;
	}
	
	public void add(SimpleNumericValueCounter other){
		if(other.isReal){
			realVal += other.realVal;
		}
		else{
			intVal += other.intVal;
		}
	}
	
	public Double getRealVal(){
		return realVal;
	}
	
	public Long getIntVal(){
		return intVal;
	}
	
	public static final SimpleNumericValueCounterAccumulator ACCUMULATOR_SINGLETON = new SimpleNumericValueCounterAccumulator();
	
	public static class SimpleNumericValueCounterAccumulator implements AccumulatorParam<SimpleNumericValueCounter>, Serializable{

		public SimpleNumericValueCounter addInPlace(SimpleNumericValueCounter counter1, SimpleNumericValueCounter counter2) {
			counter1.add(counter2);
			return counter1;
		}

		public SimpleNumericValueCounter zero(SimpleNumericValueCounter counter) {
			if(counter.isReal){
				return new SimpleNumericValueCounter(counter.getRealVal());
			}
			else{
				return new SimpleNumericValueCounter(counter.getIntVal());
			}
		}

		public SimpleNumericValueCounter addAccumulator(SimpleNumericValueCounter counter1, SimpleNumericValueCounter counter2) {
			counter1.add(counter2);
			return counter1;
		}		
	}	
}

class TargetDistribution implements Serializable {
	private Map<Value, Double> totalTargetCounts;

	public static final TargetDistributionAccumulator ACCUMULATOR_SINGLETON = new TargetDistributionAccumulator();

	public TargetDistribution(){
		totalTargetCounts = new HashMap<Value, Double>();
	}

	public TargetDistribution(Map<Value, Double> totalTargetCounts){
		this.totalTargetCounts = totalTargetCounts;
	}

	//	public void setCount(Value value, Double count){
	//		Double currentCount = totalTargetCounts.get(value);
	//		if(currentCount == null){
	//			totalTargetCounts.put(value, count);
	//		}
	//		else{
	//			totalTargetCounts.put(value, count + currentCount);
	//		}
	//	}

	public void merge(TargetDistribution otherDist){
		Iterator<Entry<Value, Double>> iterator = otherDist.getTotalTargetCounts().entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Value key = entry.getKey();
			Double count = entry.getValue();

			Double currentCount = totalTargetCounts.get(key);
			if(currentCount == null){
				totalTargetCounts.put(key, count);
			}
			else{
				totalTargetCounts.put(key, count + currentCount);
			}			
		}			
	}

	public Map<Value, Double> getTotalTargetCounts() {
		return totalTargetCounts;
	}

	public static class TargetDistributionAccumulator implements AccumulatorParam<TargetDistribution>, Serializable{

		public TargetDistribution addInPlace(TargetDistribution td1, TargetDistribution td2) {
			System.out.println("Inside addInPlace");
			td1.merge(td2);
			return td1;
		}

		public TargetDistribution zero(TargetDistribution arg0) {
			return new TargetDistribution();
		}

		public TargetDistribution addAccumulator(TargetDistribution td1, TargetDistribution td2) {
			System.out.println("Inside addAccumulator");
			td1.merge(td2);
			return td1;
		}		
	}	
}

class DistributionOnAttributes implements Serializable{

	public static final DistributionOnAttributesAccumulator ACCUMULATOR_SINGLETON = new DistributionOnAttributesAccumulator();

	private Map<Attribute, Map<Value, Double>> attributeAndTargetValueCounts;
	private Map<Attribute, Map<Value, Double>> attributeAndValueCounts;
	private Map<Attribute, Long> attributeAndNonMissingValueCounts;

	public DistributionOnAttributes(){		
		attributeAndTargetValueCounts = new HashMap<Attribute, Map<Value,Double>>();
		attributeAndValueCounts = new HashMap<Attribute, Map<Value,Double>>();	
		attributeAndNonMissingValueCounts = new HashMap<Attribute, Long>();
	}

	public DistributionOnAttributes(Map<Attribute, Map<Value, Double>> attributeAndTargetValueCounts,
			Map<Attribute, Map<Value, Double>> attributeAndValueCounts, Map<Attribute, Long> attributeAndNonMissingValueCounts){
		this.attributeAndTargetValueCounts = attributeAndTargetValueCounts;
		this.attributeAndValueCounts = attributeAndValueCounts;
		this.attributeAndNonMissingValueCounts = attributeAndNonMissingValueCounts;
	}

	public Map<Attribute, Map<Value, Double>> getAttributeAndTargetValueCounts(){
		return attributeAndTargetValueCounts;
	}

	public Map<Attribute, Map<Value, Double>> getAttributeAndValueCounts(){
		return attributeAndValueCounts;
	}

	public Map<Attribute, Long> getAttributeAndNonMissingValueCounts(){
		return attributeAndNonMissingValueCounts;
	}

	private void setCount(Attribute attribute, Value value, Double newCount, boolean forTargetCount){
		Map<Value, Double> currentValues = null;

		if(forTargetCount){
			currentValues = attributeAndTargetValueCounts.get(attribute);
		}
		else{
			currentValues = attributeAndValueCounts.get(attribute);
		}

		if(currentValues == null){
			currentValues = new HashMap<Value, Double>();
		}

		if(forTargetCount){
			attributeAndTargetValueCounts.put(attribute, currentValues);
		}
		else{
			attributeAndValueCounts.put(attribute, currentValues);
		}

		Double current = currentValues.get(value);
		if(current == null){
			current = 0.0;
		}
		currentValues.put(value, current + newCount);				
	}

	public void merge(DistributionOnAttributes otherDistribution){

		Map<Attribute, Map<Value, Double>> otherAttributeAndTargetValueCounts = otherDistribution.getAttributeAndTargetValueCounts();
		Map<Attribute, Map<Value, Double>> otherAttributeAndValueCounts = otherDistribution.getAttributeAndValueCounts();
		Map<Attribute, Long> otherAttributeAndNonMissingCount = otherDistribution.getAttributeAndNonMissingValueCounts();

		merge(otherAttributeAndTargetValueCounts, true);
		merge(otherAttributeAndValueCounts, false);	
		merge(otherAttributeAndNonMissingCount);
	}

	private void merge(Map<Attribute, Map<Value, Double>> attributeDistributions, boolean forTargetCounts){
		Iterator<Entry<Attribute, Map<Value, Double>>> iterator = attributeDistributions.entrySet().iterator();
		while(iterator.hasNext()){			
			Entry<Attribute, Map<Value, Double>> entry = iterator.next();
			Attribute attr = entry.getKey(); 
			Map<Value, Double> targetCountsForAttr = entry.getValue();

			Iterator<Entry<Value, Double>> iterator2 = targetCountsForAttr.entrySet().iterator();
			while(iterator2.hasNext()){
				Entry<Value, Double> entry2 = iterator2.next();
				Value targetValue = entry2.getKey();
				Double count = entry2.getValue();
				setCount(attr, targetValue, count, forTargetCounts);
			}			
		}		
	}

	private void merge(Map<Attribute, Long> otherAttributeAndNonMissingCount) {
		Iterator<Entry<Attribute, Long>> iterator = otherAttributeAndNonMissingCount.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Attribute, Long> entry = iterator.next();
			Attribute attribute = entry.getKey();
			Long nonMissingCount = entry.getValue();

			Long currentCount = attributeAndNonMissingValueCounts.get(attribute);
			if(currentCount == null){
				attributeAndNonMissingValueCounts.put(attribute, nonMissingCount);
			}
			else{
				attributeAndNonMissingValueCounts.put(attribute, nonMissingCount + currentCount);
			}			
		}		
	}

	public static class  DistributionOnAttributesAccumulator implements AccumulatorParam<DistributionOnAttributes>, Serializable{

		public DistributionOnAttributes addInPlace(DistributionOnAttributes dist1, DistributionOnAttributes dist2) {
			dist1.merge(dist2);
			return dist1;
		}

		public DistributionOnAttributes zero(DistributionOnAttributes other) {
			return new DistributionOnAttributes();
		}

		public DistributionOnAttributes addAccumulator(DistributionOnAttributes dist1, DistributionOnAttributes dist2) {
			dist1.merge(dist2);
			return dist1;
		}
	}	
}

class AttributeValueDistribution implements Serializable {

	private int size;
	private List<Map<Value, Map<Value, Double>>> valueAndTargetCountList;

	public static final AttributeValueDistributionAccumulator ACCUMULATOR_SINGLETON = new AttributeValueDistributionAccumulator();

	public AttributeValueDistribution(int size){
		this.size = size;
		valueAndTargetCountList = new ArrayList<Map<Value,Map<Value,Double>>>(size);
		initEmptyList();
	}

	public AttributeValueDistribution(int size, List<Map<Value, Map<Value, Double>>> valueAndTargetCountList){
		this.size = size;
		this.valueAndTargetCountList = valueAndTargetCountList;
	}

	private void initEmptyList() {
		for(int i = 0; i < size ; i++){
			valueAndTargetCountList.add(null);
		}
	}

	public List<Map<Value, Map<Value, Double>>> getValueAndTargetCountList(){
		return valueAndTargetCountList;
	}

	public void merge(AttributeValueDistribution otherAvd){
		List<Map<Value, Map<Value, Double>>> otherList = otherAvd.getValueAndTargetCountList();
		for(int i = 0; i < otherList.size(); i++){
			Map<Value, Map<Value, Double>> otherCounts = otherList.get(i);		
			if(otherCounts == null){
				continue;
			}

			Map<Value, Map<Value, Double>> currentCounts = valueAndTargetCountList.get(i);
			if(currentCounts == null){
				valueAndTargetCountList.set(i, otherCounts);
				continue;
			}

			merge(currentCounts, otherCounts);
		}
	}

	private void merge(Map<Value, Map<Value, Double>> current, Map<Value, Map<Value, Double>> other){

		Iterator<Entry<Value, Map<Value, Double>>>  iteratorOther = other.entrySet().iterator();
		while(iteratorOther.hasNext()){
			Entry<Value, Map<Value, Double>> entry = iteratorOther.next();
			Value key = entry.getKey();
			Map<Value, Double> otherTc = entry.getValue();

			Map<Value, Double> tc = current.get(key);
			if(tc == null){
				current.put(key, otherTc);
			}
			else{
				Iterator<Entry<Value, Double>> otherValIterator = otherTc.entrySet().iterator();
				while(otherValIterator.hasNext()){
					Entry<Value, Double> entry2 = otherValIterator.next();					
					Value otherTarget = entry2.getKey();
					Double otherCount = entry2.getValue();

					Double currCount = tc.get(otherTarget);
					if(currCount == null){
						tc.put(otherTarget, otherCount);
					}
					else{
						tc.put(otherTarget, currCount + otherCount);
					}					
				}
			}
		}		
	}

	public static class  AttributeValueDistributionAccumulator implements AccumulatorParam<AttributeValueDistribution>, Serializable{
		public AttributeValueDistribution addInPlace(AttributeValueDistribution avd1, AttributeValueDistribution avd2) {
			avd1.merge(avd2);
			return avd1;
		}

		public AttributeValueDistribution zero(AttributeValueDistribution avd) {
			return new AttributeValueDistribution(avd.size);
		}

		public AttributeValueDistribution addAccumulator(AttributeValueDistribution avd1, AttributeValueDistribution avd2) {
			avd1.merge(avd2);
			return avd1;
		}		
	}	
}

class AttributeMissingValuesDistribution implements Serializable {

	private int numAttributes;
	private List<Double> missingValueSumOfWeights;
	private List<Integer> missingValueSize;

	public static final AttributeMissingValuesDistributionAccumulator ACCUMULATOR_SINGLETON = new AttributeMissingValuesDistributionAccumulator();

	public AttributeMissingValuesDistribution(int size){
		this.numAttributes = size;
		missingValueSumOfWeights = new ArrayList<Double>(size);
		missingValueSize = new ArrayList<Integer>(size);
		initEmptyList();
	}

	public AttributeMissingValuesDistribution(int numAttributes, List<Double> missingValueSumOfWeights, List<Integer> missingValueSize){
		this.numAttributes = numAttributes;
		this.missingValueSumOfWeights = missingValueSumOfWeights;
		this.missingValueSize = missingValueSize;
	}

	private void initEmptyList() {
		for(int i = 0; i < numAttributes ; i++){
			missingValueSumOfWeights.add(0.0);
			missingValueSize.add(0);
		}
	}

	public List<Double> getMissingValueSumOfWeights(){
		return missingValueSumOfWeights;
	}

	public List<Integer> getMissingValueInstSizes(){
		return missingValueSize;
	}

	public void merge(AttributeMissingValuesDistribution otherAmvd){
		List<Double> otherList = otherAmvd.getMissingValueSumOfWeights();
		List<Integer> otherSizeList = otherAmvd.getMissingValueInstSizes();
		for(int i = 0; i < otherList.size(); i++){
			Double otherSumForAttribute = otherList.get(i);		
			Integer otherSizeForAttribute = otherSizeList.get(i);

			Double currentSum = missingValueSumOfWeights.get(i);
			if(currentSum == null){
				missingValueSumOfWeights.set(i, otherSumForAttribute);
			}
			else{
				missingValueSumOfWeights.set(i, currentSum + otherSumForAttribute);
			}

			Integer currentNum = missingValueSize.get(i);
			if(currentNum == null){
				missingValueSize.set(i, otherSizeForAttribute);
			}
			else{
				missingValueSize.set(i, currentNum + otherSizeForAttribute);
			}			 
		}
	}

	public static class  AttributeMissingValuesDistributionAccumulator implements AccumulatorParam<AttributeMissingValuesDistribution>, Serializable{
		public AttributeMissingValuesDistribution addInPlace(AttributeMissingValuesDistribution avd1, 
				AttributeMissingValuesDistribution avd2) {
			avd1.merge(avd2);
			return avd1;
		}

		public AttributeMissingValuesDistribution zero(AttributeMissingValuesDistribution avd) {
			return new AttributeMissingValuesDistribution(avd.numAttributes);
		}

		public AttributeMissingValuesDistribution addAccumulator(AttributeMissingValuesDistribution avd1, 
				AttributeMissingValuesDistribution avd2) {
			avd1.merge(avd2);
			return avd1;
		}		
	}	
}

class TargetCountAggregate implements Serializable{
	private Map<Value, Double> targetCountAggregate;

	//public static final TargetDistributionAccumulator ACCUMULATOR_SINGLETON = new TargetDistributionAccumulator();
}

class DoubleComparator implements Comparator<Double>, Serializable {
	public int compare(Double val1, Double val2) {
		return Double.compare(val1, val2);
	}	
}

public class SparkInstances extends IInstances implements Serializable{

	private JavaRDD<Instance> instances;
	private Long size;
	private Double sumOfWeights;
	private FrequencyCounts frequencyCounts;
	private Map<Value, Double> targetClassCounts;
	private Map<Value, Double> sequenceCounts;

	private int attributeIndex;
	private Value thresholdValueOfSplitsInOrderedList;

	static
	{
		InstancesFactory factory = InstancesFactory.getInstance();
		factory.registerProduct("SPARK", new SparkInstances());
	}	

	public SparkInstances() {
		super(Context.SPARK);
	}

	private SparkInstances(List<Attribute> attributes, int classIndex, JavaRDD<Instance> instances){
		super(Context.SPARK);
		this.classIndex = classIndex;
		this.attributes = attributes;
		this.instances = instances;
	}
	
	private SparkInstances(List<Attribute> attributes, int classIndex, JavaRDD<Instance> instances, int attributeIndex){
		super(Context.SPARK);
		this.classIndex = classIndex;
		this.attributes = attributes;
		this.instances = instances;
		this.attributeIndex = attributeIndex;
	}
	

	public SparkInstances( List<Attribute> attributes, int classIndex, String sourceURI){
		super(Context.SPARK);
		this.classIndex = classIndex;

		JavaSparkContext javaSparkContext = JavaSparkContextSingleton.getInstance();	
		this.attributes = attributes;

		JavaRDD<String> rawInput = javaSparkContext.textFile(sourceURI);
		instances = rawInput.map(new TransformStringToRDD(this.attributes, false));
	}

	public SparkInstances(List<Attribute> attributes, int classIndex, JavaRDD<Instance> rddInstances, 
			long size, double sumOfWts, Map<Value, Double> targetClassCounts) {
		super(Context.SPARK);
		this.classIndex = classIndex;
		this.attributes = attributes;
		this.instances = rddInstances;
		this.size = size;
		this.sumOfWeights = sumOfWts;
		this.targetClassCounts = targetClassCounts;
	}

	public void setSequenceCounts(Map<Value, Double> sequenceCounts){
		this.sequenceCounts = sequenceCounts;
	}

	public JavaRDD<Instance> getInstances(){
		return instances;
	}

	@Override
	public IInstances createInstances(List<Attribute> attributes, int classIndex, String sourceURI) throws Exception {
		return new SparkInstances(attributes, classIndex, sourceURI);		
	}

	@Override
	public long size() {		
		if(size == null){
			size = instances.count();
		}		
		return size;		
	}

	@Override
	public List<Attribute> getAttributes() {
		return attributes;
	}

	@Override
	public void addInstance(Instance instance) {		
		// No-op
	}

	@Override
	public Double sumOfWeights() {
		if(sumOfWeights == null){
			sumOfWeights = SparkUtils.getSumOfWeights(instances);
		}
		return sumOfWeights;
	}

	@Override
	public FrequencyCounts getFrequencyCounts() {
		if(frequencyCounts != null){
			return frequencyCounts;
		}

		JavaSparkContext javaSparkContext = JavaSparkContextSingleton.getInstance();
		final Accumulator<Double> sumOfWeightsAccumulator = javaSparkContext.accumulator(0.0);

		final Accumulator<Double> totalMissingInstancesCountAccumulator = javaSparkContext.accumulator(0.0);

		TargetDistribution zeroOfTargetDistribution = new TargetDistribution();
		final Accumulator<TargetDistribution> tdAccumulator = javaSparkContext.accumulator(
				zeroOfTargetDistribution, "Target counts", TargetDistribution.ACCUMULATOR_SINGLETON);

		DistributionOnAttributes zeroOfAttrDistribution = new DistributionOnAttributes();
		final Accumulator<DistributionOnAttributes> tcAccumulator =  javaSparkContext.accumulator(zeroOfAttrDistribution , "Attribute Target counts", 
				DistributionOnAttributes.ACCUMULATOR_SINGLETON);

		AttributeValueDistribution zeroOfAttrValueDistribution = new AttributeValueDistribution(attributes.size());
		final Accumulator<AttributeValueDistribution> avAccumulator = javaSparkContext.accumulator(zeroOfAttrValueDistribution, "Attribute Value Target Counts",
				AttributeValueDistribution.ACCUMULATOR_SINGLETON);

		instances.cache();

		long before = new Date().getTime();

		// Attribute related distributions
		JavaRDD<Instance> missingValueInstsRDD = instances.filter(new Function<Instance, Boolean>(){

			public Boolean call(Instance instance) throws Exception {

				// Aggregate into total sum
				sumOfWeightsAccumulator.add(instance.getWeight());

				// Collect into target value distributions
				Value targetClassValue = instance.getValue(attributes.size() -1);
				Map<Value, Double> newTargetCount = new HashMap<Value, Double>();
				newTargetCount.put(targetClassValue, instance.getWeight());
				TargetDistribution td = new TargetDistribution(newTargetCount);
				tdAccumulator.add(td);

				// Collect into all attribute-based distributions
				int numAttributes = attributes.size();
				boolean alreadyNotedMissingValue = false;
				List<Map<Value, Map<Value, Double>>> valueAndTargetCountList = new ArrayList<Map<Value,Map<Value,Double>>>(numAttributes);
				for(int attributeIndex = 0; attributeIndex < numAttributes -1; attributeIndex++){
					Value value = instance.getValue(attributeIndex);					

					Attribute currentAttribute = attributes.get(attributeIndex);

					if(value.getType() == ValueType.MISSING){	
						if(!alreadyNotedMissingValue){
							totalMissingInstancesCountAccumulator.add(instance.getWeight());
						}
						alreadyNotedMissingValue = true;
						valueAndTargetCountList.add(attributeIndex, null);
						continue;
					}

					Map<Value, Double> countForTargetVal = new HashMap<Value, Double>(1);
					countForTargetVal.put(targetClassValue, instance.getWeight());
					Map<Attribute, Map<Value, Double>> newTc = new HashMap<Attribute, Map<Value,Double>>();
					newTc.put(currentAttribute, countForTargetVal);

					Map<Value, Double> countForVal = new HashMap<Value, Double>(1);
					countForVal.put(value, instance.getWeight());
					Map<Attribute, Map<Value, Double>> newVc = new HashMap<Attribute, Map<Value,Double>>();
					newVc.put(currentAttribute, countForVal);

					Map<Attribute, Long> nonMissingAttrCount = new HashMap<Attribute, Long>();
					nonMissingAttrCount.put(currentAttribute, new Long(1));

					Map<Value, Map<Value, Double>> targetCountsForVal = new HashMap<Value, Map<Value,Double>>();
					Map<Value, Double> tc = new HashMap<Value, Double>();
					tc.put(targetClassValue, instance.getWeight());
					targetCountsForVal.put(value, tc);

					valueAndTargetCountList.add(attributeIndex, targetCountsForVal);

					DistributionOnAttributes distOnAttrs = new DistributionOnAttributes(newTc, newVc, 
							nonMissingAttrCount);

					tcAccumulator.add(distOnAttrs);
				}

				AttributeValueDistribution avd = new AttributeValueDistribution(attributes.size(), valueAndTargetCountList);						
				avAccumulator.add(avd);


				if(alreadyNotedMissingValue){
					return true;
				}

				return false;
			}
		}
				);

		long numInstances = instances.count();
		long after = new Date().getTime();		
		double timeElapsedDblFirstIter = ((double)(after - before)) / 1000.0;
		System.out.println("First iteration computed in: " + timeElapsedDblFirstIter + " seconds");

		// Get a mapping of attributes that are missing with corresponding instances, size and sum of weights
		Map<Attribute, IInstances> attributeAndMissingInstances = new HashMap<Attribute, IInstances>();
		Map<Attribute, Double> attributeAndMissingInstanceSumOfWts = new HashMap<Attribute, Double>();
		Map<Attribute, Integer> attributeAndMissingInstanceSizes = new HashMap<Attribute, Integer>();
		//JavaRDD<Instance> missingInstsRDD = instances.filter(new SplitMissingOrCompleteValuesInInstances(attributes, true));
		missingValueInstsRDD.cache();

		//before = new Date().getTime();	

		for(int attrIndex = 0; attrIndex < attributes.size(); attrIndex++){
			final Attribute attribute = attributes.get(attrIndex);	
			final int currIndex = attrIndex;

			final Accumulator<Double> sumOfWeightsOfInstsForAttrAccumulator = javaSparkContext.accumulator(0.0);
			final Accumulator<Integer> sizeOfMissingValueInstsForAttribute = javaSparkContext.accumulator(0);

			JavaRDD<Instance> missingInstsForAttribute = missingValueInstsRDD.filter(new Function<Instance, Boolean>() {

				public Boolean call(Instance instance) throws Exception {
					Value val = instance.getValue(currIndex);
					if(val.getType() == Value.ValueType.MISSING){
						sumOfWeightsOfInstsForAttrAccumulator.add(instance.getWeight());
						sizeOfMissingValueInstsForAttribute.add(1);
						return true;
					}
					return false;
				}
			});	

			missingInstsForAttribute.persist(StorageLevel.MEMORY_ONLY());			
			long sizeOfMissingInstsForAttr = missingInstsForAttribute.count();

			double sumOfWeightsOfInstsForAttr = sumOfWeightsOfInstsForAttrAccumulator.value();
			attributeAndMissingInstances.put(attribute, new SparkInstances(attributes, classIndex, missingInstsForAttribute, 
					sizeOfMissingInstsForAttr, sumOfWeightsOfInstsForAttr, null));
			Integer sizeOfMissingValueInsts = sizeOfMissingValueInstsForAttribute.value();
			attributeAndMissingInstanceSumOfWts.put(attribute, sumOfWeightsOfInstsForAttr);
			attributeAndMissingInstanceSizes.put(attribute, sizeOfMissingValueInsts);
		}

		double sumOfWeightsOfMissingValueInsts = totalMissingInstancesCountAccumulator.value();

		FrequencyCounts frequencyCounts = new FrequencyCounts();
		frequencyCounts.setNumInstances(numInstances);
		frequencyCounts.setSumOfWeights(sumOfWeightsAccumulator.value());

		frequencyCounts.setTotalInstancesWithMissingValues(sumOfWeightsOfMissingValueInsts);

		TargetDistribution targetDistribution = tdAccumulator.value();		
		Map<Value, Double> totalTargetCounts = targetDistribution.getTotalTargetCounts();
		frequencyCounts.setTotalTargetCounts(totalTargetCounts);
		Value maxTargetValue = DataLoader.getMaxTargetValue(totalTargetCounts);
		Double maxTargetValueCount = 0.0;
		if(maxTargetValue != null){
			maxTargetValueCount = totalTargetCounts.get(maxTargetValue);
		}		
		frequencyCounts.setMaxTargetValue(maxTargetValue);
		frequencyCounts.setMaxTargetValueCount(maxTargetValueCount);

		DistributionOnAttributes attributeDistributionResult = tcAccumulator.value();		
		frequencyCounts.setAttributeAndTargetClassCounts(attributeDistributionResult.getAttributeAndTargetValueCounts());
		frequencyCounts.setAttributeValueCounts(attributeDistributionResult.getAttributeAndValueCounts());
		frequencyCounts.setAttributeAndNonMissingValueCount(attributeDistributionResult.getAttributeAndNonMissingValueCounts());

		AttributeValueDistribution attributeValDistributionResult = avAccumulator.value();
		frequencyCounts.setValueAndTargetClassCount(attributeValDistributionResult.getValueAndTargetCountList());
		frequencyCounts.setAttributeAndMissingValueInstances(attributeAndMissingInstances);
		frequencyCounts.setAttributeAndMissingValueInstanceSumOfWeights(attributeAndMissingInstanceSumOfWts);
		frequencyCounts.setAttributeAndMissingValueInstanceSizes(attributeAndMissingInstanceSizes);
		this.frequencyCounts = frequencyCounts;
		return frequencyCounts;
	}

	@Override
	public Map<Value, IInstances> splitOnAttribute(final Attribute attribute) {
		List<Value> valuesOfAttribute = attribute.getAllValues();

		Map<Value, IInstances> result = new HashMap<Value, IInstances>();

		for(final Value value : valuesOfAttribute){
			JavaRDD<Instance> instancesWithValue = instances.filter(new Function<Instance, Boolean>() {

				public Boolean call(Instance instance) throws Exception {
					Value val = instance.attributeValue(attribute);
					if(value.equals(val)){
						return true;
					}

					return false;
				}
			});

			instancesWithValue.persist(StorageLevel.MEMORY_ONLY());

			result.put(value, new SparkInstances(attributes, classIndex, instancesWithValue));
		}
		return result;
	}

	@Override
	public IInstances sort(final Attribute attribute) {

		final int attributeIndex = attributes.indexOf(attribute);

		JavaPairRDD<Value, Instance> pair = instances.mapToPair(new PairFunction<Instance, Value, Instance>() {

			public Tuple2<Value, Instance> call(Instance instance) throws Exception {
				Value value = instance.getValue(attributeIndex);
				return new Tuple2<Value, Instance>(value, instance);
			}			
		});

		ValueComparator valueComparator = new ValueComparator();
		JavaPairRDD<Value, Instance> orderedInstances = pair.sortByKey(valueComparator, true);
		JavaRDD<Instance> newInstsRDD =  orderedInstances.values();
		newInstsRDD.cache();		
		SparkInstances newInstances = new SparkInstances(attributes, classIndex, newInstsRDD, attributeIndex);

		return newInstances;

	}
	
	@Override
	public Double sumOfWeights(final long startIndex, final long endIndex) {

		JavaSparkContext javaSparkContext = JavaSparkContextSingleton.getInstance();	
		final Accumulator<Double> sum = javaSparkContext.accumulator(0.0);

		JavaPairRDD<Instance, Long> indexed = instances.zipWithIndex();
		indexed.foreach(new VoidFunction<Tuple2<Instance,Long>>() {

			public void call(Tuple2<Instance, Long> tuple) throws Exception {
				if(tuple._2() >= startIndex && tuple._2() < endIndex){
					sum.add(tuple._1().getWeight());
				}
			}
		});

		return sum.value();
	}

	@Override
	public IInstances removeAttribute(final Attribute attribute) {
		JavaRDD<Instance> mappedInsts = instances.map(new Function<Instance, Instance>() {

			public Instance call(Instance instance) throws Exception {
				Value valueOfAttribute = instance.attributeValue(attribute);

				List<Value> newValues = new ArrayList<Value>();

				List<Value> valuesInInstance = instance.getValues();
				for(Value valInInstance : valuesInInstance){
					if(valInInstance != valueOfAttribute){
						newValues.add(valInInstance);
					}
				}

				Instance newInst = new Instance(newValues);
				return newInst;
			}
		});

		// Create a new attribute list without the list of attributes
		List<Attribute> newAttributes = new ArrayList<Attribute>();
		for(Attribute attr : attributes){
			if(attr != attribute){
				newAttributes.add(attr);
			}
		}

		SparkInstances newInstances = new SparkInstances(newAttributes, newAttributes.size() -1, mappedInsts);
		return newInstances;
	}

	@Override
	public Double getMaxValueLesserThanOrEqualTo(final double thresholdValue, final Attribute attribute) {

		final int attributeIndex = attributes.indexOf(attribute);

		JavaRDD<Instance> filteredRDD = instances.filter(new Function<Instance, Boolean>() {

			public Boolean call(Instance instance) throws Exception {
				Value value = instance.getValue(attributeIndex);
				if(value.getType() == ValueType.MISSING){
					return false;
				}

				double valInData = value.getNumericValueAsDouble();
				if(Double.compare(thresholdValue, valInData) < 0){
					return false;
				}

				return true;
			}
		});

		JavaDoubleRDD doubleRDD = filteredRDD.mapToDouble(new DoubleFunction<Instance>() {

			public double call(Instance instance) throws Exception {
				Value value = instance.getValue(attributeIndex);
				return value.getNumericValueAsDouble();
			}
		});

		Double max = doubleRDD.max(new DoubleComparator());
		return max;		
	}

	@Override
	public IInstances getSubList(final long startIndex, final long endIndex) {
		JavaSparkContext javaSparkContext = JavaSparkContextSingleton.getInstance();	
		TargetDistribution zeroOfTargetDistribution = new TargetDistribution();
		TargetDistribution zeroOfAttrValueDistribution = new TargetDistribution();
		SimpleNumericValueCounter zeroOfThresholdValIdentifier = new SimpleNumericValueCounter();

		//SimpleNumericValueCounterAccumulator
		
		final Accumulator<SimpleNumericValueCounter> thresholdValAccumulator = javaSparkContext.accumulator(
				zeroOfThresholdValIdentifier, "Threshold value identifier", SimpleNumericValueCounter.ACCUMULATOR_SINGLETON);
		
		final Accumulator<TargetDistribution> attrValueCountAccumulator = javaSparkContext.accumulator(
				zeroOfAttrValueDistribution, "Attribute value counts", TargetDistribution.ACCUMULATOR_SINGLETON);

		final Accumulator<TargetDistribution> tdAccumulator = javaSparkContext.accumulator(
				zeroOfTargetDistribution, "Target counts", TargetDistribution.ACCUMULATOR_SINGLETON);
		
		final Accumulator<Double> sumOfWeightsAccumulator = javaSparkContext.accumulator(0.0);
	
		JavaPairRDD<Instance, Long> indexed = instances.zipWithIndex();		
		JavaPairRDD<Instance, Long> filtered = indexed.filter(new Function<Tuple2<Instance,Long>, Boolean>() {

			public Boolean call(Tuple2<Instance, Long> tuple) throws Exception {
				Long index = tuple._2();
				Instance instance = tuple._1();

				// Collect into attribute's value into its own accumulator
				Value value = instance.getValue(attributeIndex);
				Map<Value, Double> attrValueCount = new HashMap<Value, Double>();
				attrValueCount.put(value, 1.0);
				TargetDistribution attrValueDist = new TargetDistribution(attrValueCount);
				attrValueCountAccumulator.add(attrValueDist);

				if(index >= startIndex && index < endIndex){					
					sumOfWeightsAccumulator.add(instance.getWeight());

					// Collect into target value distributions
					Value targetClassValue = instance.getValue(attributes.size() -1);
					Map<Value, Double> newTargetCount = new HashMap<Value, Double>();
					newTargetCount.put(targetClassValue, instance.getWeight());
					TargetDistribution td = new TargetDistribution(newTargetCount);
					tdAccumulator.add(td);
					
					if(index == endIndex -1){
						SimpleNumericValueCounter thresholdValueIdentifier = null;
						if(value.getType() == Value.ValueType.NUMERIC){
							thresholdValueIdentifier = new SimpleNumericValueCounter(value.getNumber());
						}
						else{
							thresholdValueIdentifier = new SimpleNumericValueCounter(value.getReal());
						}
						thresholdValAccumulator.add(thresholdValueIdentifier);
					}

					return true;
				}
				return false;
			}
		});
		
		JavaRDD<Instance> rddInstances = filtered.keys();
		rddInstances.persist(StorageLevel.MEMORY_ONLY());
		long size = rddInstances.count();
		double sumOfWts = sumOfWeightsAccumulator.value();
		
		SimpleNumericValueCounter thresholdValue = thresholdValAccumulator.value();
		if(thresholdValue.isReal()){
			thresholdValueOfSplitsInOrderedList = new Value(thresholdValue.getRealVal());
		}
		else{
			thresholdValueOfSplitsInOrderedList = new Value(thresholdValue.getIntVal());
		}
		TargetDistribution targetDistribution = tdAccumulator.value();		
		Map<Value, Double> totalTargetCounts = targetDistribution.getTotalTargetCounts();

		if(sequenceCounts == null || sequenceCounts.size() == 0){
			TargetDistribution finalSequenceCounter = attrValueCountAccumulator.value();
			System.out.println("**** LAST SIMILAR VALUE SEQ CHECKER = " + finalSequenceCounter.hashCode());
			Map<Value, Double> sequences = finalSequenceCounter.getTotalTargetCounts();
			setSequenceCounts(sequences);
		}

		SparkInstances result = new SparkInstances(attributes, classIndex, rddInstances, size, sumOfWts, totalTargetCounts);

		return result;
	}
	
	@Override
	public Value valueOfAttributeAtInstance(final long index, int attributeIndex) {

		JavaPairRDD<Instance, Long> indexed = instances.zipWithIndex();
		JavaPairRDD<Instance, Long> filtered = indexed.filter(new Function<Tuple2<Instance,Long>, Boolean>() {

			public Boolean call(Tuple2<Instance, Long> tuple) throws Exception {
				if(tuple._2() == index){
					return true;
				}
				return false;
			}
		});

		List<Tuple2<Instance, Long>> tupleList = filtered.take(1);
		Instance instance = tupleList.get(0)._1();

		return instance.getValue(attributeIndex);
	}

	@Override
	public void appendAll(IInstances other, final double weightFactor) {
		JavaSparkContext javaSparkContext = JavaSparkContextSingleton.getInstance();
		SparkInstances otherInsts = (SparkInstances) other;
		JavaRDD<Instance> otherInstList = otherInsts.getInstances();

		final Accumulator<Double> sumOfWtsAccumulator = javaSparkContext.accumulator(0.0);

		JavaRDD<Instance> modifiedRDD = otherInstList.map(new Function<Instance, Instance>(){
			public Instance call(Instance instance) throws Exception {
				Double weight = weightFactor * instance.getWeight();
				sumOfWtsAccumulator.add(weight);
				Instance newInstance = new Instance(instance.getValues());
				newInstance.setWeight(weight);
				return newInstance;
			}			
		});

		instances = instances.union(modifiedRDD);
		instances.cache();
		size = instances.count();

		double sumOfWtsOfNewList = sumOfWtsAccumulator.value();
		if(sumOfWeights == null){
			System.out.println("WAIT HERE");
			sumOfWeights = sumOfWtsOfNewList;
		}
		else{
			sumOfWeights += sumOfWtsOfNewList;
		}
		frequencyCounts = null; // Will be re-evaluated when needed
	}

	@Override
	public String toString(){
		List<Instance> insts = instances.collect();
		return insts.toString();
	}

	@Override
	public String getContextId() {
		return "SPARK";
	}

	@Override
	public Map<Value, Double> getTargetClassCounts() {
		if(targetClassCounts != null){
			return targetClassCounts;
		}
		else{
			return getFrequencyCounts().getTotalTargetCounts();
		}
	}

	@Override
	public long getNumOccurrencesOfValueInOrderedList(Value value) {
		double maxIndexWithSameValue = sequenceCounts.get(value);		
		return (long)maxIndexWithSameValue;		
	}

	@Override
	public Value getThresholdValueOfSplitsInOrderedList() {
		return thresholdValueOfSplitsInOrderedList;
	}

	@Override
	public IMatrix matrix(long startRowIndex, long startColumnIndex,
			long endRowIndex, long endColumnIndex)
			throws MatrixElementIndexOutOfBounds {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMatrix[] matrixAndClassVector(boolean regularize) {
		JavaRDD<Vector> valuesVector = instances.map(new Function<Instance, Vector>() {

			public Vector call(Instance instance) throws Exception {
				
				int exampleSize = attributes.size() -1;
				double values[] = new double[exampleSize];
				for(int i = 0; i < attributes.size(); i++){
					if(i != classIndex){
						values[i]  = instance.getValue(i).getNumericValueAsDouble();
					}
					
				}
				
				Vector vector = new DenseVector(values);
				return vector;
			}
		});
		
		JavaRDD<Vector> targetsVector = instances.map(new Function<Instance, Vector>() {

			public Vector call(Instance instance) throws Exception {
				
				int exampleSize = attributes.size() -1;
				double values[] = new double[exampleSize];
				for(int i = 0; i < attributes.size(); i++){
					if(i == classIndex){
						values[i]  = instance.getValue(i).getNumericValueAsDouble();
					}					
				}
				
				Vector vector = new DenseVector(values);
				return vector;
			}
		});
		
		RowMatrix valuesMatrix = new RowMatrix(valuesVector.rdd());
		RowMatrix targetsMatrix = new RowMatrix(targetsVector.rdd());
		
//		SparkMatrix valuesSparkMatrix = new SparkMatrix(valuesMatrix);
//		SparkMatrix targetsSparkMatrix = new SparkMatrix(targetsMatrix);
//		
//		IMatrix[] matrices = new IMatrix[2];
//		matrices[0] = valuesSparkMatrix; matrices[1] = targetsSparkMatrix;
//		return matrices;
		return null;
	}

	@Override
	public IInstances insertMissingNominalValuesWithModes(
			Map<Attribute, Map<Value, Double>> attributesAndValueCounts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double mean(Attribute attribute, long startIndex, long endIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double standardDeviation(Attribute attribute, long startIndex, long endIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IInstances convertNominalToSyntheticBinaryAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double trainNeuralNetwork(MultilayerNeuralNetwork network, double learningRate, double momentum,int numEpoch, boolean isStochastic) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Impurity initializeImpurity(Attribute attribute,
			long partitionIndex, int impurityOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Impurity updateImpurity(long startIndex, long endIndex,
			Impurity impurity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double partialDerivativeOfCostFunctionForLinearRegression(
			double[] parameters, int featureIndex) {

		return SparkUtils.getSumOfErrors(instances, attributes.size() -1, 
				parameters, classIndex, featureIndex) / instances.count();
	}

	@Override
	public double[] gradientDescentForLinearRegression(
			double[] linearRegressionParams, int numIterations, Double stepSize) {

		double[] tempParams = new double[linearRegressionParams.length];
		for(int i = 0; i < numIterations; i++){
			for(int j = 0; j < linearRegressionParams.length; j++){
				tempParams[j] = linearRegressionParams[j] - stepSize * partialDerivativeOfCostFunctionForLinearRegression(linearRegressionParams, j);
			}
			linearRegressionParams = tempParams;
		}
		return linearRegressionParams;
	}

	@Override
	public double[] stochasticGradientDescentForLinearRegression(
			double[] linearRegressionParams, int numIterations, Double stepSize) {
		// TODO Auto-generated method stub
		return null;
	}

}
