package com.inferneon.supervised;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.exceptions.InvalidDataException;

/**
 * Represents several counts for a given set of instances.
 *
 */

public class FrequencyCounts {
	
	private static final String NON_UNIFORM_TARGET_COUNTS = "Target value counts will be not be uniform across attributes for data with missing values.";
	
	private Long numInstances;
	private Long totalInstancesWithMissingValues;
	private Map<Attribute, Map<Value, Double>> attributeAndTargetClassCounts;
	private Map<Attribute, Long> attributeAndNonMissingValueCount;

	private List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCount;
	private Map<Attribute, Map<Value, Double>> attributeValueCounts;
	private Map<Attribute, List<Instance>> attributeAndMissingValueInstances;
	
	public Long getNumInstances() {
		return numInstances;
	}

	public void setNumInstances(Long numInstances) {
		this.numInstances = numInstances;
	}

	public Map<Value, Double> getTargetCountsForAttribute(Attribute attribute) {
		return attributeAndTargetClassCounts.get(attribute);
	}
	
	public void setAttributeAndTargetClassCounts(Map<Attribute, Map<Value, Double>> attributeAndTargetClassCounts) {
		this.attributeAndTargetClassCounts = attributeAndTargetClassCounts;
	}
	
	public List<Map<Value, Map<Value, Double>>> getValueAndTargetClassCount() {
		return valueAndTargetClassCount;
	}
	
	public Map<Attribute, Long> getAttributeAndNonMissingValueCount() {
		return attributeAndNonMissingValueCount;
	}

	public void setAttributeAndNonMissingValueCount(
			Map<Attribute, Long> attributeAndNonMissingValueCount) {
		this.attributeAndNonMissingValueCount = attributeAndNonMissingValueCount;
	}
	
	public void setValueAndTargetClassCount(
			List<Map<Value, Map<Value, Double>>> valueAndTargetClassCount) {
		this.valueAndTargetClassCount = valueAndTargetClassCount;
	}

	public void setAttributeValueCounts(
			Map<Attribute, Map<Value, Double>> attributeValueCounts) {
		this.attributeValueCounts = attributeValueCounts;		
	}
	
	public Map<Attribute, Map<Value, Double>> getAttributeValueCounts() {
		return attributeValueCounts;
	}

	public void setAttributeAndMissingValueInstances(
			Map<Attribute, List<Instance>> attributeAndMissingValueInstances) {
		this.attributeAndMissingValueInstances = attributeAndMissingValueInstances;		
	}	
	
	public Map<Attribute, List<Instance>> getAttributeAndMissingValueInstances() {
		return attributeAndMissingValueInstances;
	}

	public void setTotalInstancesWithMissingValues(
			Long totalInstancesWithMissingValues) {
		this.totalInstancesWithMissingValues =totalInstancesWithMissingValues;		
	}
	
	public Long getTotalInstancesWithMissingValues(){
		return totalInstancesWithMissingValues;
	}
	
	public Double getNumMissingInstancesForAttribute(Attribute attribute){
		 List<Instance> missingValueInstancesForAttr =  attributeAndMissingValueInstances.get(attribute);
		 if(missingValueInstancesForAttr == null || missingValueInstancesForAttr.size() == 0){
			 return 0.0;
		 }
		 
		 Double totalWeightOfInstancesWithMissingVals = 0.0;
		 
		 for(Instance ins : missingValueInstancesForAttr){
			 totalWeightOfInstancesWithMissingVals += ins.getWeight();
		 }
		 
		 return totalWeightOfInstancesWithMissingVals;
		 
	}
	
	public Map<Value, Double> getTargetCounts() throws InvalidDataException{
		
		if(attributeAndMissingValueInstances != null && attributeAndMissingValueInstances.size() > 0){
			throw new InvalidDataException(NON_UNIFORM_TARGET_COUNTS);
		}
		
		if(attributeAndTargetClassCounts.size() == 0){
			return new HashMap<Value, Double>();
		}
		
		// Target class counts for any attribute will do.
		return attributeAndTargetClassCounts.entrySet().iterator().next().getValue();
	}

	public Map<Value, Double> getTargetCounts(int attributeIndex,
			Value targetValue) {
		Map<Value, Map<Value, Double>> valueAndTargetCounts = valueAndTargetClassCount.get(attributeIndex);
		return valueAndTargetCounts.get(targetValue);
	}
	
	public Map<Value, Double> getTargetCounts(Instances instances){
		
		Map<Value, Double> targetCounts = new HashMap<>();
		
		List<Instance> insts = instances.getInstances();
		for(Instance instance : insts){
			Value classValue = instance.getValue(instances.getClassIndex());
			
			Double countForValue = targetCounts.get(classValue);
			if(countForValue == null){
				countForValue = instance.getWeight();
				targetCounts.put(classValue, countForValue);
			}
			else{
				targetCounts.put(classValue, countForValue + instance.getWeight());
			}
		}
		
		return targetCounts;
	}
	
	public Value getTargetClassWithMaxInstances(Instances instances){
		Map<Value, Double> targetCounts = getTargetCounts(instances);		
		return getTargetClassWithMaxInstances(targetCounts);
	}
	
	public Value getTargetClassWithMaxInstances(Map<Value, Double> targetCounts){
		
		Double maxValue = 0.0;
		Value valueWithMaxInstances = null;
		Set<Entry<Value, Double>> entries = targetCounts.entrySet();
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
	
	public Double getNumInstancesInMaxClass(Instances instances) {
		Map<Value, Double> targetCounts = getTargetCounts(instances);		
		Value valueWithMaxInstances = getTargetClassWithMaxInstances(targetCounts);		
		return targetCounts.get(valueWithMaxInstances);
	}
}
