package com.inferneon.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.inferneon.core.Attribute.NumericType;
import com.inferneon.core.Value.ValueType;
import com.inferneon.core.exceptions.InvalidDataException;
import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;
import com.inferneon.core.matrices.IMatrix;
import com.inferneon.core.matrices.Matrix;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.decisiontree.Impurity;
import com.inferneon.supervised.neuralnetwork.MultilayerNeuralNetwork;
import com.inferneon.supervised.neuralnetwork.NeuralNode;

public class Instances extends IInstances {

	// Register this class with the factory
	static
	{
		InstancesFactory factory = InstancesFactory.getInstance();
		factory.registerProduct("STAND_ALONE", new Instances(Context.STAND_ALONE));
	}

	private List<Instance> instances;
	private int classIndex;
	private int attributeIndex;   // Attribute index on which these instances are sorted on.
	private Value thresholdValueOfSplitsInOrderedList;

	public Instances(Context context){
		super(context);
		instances = new ArrayList<>();
	}

	public Instances(Context context, List<Instance> instances, List<Attribute> attributes, int classIndex){
		super(context);
		this.instances = instances;
		this.attributes = attributes;
		this.classIndex = classIndex;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	@Override
	public List<Attribute> getAttributes(){
		return attributes;
	}

	public long size(){
		return instances.size();
	}

	public void addInstance(Instance instance) {
		instances.add(instance);
	}

	public void setAttributes(List<Attribute> attributes){
		this.attributes = attributes;
		classIndex = attributes.size() -1;   // Default
	}

	public int getClassIndex() {
		return classIndex;
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	public int numClasses(){
		Attribute classAttribute = attributes.get(classIndex);
		return classAttribute.getNumValues();
	}

	@Override
	public IInstances removeAttribute(Attribute attribute) {
		Instances newInstances = new Instances(context);

		// Create a new values list without a value for the given attribute		
		for(Instance inst : instances){
			Value valueOfAttribute = inst.attributeValue(attribute);

			List<Value> newValues = new ArrayList<>();

			List<Value> valuesInInstance = inst.getValues();
			for(Value valInInstance : valuesInInstance){
				if(valInInstance != valueOfAttribute){
					newValues.add(valInInstance);
				}
			}

			Instance newInst = new Instance(newValues);
			newInstances.addInstance(newInst);			
		}

		// Create a new attribute list without the list of attributes
		List<Attribute> newAttributes = new ArrayList<>();
		for(Attribute attr : attributes){
			if(attr != attribute){
				newAttributes.add(attr);
			}
		}

		newInstances.setAttributes(newAttributes);

		return newInstances;
	}

	public Long getNumInstancesWithKnownValues(Attribute attribute){

		long knownValues = 0;
		for(Instance instance : this.instances){
			Value value = instance.getValue(attributes.indexOf(attribute));

			if(value.getType() != ValueType.MISSING){
				knownValues++;
			}
		}
		return knownValues;
	}

	public int attributeIndex(Attribute attribute){
		return attributes.indexOf(attribute);
	}

	@Override
	public String toString(){
		String description = attributes.size() + " attributes, " + instances.size() + " instances" 
				+ System.getProperty("line.separator");

		for(Attribute attribute : attributes){
			description += "@attribute " + attribute + System.getProperty("line.separator");
		}

		description += System.getProperty("line.separator");

		description += "@data" + System.getProperty("line.separator");
		for(Instance instance : instances){
			description += instance.toString() + System.getProperty("line.separator");
		}
		return description.trim();
	}

	public Double sumOfWeights() {
		double sum = 0L;
		for(Instance inst: instances){
			sum += inst.getWeight();
		}

		return sum;
	}

	public Double sumOfWeights(long startIndex, long endIndex) {
		double sum = 0L;

		for(long i = startIndex; i < endIndex; i++){
			Instance inst = instances.get((int)i);
			sum += inst.getWeight();
		}

		return sum;
	}

	@Override
	public FrequencyCounts getFrequencyCounts(){

		FrequencyCounts frequencyCounts = new FrequencyCounts();

		List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCountList = new ArrayList<>(); 

		Map<Attribute, Map<Value, Double>> attributeAndTargetClassCounts = new HashMap<>();

		Map<Attribute, Long> attributeAndNonMissingValueCount = new HashMap<>();

		Map<Attribute, Map<Value, Double>> attributeValueCounts = new HashMap<>();

		Map<Attribute, IInstances> attributeAndMissingValueInstances = new HashMap<>();

		Map<Attribute, Double> attributeAndMissingValueInstancesSumOfWts = new HashMap<>();

		Map<Attribute, Integer> attributeAndMissingInstanceSizes = new HashMap<Attribute, Integer>();

		Set<Instance> instancesWithMissingvalues = new HashSet<>();

		Map<Value, Double> totalTargetCounts = new HashMap<>();

		Double sumOfWeights = 0.0;

		int numAttributes = attributes.size();
		for(int attributeIndex = 0; attributeIndex < numAttributes -1; attributeIndex++){

			Attribute currentAttribute = attributes.get(attributeIndex);
			Map<Value, Map<Value, Double>>  valueAndTargetClassCount = new HashMap<>();

			for(Instance instance: instances){
				Value value = instance.getValue(attributeIndex);
				Value targetClassValue = instance.getValue(attributes.size() -1);

				if(attributeIndex == 0){
					Double instanceWeight = instance.getWeight();
					sumOfWeights += instanceWeight;
					Double targetValueCount = totalTargetCounts.get(targetClassValue);
					if(targetValueCount == null){
						totalTargetCounts.put(targetClassValue, instanceWeight);
					}
					else{
						totalTargetCounts.put(targetClassValue, targetValueCount + instanceWeight);
					}
				}

				if(value.getType() == ValueType.MISSING){					
					IInstances missingValueInstances = attributeAndMissingValueInstances.get(currentAttribute);
					Double currentSumOfWtsOfMissingValuInstancesForAttr = attributeAndMissingValueInstancesSumOfWts.get(currentAttribute);
					if(currentSumOfWtsOfMissingValuInstancesForAttr == null){
						currentSumOfWtsOfMissingValuInstancesForAttr = 0.0;
					}
					if(missingValueInstances == null){
						missingValueInstances = new Instances(context, new ArrayList<Instance>(), attributes, classIndex);
					}

					Integer currentNumMissingForAttr = attributeAndMissingInstanceSizes.get(currentAttribute);
					if(currentNumMissingForAttr == null){
						currentNumMissingForAttr = 0;
					}

					missingValueInstances.addInstance(instance);
					attributeAndMissingValueInstances.put(currentAttribute, missingValueInstances);
					attributeAndMissingValueInstancesSumOfWts.put(currentAttribute, currentSumOfWtsOfMissingValuInstancesForAttr 
							+ instance.getWeight());
					attributeAndMissingInstanceSizes.put(currentAttribute, currentNumMissingForAttr + 1);

					instancesWithMissingvalues.add(instance);
					continue;
				}

				Map<Value, Double> targetClassCount = valueAndTargetClassCount.get(value);
				if(targetClassCount == null){
					targetClassCount = new HashMap<>();
					targetClassCount.put(targetClassValue, instance.getWeight());
					valueAndTargetClassCount.put(value, targetClassCount);
				}
				else{
					Double currentCountForTarget = targetClassCount.get(targetClassValue);
					if(currentCountForTarget == null){
						targetClassCount.put(targetClassValue, instance.getWeight());
					}
					else{
						targetClassCount.put(targetClassValue, currentCountForTarget + instance.getWeight());
					}				
				}

				Map<Value, Double> targetClassCountsForAttribute = attributeAndTargetClassCounts.get(currentAttribute);
				if(targetClassCountsForAttribute == null){
					targetClassCountsForAttribute = new HashMap<>();
					targetClassCountsForAttribute.put(targetClassValue, instance.getWeight());
					attributeAndTargetClassCounts.put(currentAttribute, targetClassCountsForAttribute);

				}
				else{
					Double currentTargetCountForAttribute = targetClassCountsForAttribute.get(targetClassValue);
					if(currentTargetCountForAttribute == null){
						targetClassCountsForAttribute.put(targetClassValue, instance.getWeight());
					}
					else{
						targetClassCountsForAttribute.put(targetClassValue, currentTargetCountForAttribute + instance.getWeight());
					}
				}	
				Long nonMissingValueCountForAttribute = attributeAndNonMissingValueCount.get(currentAttribute); 
				if(nonMissingValueCountForAttribute == null){
					attributeAndNonMissingValueCount.put(currentAttribute, new Long(1));
				}
				else{
					attributeAndNonMissingValueCount.put(currentAttribute, nonMissingValueCountForAttribute + 1);
				}

				// Update the attribute's value count
				Map<Value, Double> attrValCount = attributeValueCounts.get(currentAttribute);
				if(attrValCount == null){
					attrValCount = new HashMap<>();
					attrValCount.put(value, instance.getWeight());
				}
				else{
					Double currentCountForAttrVal = attrValCount.get(value);
					if(currentCountForAttrVal == null){
						attrValCount.put(value, instance.getWeight());
					}
					else{
						attrValCount.put(value, currentCountForAttrVal + instance.getWeight());
					}	
				}
				attributeValueCounts.put(currentAttribute, attrValCount);
			}

			valueAndTargetClassCountList.add(valueAndTargetClassCount);
		}

		Value maxTargetValue = DataLoader.getMaxTargetValue(totalTargetCounts);
		Double maxTargetValueCount = 0.0;
		if(maxTargetValue != null){
			maxTargetValueCount = totalTargetCounts.get(maxTargetValue);
		}

		frequencyCounts.setTotalTargetCounts(totalTargetCounts);
		frequencyCounts.setSumOfWeights(sumOfWeights);
		frequencyCounts.setMaxTargetValue(maxTargetValue);
		frequencyCounts.setMaxTargetValueCount(maxTargetValueCount);
		frequencyCounts.setAttributeAndTargetClassCounts(attributeAndTargetClassCounts);
		frequencyCounts.setAttributeAndNonMissingValueCount(attributeAndNonMissingValueCount);
		frequencyCounts.setValueAndTargetClassCount(valueAndTargetClassCountList);
		frequencyCounts.setNumInstances((long)instances.size());
		frequencyCounts.setAttributeValueCounts(attributeValueCounts);
		frequencyCounts.setAttributeAndMissingValueInstances(attributeAndMissingValueInstances);
		frequencyCounts.setAttributeAndMissingValueInstanceSumOfWeights(attributeAndMissingValueInstancesSumOfWts);
		frequencyCounts.setAttributeAndMissingValueInstanceSizes(attributeAndMissingInstanceSizes);
		frequencyCounts.setTotalInstancesWithMissingValues(getSumOfWeights(instancesWithMissingvalues));

		return frequencyCounts;
	}

	@Override
	public IInstances insertMissingNominalValuesWithModes(Map<Attribute, Map<Value, Double>> attributesAndValueCounts){
		if(attributesAndValueCounts == null){
			FrequencyCounts frequencyCounts = getFrequencyCounts();
			attributesAndValueCounts = frequencyCounts.getAttributeValueCounts();
		}

		Map<Attribute, Value> modesOfNominalAttributes = modesOfNominalAttributes(attributesAndValueCounts);

		List<Instance> newInsts = new ArrayList<>();
		for(Instance instance : instances){	
			List<Value> newValues = new ArrayList<>();

			int attributeIndex = 0;
			for(Attribute attribute : attributes){
				Value value = instance.getValue(attributeIndex);
				if(!(attribute.getType() == Attribute.Type.NOMINAL  && value.getType() == ValueType.MISSING )){
					newValues.add(instance.getValue(attributeIndex));
					attributeIndex++;
					continue;
				}

				Value newValue = modesOfNominalAttributes.get(attribute);
				newValues.add(newValue);
				attributeIndex++;
			}			
			Instance newInst = new Instance(newValues);
			newInsts.add(newInst);
		}
		Instances newInstances = new Instances(context, newInsts, attributes, classIndex);		
		return newInstances;
	}

	private Map<Attribute, Value> modesOfNominalAttributes(Map<Attribute, Map<Value, Double>> attributesAndValueCounts) {
		Iterator<Entry<Attribute, Map<Value, Double>>> iterator = attributesAndValueCounts.entrySet().iterator();
		Map<Attribute, Value> modes = new HashMap<>();
		while(iterator.hasNext()){
			Entry<Attribute, Map<Value, Double>> entry = iterator.next();
			Attribute attribute = entry.getKey();
			Map<Value, Double>  valueAndCounts = entry.getValue();

			Value valueWithMaxCount = null; 
			Double maxCount = 0.0;
			Iterator<Entry<Value, Double>> modesIter = valueAndCounts.entrySet().iterator();
			while(modesIter.hasNext()){
				Entry<Value, Double> modesEntry = modesIter.next();
				Value val = modesEntry.getKey();
				Double count = modesEntry.getValue();
				if(Double.compare(count, maxCount) > 0){
					maxCount = count;
					valueWithMaxCount = val; 
				}
			}			
			modes.put(attribute, valueWithMaxCount);			
		}
		return modes;
	}

	@Override
	public IInstances convertNominalToSyntheticBinaryAttributes() {

		Instances modifiedInsts = (Instances)insertMissingNominalValuesWithModes(null);		
		Map<Attribute, List<Value>>  sortedValuesOnTargetAverages = modifiedInsts.getSortedValuesOnTargetAverages();

		List<Attribute> newAttrList = new ArrayList<>();

		Map<Attribute, List<List<Value>>> attributeAndNewAttributeValueList = new HashMap<>();
		int newClassIndex = classIndex;
		int currentOldAttributeIndex = 0;
		for(Attribute attribute : attributes){
			if(attribute.getType() != Attribute.Type.NOMINAL){
				newAttrList.add(attribute);
				currentOldAttributeIndex++;
				continue;
			}

			List<Value> sortedValues = sortedValuesOnTargetAverages.get(attribute);

			List<List<Value>> newAttributeValueList = new ArrayList<>();			
			for(int i = 1; i < sortedValues.size(); i++){
				List<Value> subList = sortedValues.subList(i, sortedValues.size());
				String newAttrName = buildAttributeName(attribute.getName(), subList);
				Attribute newAttribute = new Attribute(newAttrName, NumericType.INTEGER);
				newAttrList.add(newAttribute);
				newAttributeValueList.add(subList);
			}

			attributeAndNewAttributeValueList.put(attribute, newAttributeValueList);
			if(currentOldAttributeIndex <= classIndex){
				newClassIndex += sortedValues.size() -2;
			}
			currentOldAttributeIndex++;
		}

		List<Instance> newInsts = new ArrayList<>();
		List<Instance> completedInsts = modifiedInsts.getInstances();
		for(Instance instance : completedInsts){
			List<Value> newValues = new ArrayList<>();
			int attributeIndex = 0;
			for(Attribute oldAttribute : attributes){				
				Value oldValue = instance.getValue(attributeIndex);
				if(oldAttribute.getType() != Attribute.Type.NOMINAL){					
					newValues.add(oldValue);
					attributeIndex++;
					continue;
				}

				List<List<Value>> newAttributeValueList= attributeAndNewAttributeValueList.get(oldAttribute);

				for(List<Value> valsInNewAttribute : newAttributeValueList){
					Long newBinaryValue = 0L;
					if(valsInNewAttribute.contains(oldValue)){						
						newBinaryValue = 1L;
					}
					else{
						newBinaryValue = 0L;
					}
					Value newVal = new Value(newBinaryValue);
					newValues.add(newVal);
				}		
				attributeIndex++;
			}

			Instance newInst = new Instance(newValues);
			newInsts.add(newInst);
		}

		Instances newInstances = new Instances(context, newInsts, newAttrList, newClassIndex);
		return newInstances;
	}

	private String buildAttributeName(String oldAttributeName, List<Value> values){
		String newAttributeName = oldAttributeName + "=";
		for(int i = 0; i < values.size(); i++){
			Value value = values.get(i);
			newAttributeName += value.getName() + (i == values.size() -1 ? "" : ",");
		}
		return newAttributeName;
	}

	protected Map<Attribute, List<Value>> getSortedValuesOnTargetAverages(){
		List<Integer> nominalAttrIndices = new ArrayList<>();
		int numAttributes = attributes.size();

		for(int a = 0; a < numAttributes; a++){
			Attribute attribute = attributes.get(a);
			if(attribute.getType() == Attribute.Type.NOMINAL){
				nominalAttrIndices.add(a);
			}
		}

		int numNominalAttributes = nominalAttrIndices.size();
		Map<Attribute, Map<Value, Double>> attributeAndTargetValueSums = new HashMap<>();
		Map<Attribute, Map<Value, Double>> attributeAndValueCounts = new HashMap<>();

		int numInsts = instances.size();
		for(int i = 0; i < numInsts; i++){
			Instance instance = instances.get(i);
			double targetVal = instance.getValue(classIndex).getNumericValueAsDouble();

			for(int j = 0; j < numNominalAttributes; j++){
				Value value = instance.getValue(j);
				if(value.getType() == Value.ValueType.MISSING){
					continue;
				}

				Attribute attribute = attributes.get(j);
				Map<Value, Double> targetValueSum = attributeAndTargetValueSums.get(attribute);
				if(targetValueSum == null){					
					targetValueSum = new HashMap<>();
					targetValueSum.put(value, targetVal);
					attributeAndTargetValueSums.put(attribute, targetValueSum);

					Map<Value, Double> valueCounts = new HashMap<>();
					valueCounts.put(value, instance.getWeight());

					if(attribute.getName().equals("motor") && value.toString().equals("E")){
						System.out.println("First update for E: " + 1);
					}

					attributeAndValueCounts.put(attribute, valueCounts);
				}
				else{
					Double currentVal = targetValueSum.get(value);
					if(currentVal != null){
						targetValueSum.put(value, currentVal + targetVal);
					}
					else{
						targetValueSum.put(value, targetVal);
					}

					Map<Value, Double> currentValueCounts = attributeAndValueCounts.get(attribute);
					if(currentValueCounts == null){
						currentValueCounts = new HashMap<>();


						if(attribute.getName().equals("motor") && value.toString().equals("E")){
							System.out.println("Second update for E: " + 1);
						}

						currentValueCounts.put(value, instance.getWeight());
						attributeAndValueCounts.put(attribute, currentValueCounts);
					}
					else{
						Double currentValueCount = currentValueCounts.get(value);
						if(currentValueCount == null){

							if(attribute.getName().equals("motor") && value.toString().equals("E")){
								System.out.println("Third update for E: " + 1);
							}
							currentValueCounts.put(value, instance.getWeight());							
						}
						else{

							if(attribute.getName().equals("motor") && value.toString().equals("E")){
								System.out.println("Fourth updates for E: " + (currentValueCount + instance.getWeight()));
							}
							currentValueCounts.put(value, currentValueCount + instance.getWeight());
						}
					}
				}
			}
		}

		Map<Attribute, List<Value>> attributeAndSortedValuesOnTargetCounts = new HashMap<>();

		Iterator<Map.Entry<Attribute, Map<Value, Double>>> iterator = attributeAndTargetValueSums.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<Attribute, Map<Value, Double>> entry = iterator.next();
			Attribute attribute = entry.getKey();
			Map<Value, Double> targetSums = entry.getValue();
			Map<Value, Double> valueCounts = attributeAndValueCounts.get(attribute);

			List<Value> sortedValues = getSortedValuesOnTargetCounts(attribute, targetSums, valueCounts);
			attributeAndSortedValuesOnTargetCounts.put(attribute, sortedValues);			
		}						
		return attributeAndSortedValuesOnTargetCounts;		
	}

	private List<Value> getSortedValuesOnTargetCounts(Attribute attribute, Map<Value, Double> targetSums, Map<Value, Double> valueCounts) {
		List<Value> nominalValues = attribute.getAllValues();	

		List<Value> sortedResult = new ArrayList<>();
		List<Value> missingValues = new ArrayList<>();
		Map<Value, Double> valueAndAverages = new HashMap<>();
		for(Value nominalValue : nominalValues){
			Double sums = targetSums.get(nominalValue);
			Double counts = valueCounts.get(nominalValue);
			if(sums == null || counts == null){
				missingValues.add(nominalValue);
				continue;
			}

			valueAndAverages.put(nominalValue, sums/counts);	
		}

		List<Map.Entry<Value, Double>> entries = new ArrayList<Entry<Value, Double>>(valueAndAverages.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<Value, Double>>() {
			public int compare(Map.Entry<Value, Double> entry1, Map.Entry<Value, Double> entry2){
				int compare = entry1.getValue().compareTo(entry2.getValue());
				if(compare != 0){
					return compare;
				}

				return entry1.getKey().isGreaterThan(entry2.getKey()) ? 1 : -1;
			}
		});

		for(Map.Entry<Value, Double> entry : entries){
			sortedResult.add(entry.getKey());
		}

		sortedResult.addAll(missingValues);
		return sortedResult;		
	}

	@Override
	public IInstances createInstances(List<Attribute> attributes, int classIndex, String sourceURI) throws IOException, InvalidDataException{		
		if(sourceURI == null){
			IInstances instances =  new Instances(Context.STAND_ALONE, new ArrayList<Instance>(), attributes, classIndex);
			return instances;
		}

		InputStream inputStream = new FileInputStream(sourceURI);
		return loadData(attributes, inputStream, false);
	}

	@Override
	public Map<Value, IInstances> splitOnAttribute(Attribute attribute) {

		Map<Value, IInstances> valueAndInstancesHavingValue = new HashMap<Value, IInstances>();
		Iterator<Instance> iterator = instances.iterator();		

		while(iterator.hasNext()){
			Instance instance = iterator.next();
			Value value = instance.getValue(attributeIndex(attribute));

			if(value.getType() == ValueType.MISSING){
				continue;
			}

			IInstances instancesHavingValue = valueAndInstancesHavingValue.get(value);
			if(instancesHavingValue == null){
				instancesHavingValue = new Instances(context);
				instancesHavingValue.setAttributes(attributes);
			}
			instancesHavingValue.addInstance(instance);
			valueAndInstancesHavingValue.put(value,  instancesHavingValue);			
		}

		return valueAndInstancesHavingValue;
	}

	public Double getSumOfWeights(Set<Instance> instances){
		double sum = 0L;
		for(Instance inst: instances){
			sum += inst.getWeight();
		}

		return sum;
	}

	@Override
	public Double getMaxValueLesserThanOrEqualTo(double thresholdValue, Attribute attribute){
		Double result = 0.0;

		int attributeIndex = attributeIndex(attribute);
		for(Instance inst: instances){

			Value value = inst.getValue(attributeIndex);
			if(value.getType() == ValueType.MISSING){
				continue;
			}

			double valInData = value.getNumericValueAsDouble();
			if(Double.compare(thresholdValue, valInData) < 0){
				continue;
			}

			if(Double.compare(valInData, result) >= 0){
				result = valInData;
			}
		}

		return result;
	}

	@Override
	public void sort(Attribute attribute) {
		Collections.sort(instances, new InstanceComparator(attribute, attributes));
		this.attributeIndex  = attributes.indexOf(attribute);		
	}

	@Override
	public double mean(Attribute attribute, long startIndex, long endIndex){

		double sum = 0.0;
		int numInsts = instances.size();
		int missingCount = 0;
		for(int i = 0; i < numInsts; i++){
			Instance inst = instances.get(i);
			Value value = inst.getValue(attributeIndex);
			if(value.getType() == ValueType.MISSING){
				missingCount++;
				continue;
			}

			sum += value.getNumericValueAsDouble();
		}

		int netCount = numInsts - missingCount;
		double mean = sum / netCount;				
		return mean;
	}

	@Override
	public double standardDeviation(Attribute attribute, long startIndex, long endIndex){
		int attributeIndex = attributes.indexOf(attribute);
		double standardDeviation = 0.0;
		double mean = mean(attribute, startIndex, endIndex);
		int missingCount = 0;
		double sse = 0.0;
		int numInsts = (int)(endIndex - startIndex + 1);
		for(int i = (int)startIndex; i < (int)endIndex; i++){
			Instance inst = instances.get(i);
			Value value = inst.getValue(attributeIndex);
			if(value.getType() == ValueType.MISSING){
				missingCount++;
				continue;
			}

			double diff = value.getNumericValueAsDouble() - mean;
			sse += Math.pow(diff, 2.0);
		}

		int netCount = numInsts - missingCount;
		standardDeviation = Math.sqrt(sse  / netCount);

		return standardDeviation;
	}

	@Override
	public IInstances getSubList(long fromIndex, long toIndex) {
		// In the stand-alone mode, integer should suffice on the instances collection
		List<Instance> insts = new ArrayList<>(instances.subList((int)fromIndex, (int)toIndex));

		Instance thresholdInstance = instances.get((int)toIndex -1);
		thresholdValueOfSplitsInOrderedList = thresholdInstance.getValue(attributeIndex);

		return new Instances(context, insts,  attributes, classIndex);		
	}

	@Override
	public Value valueOfAttributeAtInstance(long index, int attributeIndex) {
		return instances.get((int)index).getValue(attributeIndex);	
	}

	@Override
	public void appendAll(IInstances other, double weightFactor){
		Instances otherInsts = (Instances) other;
		List<Instance> otherInstList = otherInsts.getInstances();
		for(Instance instance : otherInstList){
			Double weight = weightFactor * instance.getWeight();
			Instance newInstance = new Instance(instance.getValues());
			newInstance.setWeight(weight);
			addInstance(newInstance);
		}
	}

	private IInstances loadData(List<Attribute> attributes, InputStream inputStream, boolean removeFirstRow) throws IOException, InvalidDataException{

		CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));

