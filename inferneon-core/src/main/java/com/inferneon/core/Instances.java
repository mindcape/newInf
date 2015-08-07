package com.inferneon.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.inferneon.core.Value.ValueType;
import com.inferneon.core.exceptions.InvalidDataException;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.FrequencyCounts;

public class Instances extends IInstances {
	
	// Register this class with the factory
	static
	{
		InstancesFactory factory = InstancesFactory.getInstance();
		factory.registerProduct("STAND_ALONE", new Instances(Context.STAND_ALONE));
	}
	
	private List<Attribute> attributes;
	private List<Instance> instances;
	private int classIndex;

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

	public Iterator<Instance> iterator(){
		return instances.iterator();
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

					missingValueInstances.addInstance(instance);
					attributeAndMissingValueInstances.put(currentAttribute, missingValueInstances);
					attributeAndMissingValueInstancesSumOfWts.put(currentAttribute, currentSumOfWtsOfMissingValuInstancesForAttr 
										+ instance.getWeight());
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
		
		frequencyCounts.setTotalInstancesWithMissingValues(getSumOfWeights(instancesWithMissingvalues));

		return frequencyCounts;
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
	public Long indexOfFirstInstanceWithMissingValueForAttribute(
			int attributeIndex) {
		long firstInstanceWithMissingValueForAttribute = 0;
		for(Instance inst : instances){
			Value val = inst.getValue(attributeIndex);
			if(val.getType() == ValueType.MISSING){
				break;
			}
			firstInstanceWithMissingValueForAttribute++;
		}

		return firstInstanceWithMissingValueForAttribute;
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
	}

	@Override
	public IInstances getSubList(long fromIndex, long toIndex) {
		// In the stand-alone mode, integer should suffice on the instances collection
		List<Instance> insts = new ArrayList<>(instances.subList((int)fromIndex, (int)toIndex));

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
	public void appendAllInstancesWithMissingAttributeValues(IInstances other,
			Attribute attribute, double weightFactor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void union(IInstances missingValueInstsForAttribute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<Value, Double> getTargetClassCounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNextIndexWithDifferentValueInOrderedList(long index, Value value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getMaxIndexWithSameValueInOrderedList(Value value) {
		// TODO Auto-generated method stub
		return 0;
	}
}
