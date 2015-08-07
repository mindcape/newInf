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
import java.util.Set;

import org.apache.spark.Accumulator;
import org.apache.spark.AccumulatorParam;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.DoubleFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;
import akka.util.Collections;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.Value;
import com.inferneon.core.Value.ValueType;
import com.inferneon.core.ValueComparator;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.FrequencyCounts;
import com.ipsg.inferneon.spark.SequenceCounter.SequenceCounterAccumulator;
import com.ipsg.inferneon.spark.commonfunctions.TransformStringToRDD;
import com.ipsg.inferneon.spark.utils.JavaSparkContextSingleton;
import com.ipsg.inferneon.spark.utils.SparkUtils;

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

class SequenceCounter implements Serializable {
	private Map<Value, Long> valueCounts;
	private Value runningValue;

	public static final SequenceCounterAccumulator ACCUMULATOR_SINGLETON = new SequenceCounterAccumulator();

	//	public SequenceCounter(){
	//		valueCounts = new HashMap<Value, Long>();
	//	}

	public SequenceCounter(Value value){
		//this();
		if(runningValue == null){
			runningValue = value;
			return;
		}

		if(runningValue.equals(value)){
			Long currentCount = valueCounts.get(value);
			if(currentCount == null){
				valueCounts.put(value, 2L);
			}
			else{
				valueCounts.put(value, currentCount + 1);
			}
		}

		runningValue = value;
	}