		Instances instances = new Instances(Context.STAND_ALONE);

		List<String[]> rowsList = null;

		rowsList = csvReader.readAll();
		if(removeFirstRow){
			rowsList.remove(0);
		}

		int count = 0;
		for(String[] row : rowsList) {

			if(row.length != attributes.size()){
				csvReader.close();
				throw new InvalidDataException("Invalid data in row number: " + count);
			}

			int attrCount = 0;
			List<Value> valuesForInstance = new ArrayList<>();
			for(Attribute attribute : attributes){
				Value val = null;
				String strValue = row[attrCount++].trim();
				if(strValue.equals("?")){
					val = new Value();
					valuesForInstance.add(val);
					continue;
				}

				Attribute.Type attributeType = attribute.getType();
				if(attributeType == Attribute.Type.NOMINAL){
					val = attribute.getNominalValueForName(strValue);
					if(val == null){
						csvReader.close();
						throw new InvalidDataException("Unknown nominal value for attribute " + attribute.getName() + " with value " + strValue + " at row " + count);
					}
				}
				else if(attributeType == Attribute.Type.NUMERIC){
					try{
						Long number = Long.valueOf(strValue);
						val = new Value(number);
					}
					catch(NumberFormatException nfe1){
						try{
							Double realNumber = Double.valueOf(strValue);
							val = new Value(realNumber);
						}
						catch(NumberFormatException nfe2){		
							csvReader.close();
							throw new InvalidDataException("Invalid value for numeric attribute " + attribute.getName() + " with value " + strValue + " at row " + count);
						}
					}

				}
				valuesForInstance.add(val);
			}

			Instance instance = new Instance(valuesForInstance);
			instances.addInstance(instance);

			count++;			
		}

