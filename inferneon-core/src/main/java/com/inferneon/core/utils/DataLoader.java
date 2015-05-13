package com.inferneon.core.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.print.AttributeException;

import au.com.bytecode.opencsv.CSVReader;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.Value.ValueType;
import com.inferneon.core.exceptions.InvalidDataException;
import com.inferneon.supervised.FrequencyCounts;

public class DataLoader {

	private static final Value UNKNOWN_VALUE = new Value();

	public static final Instances loadData(List<Attribute> attributes, String filePath) throws IOException, InvalidDataException{

		InputStream inputStream = new FileInputStream(new File(filePath));
		return loadData(attributes, inputStream, true);
	}

	public static Instances loadData(List<Attribute> attributes, String data,
			String fileName)  throws IOException, InvalidDataException{
		InputStream inputStream = new ByteArrayInputStream(data.getBytes());
		return loadData(attributes, inputStream, false);		
	}

	private static final Instances loadData(List<Attribute> attributes, InputStream inputStream, boolean removeFirstRow) throws IOException, InvalidDataException{

		CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));

		Instances instances = new Instances();

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
					val = UNKNOWN_VALUE;
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

	public static FrequencyCounts getFrequencyCounts(Instances instances){

		FrequencyCounts frequencyCounts = new FrequencyCounts();

		List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCountList = new ArrayList<>(); 

		Map<Attribute, Map<Value, Double>> attributeAndTargetClassCounts = new HashMap<>();

		Map<Attribute, Long> attributeAndNonMissingValueCount = new HashMap<>();

		Map<Attribute, Map<Value, Double>> attributeValueCounts = new HashMap<>();

		Map<Attribute, List<Instance>> attributeAndMissingValueInstances = new HashMap<>();

		Set<Instance> instancesWithMissingvalues = new HashSet<>();
		
		Map<Value, Double> totalTargetCounts = new HashMap<>();
		
		Double sumOfWeights = 0.0;

		List<Attribute> attributes = instances.getAttributes();
		int numAttributes = attributes.size();
		for(int attributeIndex = 0; attributeIndex < numAttributes -1; attributeIndex++){

			Attribute currentAttribute = attributes.get(attributeIndex);
			Map<Value, Map<Value, Double>>  valueAndTargetClassCount = new HashMap<>();
			List<Instance> insts = instances.getInstances();

			for(Instance instance: insts){
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
				
				if(value == UNKNOWN_VALUE){					
					List<Instance> missingValueInstances = attributeAndMissingValueInstances.get(currentAttribute);
					if(missingValueInstances == null){
						missingValueInstances = new ArrayList<>();

					}

					missingValueInstances.add(instance);
					attributeAndMissingValueInstances.put(currentAttribute, missingValueInstances);
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

		Value maxTargetValue = getMaxTargetValue(totalTargetCounts);
		Double maxTargetValueCount = totalTargetCounts.get(maxTargetValue);
		
		frequencyCounts.setTotalTargetCounts(totalTargetCounts);
		frequencyCounts.setSumOfWeights(sumOfWeights);
		frequencyCounts.setMaxTargetValue(maxTargetValue);
		frequencyCounts.setMaxTargetValueCount(maxTargetValueCount);
		frequencyCounts.setAttributeAndTargetClassCounts(attributeAndTargetClassCounts);
		frequencyCounts.setAttributeAndNonMissingValueCount(attributeAndNonMissingValueCount);
		frequencyCounts.setValueAndTargetClassCount(valueAndTargetClassCountList);
		frequencyCounts.setNumInstances(instances.size());
		frequencyCounts.setAttributeValueCounts(attributeValueCounts);
		frequencyCounts.setAttributeAndMissingValueInstances(attributeAndMissingValueInstances);
		frequencyCounts.setTotalInstancesWithMissingValues((long)instancesWithMissingvalues.size());

		return frequencyCounts;
	}

	public static Double getSumOfWeights(List<Instance> instances){
		double sum = 0L;
		for(Instance inst: instances){
			sum += inst.getWeight();
		}
		
		return sum;
	}
	
	private static Value getMaxTargetValue( Map<Value, Double> totalTargetCounts) {

		Double maxValue = 0.0;
		Value valueWithMaxInstances = null;
		Set<Entry<Value, Double>> entries = totalTargetCounts.entrySet();
		Iterator<Entry<Value, Double>> iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Value value = entry.getKey();
			Double numInstances = entry.getValue();
			
			if(Double.compare(numInstances, maxValue) > 0){
				maxValue = numInstances;
				valueWithMaxInstances = value;
			}
		}

		return valueWithMaxInstances;
		
	}

	public static Double getMaxValueLesserThanOrEqualTo(double thresholdValue, Attribute attribute, Instances instances){
		Double result = 0.0;
		List<Instance> insts = instances.getInstances();
		int attributeIndex = instances.attributeIndex(attribute);
		for(Instance inst: insts){
			
			Value value = inst.getValue(attributeIndex);
			if(value.getType() == ValueType.MISSING){
				continue;
			}
			
			double valInData = inst.getValue(attributeIndex).getNumericValueAsDouble();
			if(Double.compare(thresholdValue, valInData) < 0){
				continue;
			}

			if(Double.compare(valInData, result) >= 0){
				result = valInData;
			}

		}

		return result;
	}
}