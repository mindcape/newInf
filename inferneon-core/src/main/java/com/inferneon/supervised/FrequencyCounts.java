package com.inferneon.supervised;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.Value;
import com.inferneon.core.exceptions.InvalidDataException;

/**
 * Represents several counts for a given set of instances.
 *
 */

public class FrequencyCounts {
	
	private static final String NON_UNIFORM_TARGET_COUNTS = "Target value counts will be not be uniform across attributes for data with missing values.";
	
	private Long numInstances;
	private Double sumOfWeights;
	private Double totalInstancesWithMissingValues;
	private Map<Value, Double> totalTargetCounts;
	private Value maxTargetValue;
	private Double maxTargetValueCount;
	private Map<Attribute, Map<Value, Double>> attributeAndTargetClassCounts;
	private Map<Attribute, Long> attributeAndNonMissingValueCount;

	private List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCount;
	private Map<Attribute, Map<Value, Double>> attributeValueCounts;
	private Map<Attribute, IInstances> attributeAndMissingValueInstances;
	
	public Long getNumInstances() {
		return numInstances;
	}

	public void setNumInstances(Long numInstances) {
		this.numInstances = numInstances;
	}

	public Double getSumOfWeights() {
		return sumOfWeights;
	}

	public void setSumOfWeights(Double sumOfWeights) {
		this.sumOfWeights = sumOfWeights;
	}
	
	public Map<Value, Double> getTotalTargetCounts() {
		return totalTargetCounts;
	}

	public Value getMaxTargetValue() {
		return maxTargetValue;
	}

	public void setMaxTargetValue(Value maxTargetValue) {
		this.maxTargetValue = maxTargetValue;
	}

	public Double getMaxTargetValueCount() {
		return maxTargetValueCount;
	}

	public void setMaxTargetValueCount(Double maxTargetValueCount) {
		this.maxTargetValueCount = maxTargetValueCount;
	}
	
	public void setTotalTargetCounts(Map<Value, Double> totalTargetCounts) {
		this.totalTargetCounts = totalTargetCounts;
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
			Map<Attribute, IInstances> attributeAndMissingValueInstances) {
		this.attributeAndMissingValueInstances = attributeAndMissingValueInstances;		
	}	
	
	public Map<Attribute, IInstances> getAttributeAndMissingValueInstances() {
		return attributeAndMissingValueInstances;
	}

	public void setTotalInstancesWithMissingValues(
			Double totalInstancesWithMissingValues) {
		this.totalInstancesWithMissingValues =totalInstancesWithMissingValues;		
	}
	
	public Double getTotalInstancesWithMissingValues(){
		return totalInstancesWithMissingValues;
	}
	
	public Double getNumMissingInstancesForAttribute(Attribute attribute){
		IInstances missingValueInstancesForAttr =  attributeAndMissingValueInstances.get(attribute);
		 if(missingValueInstancesForAttr == null || missingValueInstancesForAttr.size() == 0){
			 return 0.0;
		 }
		 
		 Double totalWeightOfInstancesWithMissingVals = missingValueInstancesForAttr.sumOfWeights();
		 
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

	public Double getErrorOnDistribution() {
		return sumOfWeights - maxTargetValueCount;
	}
}