	public void merge(SequenceCounter otherSequence){
		Map<Value, Long> otherValueCounts = otherSequence.getValueCounts();
		if(otherValueCounts == null){
			return;
		}

		Iterator<Entry<Value, Long>> iterator = otherValueCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Long> entry = iterator.next();
			Value key = entry.getKey();
			Long count = entry.getValue();

			Long currentCount = valueCounts.get(key);
			if(currentCount == null){
				valueCounts.put(key, count);
			}
			else{
				valueCounts.put(key, count + currentCount);
			}			
		}			
	}

	public Map<Value, Long> getValueCounts() {
		return valueCounts;
	}

	public static class SequenceCounterAccumulator implements AccumulatorParam<SequenceCounter>, Serializable{

		public SequenceCounter addInPlace(SequenceCounter sc1, SequenceCounter sc2) {
			sc1.merge(sc2);
			return sc1;
		}

		public SequenceCounter zero(SequenceCounter arg0) {
			return new SequenceCounter(null);
		}

		public SequenceCounter addAccumulator(SequenceCounter sc1, SequenceCounter sc2) {
			sc1.merge(sc2);
			return sc1;
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
	public void sort(final Attribute attribute) {

		final int attributeIndex = attributes.indexOf(attribute);
		this.attributeIndex = attributeIndex;
		//Long count = instances.count();

		JavaPairRDD<Value, Instance> pair = instances.mapToPair(new PairFunction<Instance, Value, Instance>() {

			public Tuple2<Value, Instance> call(Instance instance) throws Exception {
				Value value = instance.getValue(attributeIndex);
				return new Tuple2<Value, Instance>(value, instance);
			}			
		});

		ValueComparator valueComparator = new ValueComparator();
		JavaPairRDD<Value, Instance> orderedInstances = pair.sortByKey(valueComparator, true);
		instances = orderedInstances.values();
		instances.cache();		

		sequenceCounts = null; // Reset so we get the correct values for a range

	}

	@Override
	public Long indexOfFirstInstanceWithMissingValueForAttribute(int attributeIndex) {

		final int attrIndex = attributeIndex;

		JavaRDD<Instance> instancesWithNonMissingValues = instances.filter(new Function<Instance, Boolean>() {

			public Boolean call(Instance instance) throws Exception {
				Value value = instance.getValue(attrIndex);
				if(value.getType() != Value.ValueType.MISSING){
					return true;
				}
				return false;
			}
		});

		return instancesWithNonMissingValues.count();		
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
		final Accumulator<TargetDistribution> attrValueCountAccumulator = javaSparkContext.accumulator(
				zeroOfAttrValueDistribution, "Attribute value counts", TargetDistribution.ACCUMULATOR_SINGLETON);

		final Accumulator<TargetDistribution> tdAccumulator = javaSparkContext.accumulator(
				zeroOfTargetDistribution, "Target counts", TargetDistribution.ACCUMULATOR_SINGLETON);
		//		SequenceCounter zeroOfSequenceCounter = new SequenceCounter(null);
		//		final Accumulator<SequenceCounter> sequenceCounterAccumulator = javaSparkContext.accumulator(
		//				zeroOfSequenceCounter, "Sequence Checker", SequenceCounter.ACCUMULATOR_SINGLETON);


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

					return true;
				}
				return false;
			}
		});

		JavaRDD<Instance> rddInstances = filtered.keys();
		rddInstances.persist(StorageLevel.MEMORY_ONLY());
		long size = rddInstances.count();
		double sumOfWts = sumOfWeightsAccumulator.value();
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
	public void appendAllInstancesWithMissingAttributeValues(IInstances other, Attribute attribute, double wtFactor) {
		JavaSparkContext javaSparkContext = JavaSparkContextSingleton.getInstance();
		SparkInstances otherInsts = (SparkInstances) other;
		JavaRDD<Instance> otherInstList = otherInsts.getInstances();

		//final Accumulator<Double> totalMissingInstancesCountAccumulator = javaSparkContext.accumulator(0.0);
		final Accumulator<Double> sumOfWtsAccumulator = javaSparkContext.accumulator(0.0);

		final Accumulator<Double> sumOfWeightsAccumulator = javaSparkContext.accumulator(0.0);

		TargetDistribution zeroOfTargetDistribution = new TargetDistribution();
		final Accumulator<TargetDistribution> tdAccumulator = javaSparkContext.accumulator(
				zeroOfTargetDistribution, "Target counts", TargetDistribution.ACCUMULATOR_SINGLETON);

		DistributionOnAttributes zeroOfAttrDistribution = new DistributionOnAttributes();
		final Accumulator<DistributionOnAttributes> tcAccumulator =  javaSparkContext.accumulator(zeroOfAttrDistribution , "Attribute Target counts", 
				DistributionOnAttributes.ACCUMULATOR_SINGLETON);

		AttributeValueDistribution zeroOfAttrValueDistribution = new AttributeValueDistribution(attributes.size());
		final Accumulator<AttributeValueDistribution> avAccumulator = javaSparkContext.accumulator(zeroOfAttrValueDistribution, "Attribute Value Target Counts",
				AttributeValueDistribution.ACCUMULATOR_SINGLETON);

		AttributeMissingValuesDistribution zeroOfAttributeMissingValuesDistribution = new AttributeMissingValuesDistribution(attributes.size());
		final Accumulator<AttributeMissingValuesDistribution> amvAccumulator = javaSparkContext.accumulator(zeroOfAttributeMissingValuesDistribution, 
				"Attribute Missing values counts", AttributeMissingValuesDistribution.ACCUMULATOR_SINGLETON);

		final double weightFactor = wtFactor;
		JavaRDD<Instance> modifiedRDD = otherInstList.map(new Function<Instance, Instance>(){
			public Instance call(Instance ins) throws Exception {
				Double weight = weightFactor * ins.getWeight();
				sumOfWtsAccumulator.add(weight);
				Instance newInstance = new Instance(ins.getValues());
				newInstance.setWeight(weight);

				// Aggregate into total sum

				System.out.println("Updated weight to add to accumulator = " + weight);
				System.out.println("Instance weight added to accumulator = " + newInstance.getWeight());

				sumOfWeightsAccumulator.add(newInstance.getWeight());

				// Collect into target value distributions
				Value targetClassValue = newInstance.getValue(attributes.size() -1);
				Map<Value, Double> newTargetCount = new HashMap<Value, Double>();
				newTargetCount.put(targetClassValue, newInstance.getWeight());
				TargetDistribution td = new TargetDistribution(newTargetCount);
				tdAccumulator.add(td);

				// Collect into all attribute-based distributions
				int numAttributes = attributes.size();
				//boolean alreadyNotedMissingValue = false;
				List<Map<Value, Map<Value, Double>>> valueAndTargetCountList = new ArrayList<Map<Value,Map<Value,Double>>>(numAttributes);
				List<Double> missingValueWeights = new ArrayList<Double>(numAttributes);
				for(int i = 0; i < numAttributes; i++){
					missingValueWeights.add(0.0);
				}
				List<Integer> missingValueSizes = new ArrayList<Integer>(numAttributes);
				for(int i = 0; i < numAttributes; i++){
					missingValueSizes.add(0);
				}
				for(int attributeIndex = 0; attributeIndex < numAttributes -1; attributeIndex++){
					Value value = newInstance.getValue(attributeIndex);					

					Attribute currentAttribute = attributes.get(attributeIndex);

					if(value.getType() == ValueType.MISSING){	
						//						if(!alreadyNotedMissingValue){
						//							double wtOfNewInstance = newInstance.getWeight();
						//							totalMissingInstancesCountAccumulator.add(wtOfNewInstance);
						//						}
						//						alreadyNotedMissingValue = true;
						valueAndTargetCountList.add(attributeIndex, null);
						missingValueWeights.set(attributeIndex, newInstance.getWeight());
						missingValueSizes.set(attributeIndex, 1);
						continue;
					}

					Map<Value, Double> countForTargetVal = new HashMap<Value, Double>(1);
					countForTargetVal.put(targetClassValue, newInstance.getWeight());
					Map<Attribute, Map<Value, Double>> newTc = new HashMap<Attribute, Map<Value,Double>>();
					newTc.put(currentAttribute, countForTargetVal);

					Map<Value, Double> countForVal = new HashMap<Value, Double>(1);
					countForVal.put(value, newInstance.getWeight());
					Map<Attribute, Map<Value, Double>> newVc = new HashMap<Attribute, Map<Value,Double>>();
					newVc.put(currentAttribute, countForVal);

					Map<Attribute, Long> nonMissingAttrCount = new HashMap<Attribute, Long>();
					nonMissingAttrCount.put(currentAttribute, new Long(1));

					Map<Value, Map<Value, Double>> targetCountsForVal = new HashMap<Value, Map<Value,Double>>();
					Map<Value, Double> tc = new HashMap<Value, Double>();
					tc.put(targetClassValue, newInstance.getWeight());
					targetCountsForVal.put(value, tc);

					valueAndTargetCountList.add(attributeIndex, targetCountsForVal);

					DistributionOnAttributes distOnAttrs = new DistributionOnAttributes(newTc, newVc, 
							nonMissingAttrCount);

					tcAccumulator.add(distOnAttrs);
				}

				AttributeValueDistribution avd = new AttributeValueDistribution(attributes.size(), valueAndTargetCountList);	
				avAccumulator.add(avd);
				AttributeMissingValuesDistribution amvDist = new AttributeMissingValuesDistribution(attributes.size(), missingValueWeights,
						missingValueSizes);
				amvAccumulator.add(amvDist);

				return newInstance;
			}			
		});


		size = instances.count();
		long numNewInsts = modifiedRDD.count();
		double sumOfWts = sumOfWeightsAccumulator.value();
		FrequencyCounts frequencCountsOfNewRDD = new FrequencyCounts();
		frequencCountsOfNewRDD.setNumInstances(numNewInsts);
		frequencCountsOfNewRDD.setSumOfWeights(sumOfWts);
		frequencCountsOfNewRDD.setTotalInstancesWithMissingValues(sumOfWts);

		TargetDistribution targetDistribution = tdAccumulator.value();		
		Map<Value, Double> totalTargetCounts = targetDistribution.getTotalTargetCounts();
		frequencCountsOfNewRDD.setTotalTargetCounts(totalTargetCounts);
		Value maxTargetValue = DataLoader.getMaxTargetValue(totalTargetCounts);
		Double maxTargetValueCount = 0.0;
		if(maxTargetValue != null){
			maxTargetValueCount = totalTargetCounts.get(maxTargetValue);
		}		
		frequencCountsOfNewRDD.setMaxTargetValue(maxTargetValue);
		frequencCountsOfNewRDD.setMaxTargetValueCount(maxTargetValueCount);

		DistributionOnAttributes attributeDistributionResult = tcAccumulator.value();		
		frequencCountsOfNewRDD.setAttributeAndTargetClassCounts(attributeDistributionResult.getAttributeAndTargetValueCounts());
		frequencCountsOfNewRDD.setAttributeValueCounts(attributeDistributionResult.getAttributeAndValueCounts());
		frequencCountsOfNewRDD.setAttributeAndNonMissingValueCount(attributeDistributionResult.getAttributeAndNonMissingValueCounts());

		AttributeValueDistribution attributeValDistributionResult = avAccumulator.value();
		frequencCountsOfNewRDD.setValueAndTargetClassCount(attributeValDistributionResult.getValueAndTargetCountList());

		AttributeMissingValuesDistribution attributeMissingValuesDistributionResult = amvAccumulator.value();
		List<Double> missingValuesSumOfWts = attributeMissingValuesDistributionResult.getMissingValueSumOfWeights();
		List<Integer> missingValuesInstSizes = attributeMissingValuesDistributionResult.getMissingValueInstSizes();

		int attrIndex = 0;
		Map<Attribute, Double> attributeMissingValuesInstWts = new HashMap<Attribute, Double>();
		Map<Attribute, Integer> attributeMissingValuesInstSizes = new HashMap<Attribute, Integer>();
		for(Double weights : missingValuesSumOfWts){
			Attribute attr = attributes.get(attrIndex);
			attributeMissingValuesInstWts.put(attr, weights);
			attributeMissingValuesInstSizes.put(attr, missingValuesInstSizes.get(attrIndex));
			attrIndex++;
		}
		frequencCountsOfNewRDD.setAttributeAndMissingValueInstanceSizes(attributeMissingValuesInstSizes);
		frequencCountsOfNewRDD.setAttributeAndMissingValueInstanceSumOfWeights(attributeMissingValuesInstWts);

		frequencyCounts.merge(frequencCountsOfNewRDD);

		instances = instances.union(modifiedRDD);
		instances.cache();
		sumOfWeights = frequencyCounts.getSumOfWeights();
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
	public void union(IInstances otherInstances) {
		SparkInstances otherSparkInsts = (SparkInstances) otherInstances;
		JavaRDD<Instance> otherRDD = otherSparkInsts.getInstances();
		JavaRDD<Instance> union = instances.union(otherRDD);
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
	public long getNextIndexWithDifferentValueInOrderedList(long index, Value value) {

		List<Value> values = new ArrayList(sequenceCounts.keySet());
		java.util.Collections.sort(values, new ValueComparator());

		if(index == 0){
			Double count = sequenceCounts.get(value);
			if(count == null){
				return index + 1;
			}

			return (long)(index + count);
		}

		Value nextValue = null;
		for(Value val : values){
			if(val.isGreaterThan(value)){
				nextValue = val;
				break;
			}
		}

		Double count = sequenceCounts.get(nextValue);
		if(count == null){
			return index + 1;
		}

		return (long)(index + count);
	}

	@Override
	public long getMaxIndexWithSameValueInOrderedList(Value value) {

		double maxIndexWithSameValue = sequenceCounts.get(value);		
		return (long)maxIndexWithSameValue;
		
	}

	//	@Override
	//	public long getNextIndexWithDifferentValueInOrderedList(long splitPoint) {
	//		if(similarSequenceIndexes == null || similarSequenceIndexes.size() == 0){
	//			return splitPoint + 1;
	//		}
	//
	//		int count = 0;
	//		for(Long[] seq : similarSequenceIndexes){
	//			Long first = seq[0];
	//			if(splitPoint == first -1){
	//				
	//				if(seq[1] == null){
	//					
	//					if(count < similarSequenceIndexes.size() -2){
	//						System.out.println("WAIT HERE");
	//					}
	//					
	//					return splitPoint +1;
	//				}
	//				
	//				return seq[1];
	//			}
	//			
	//			count++;
	//		}
	//
	//		return splitPoint + 1;
	//	}
}
