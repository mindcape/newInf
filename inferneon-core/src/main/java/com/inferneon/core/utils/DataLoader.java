package com.inferneon.core.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.core.exceptions.InvalidDataException;
import com.inferneon.supervised.FrequencyCounts;

public class DataLoader {

	private static final Value UNKNOWN_VALUE = new Value();

	public static final Instances loadData(List<Attribute> attributes, String filePath) throws IOException, InvalidDataException{
		Instances instances = null;
		try(InputStream is = ParserUtils.class.getResourceAsStream(filePath);) {
			instances = loadData(attributes, is, true);
		}
		catch (IOException e) {
			System.out.println("Error in reading the input file");
		}
		return instances;
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

		List<Attribute> attributes = instances.getAttributes();
		int numAttributes = attributes.size();
		for(int attributeIndex = 0; attributeIndex < numAttributes -1; attributeIndex++){

			if(attributeIndex == 3){
				System.out.println("WAIT HERE");
			}

			Attribute currentAttribute = attributes.get(attributeIndex);
			Map<Value, Map<Value, Double>>  valueAndTargetClassCount = new HashMap<>();
			List<Instance> insts = instances.getInstances();

			for(Instance instance: insts){
				Value value = instance.getValue(attributeIndex);
				Value targetClassValue = instance.getValue(attributes.size() -1);

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

		frequencyCounts.setAttributeAndTargetClassCounts(attributeAndTargetClassCounts);
		frequencyCounts.setAttributeAndNonMissingValueCount(attributeAndNonMissingValueCount);
		frequencyCounts.setValueAndTargetClassCount(valueAndTargetClassCountList);
		frequencyCounts.setNumInstances(instances.size());
		frequencyCounts.setAttributeValueCounts(attributeValueCounts);
		frequencyCounts.setAttributeAndMissingValueInstances(attributeAndMissingValueInstances);
		frequencyCounts.setTotalInstancesWithMissingValues((long)instancesWithMissingvalues.size());

		return frequencyCounts;
	}

	public static Double getMaxValueLesserThanOrEqualTo(double thresholdValue, Attribute attribute, Instances instances){
		Double result = 0.0;
		List<Instance> insts = instances.getInstances();
		int attributeIndex = instances.attributeIndex(attribute);
		for(Instance inst: insts){
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