		csvReader.close();

		instances.setAttributes(attributes);

		return instances;		
	}

	@Override
	public String getContextId() {
		return "STAND_ALONE";
	}

	@Override
	public Map<Value, Double> getTargetClassCounts() {
		return getFrequencyCounts().getTotalTargetCounts();
	}

	@Override
	public long getNumOccurrencesOfValueInOrderedList(Value value) {
		Long numOccurrences = 0L;
		for(Instance inst : instances){
			Value valueOfAttrInInst = inst.getValue(attributeIndex);
			if(value.equals(valueOfAttrInInst)){
				numOccurrences++;
			}
		}

		return numOccurrences;
	}

	@Override
	public Value getThresholdValueOfSplitsInOrderedList() {
		return thresholdValueOfSplitsInOrderedList;
	}

	@Override
	public IMatrix matrix(long startRowIndex, long startColumnIndex, long endRowIndex, long endColumnIndex)
			throws MatrixElementIndexOutOfBounds{
		int numRows = (int) (endRowIndex - startRowIndex +1);
		int numColumns = (int) (endColumnIndex - startColumnIndex +1);
		double data[][] = new double[numRows][numColumns];
		for(int i = (int)startRowIndex; i < (int) endRowIndex; i++){
			Instance inst = instances.get(i);
			double vals[] = new double[numColumns];
			for(int j = 0; j < numColumns; j++){
				Value value = inst.getValue(i);
				vals[j] = value.getNumericValueAsDouble();
			}

			data[i] = vals;
		}

		Matrix matrix = new Matrix(data);
		return matrix;
	}

	@Override
	public IMatrix[] matrixAndClassVector(boolean regularize){
		int numRows = instances.size();
		int totalAttributes = attributes.size();

		int numColumnsOfMatrix = totalAttributes -1;

		double data[][] = new double[numRows][numColumnsOfMatrix];		
		Matrix valuesMat = new Matrix(data);

		double classVectorData[][] = new double[numRows][1];
		Matrix classVector = new Matrix(classVectorData);

		for(int i = 0; i < numRows; i++){
			Instance inst = instances.get(i);
			double vals[] = new double[numColumnsOfMatrix];
			double targetValues[] = new double[1];

			for(int j = 0; j < totalAttributes; j++){
				Value value = inst.getValue(j);

				if(j == classIndex){
					if(value.getType() == Value.ValueType.MISSING){
						targetValues[0] = Double.NaN;
					}
					else{
						targetValues[0] = value.getNumericValueAsDouble();
					}
				}
				else{
					vals[j] = value.getNumericValueAsDouble();
				}
			}
			classVectorData[i] = targetValues;
			data[i] = vals;
		}

		IMatrix matrixAndClassVector[] = new IMatrix[2];
		if(regularize){
			valuesMat = (Matrix)valuesMat.normalize(true, true);
			classVector = (Matrix) classVector.normalize(true, false);
		}

		matrixAndClassVector[0] = valuesMat;
		matrixAndClassVector[1] = classVector;

		return matrixAndClassVector;
	}

	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Instances)){
			return false;
		}

		Instances other = (Instances) obj;
		List<Attribute> attributesOfOther = other.getAttributes();
		if(attributes.size() != attributesOfOther.size()){
			return false;
		}

		int attrCount = 0;
		for(Attribute attribute : attributes){
			if(!(attribute.equals(attributesOfOther.get(attrCount)))){
				return false;
			}
			attrCount++;
		}

		List<Instance> instancesOfOther = other.getInstances();
		if(instances.size() != instancesOfOther.size()){
			return false;
		}

		int instanceCount = 0;
		for(Instance instance : instances){
			Instance otherInst = instancesOfOther.get(instanceCount);
			if(!(instance.equals(otherInst))){
				return false;
			}	
			instanceCount++;
		}
		return true;
	}

	@Override
	public double trainNeuralNetwork(MultilayerNeuralNetwork network,double learningRate, boolean isStochastic) {

		List<NeuralNode> inputNodes = network.getInputNodes();
		List<NeuralNode> outputNodes = network.getOutputNodes();
		List<List<NeuralNode>> hiddenLayers = network.getHiddenLayers();

		if(isStochastic){
			Instance instance = instances.get(0);
			network.calculateOutputs(instance, inputNodes, hiddenLayers, outputNodes);
			network.calculateError(instance, hiddenLayers, outputNodes, attributes);
			network.updateWeight(hiddenLayers, outputNodes, learningRate, isStochastic);
		}
		else{
			// Batch gradient descent
			for(int ins=0; ins<instances.size(); ins++){
				Instance instance = instances.get(ins);
				network.calculateOutputs(instance, inputNodes, hiddenLayers, outputNodes);
				network.calculateError(instance, hiddenLayers, outputNodes, attributes);
				network.updateWeight(hiddenLayers, outputNodes, learningRate, isStochastic);
			}
		}
		return 0;
	}
	

	@Override
	public Impurity initializeImpurity(Attribute attribute, long partitionIndex, int impurityOrder) {
		long numInstances = instances.size();
		double sum = 0.0;
		double squareSum = 0.0, variance = 0.0, stardDev = 0.0;
		double numLeft = 0.0, numRight = 0.0;
		double sumLeft = 0.0, sumRight = 0.0;
		double squareSumLeft = 0.0, squareSumRight = 0.0;
		int count = 0;
		for(int i = 0; i < numInstances; i++){
			Instance instance = instances.get((int)i);
			Value value = instance.getValue(classIndex);			
			if(value.getType() == Value.ValueType.MISSING){
				continue;
			}
			count++;
			Double val = value.getNumericValueAsDouble();
			sum += val;
			squareSum += val * val;

			if(count > 1){
				variance = Math.abs((squareSum - sum * sum/count)/count);
				stardDev = Math.sqrt(variance);				
			}
			else {
				variance = 0.0; 
				stardDev = 0.0;
			}

			if(i < partitionIndex){
				//numLeft = i;
				numLeft++;
				sumLeft = sum;
				squareSumLeft = squareSum;
			}
			else{
				//numRight = i - numLeft;
				numRight++;
				sumRight = sum - sumLeft;
				squareSumRight = squareSum - squareSumLeft;
			}
		}
		//numLeft++;
		
		Impurity impurity = new Impurity(attribute, impurityOrder, numInstances, numLeft, numRight, sumLeft, 
				sumRight, squareSumLeft, squareSumRight, variance, stardDev);
		return impurity;
	}
	
	@Override
	public Impurity updateImpurity(long startIndex, long endIndex, Impurity impurity){
		
		double maxImpurityValue = Double.MIN_VALUE;
		Attribute attribute = impurity.getAttribute();
		int attributeIndex = attributes.indexOf(attribute);		
		
		Impurity maxImpurity = null;
		double impurityValue = Double.MIN_VALUE;
		
		for(long i = startIndex; i < endIndex; i++){
			Instance instance = instances.get((int)i);
			Double classValue = instance.getValue(classIndex).getNumericValueAsDouble();
			impurityValue = impurity.updateNext(classValue);
			
			Value attrValue = instance.getValue(attributeIndex);
			Instance nextInstance = instances.get((int)i + 1);			
			Value attrValueNext = nextInstance.getValue(attributeIndex);
			if(attrValue.equals(attrValueNext)){
				continue;
			}
			
			if(Double.compare(impurityValue, maxImpurityValue) > 0){
				maxImpurityValue = impurityValue;
				maxImpurity = impurity.clone();
				double splitValue = (attrValue.getNumericValueAsDouble() + attrValueNext.getNumericValueAsDouble()) / 2.0;
			    maxImpurity.setSplitValue(splitValue);
			}
		}
		
		return maxImpurity;
	}	
}
