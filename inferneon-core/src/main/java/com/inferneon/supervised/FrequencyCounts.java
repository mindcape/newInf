package com.inferneon.supervised;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Value;
import com.inferneon.core.utils.MathUtils;

/**
 * Represents several counts for a given set of instances.
 *
 */

public class FrequencyCounts implements Serializable{

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
	private Map<Attribute, Integer> attributeAndMissingValueInstanceSizes;
	private Map<Attribute, Double> attributeAndMissingValueInstanceSumOfWeights;

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

	public Map<Attribute, Map<Value, Double>> getAttributeAndTargetClassCounts() {
		return attributeAndTargetClassCounts;
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
	
	public Map<Attribute, Integer> getAttributeAndMissingValueInstanceSizes() {
		return attributeAndMissingValueInstanceSizes;
	}

	public void setAttributeAndMissingValueInstanceSizes(
			Map<Attribute, Integer> attributeAndMissingValueInstanceSizes) {
		this.attributeAndMissingValueInstanceSizes = attributeAndMissingValueInstanceSizes;
	}

	public Map<Attribute, Double> getAttributeAndMissingValueInstanceSumOfWeights() {
		return attributeAndMissingValueInstanceSumOfWeights;
	}

	public void setAttributeAndMissingValueInstanceSumOfWeights(
			Map<Attribute, Double> attributeAndMissingValueInstanceSumOfWeights) {
		this.attributeAndMissingValueInstanceSumOfWeights = attributeAndMissingValueInstanceSumOfWeights;
	}
	
	public Double getNumMissingInstancesForAttribute(Attribute attribute){
//		IInstances missingValueInstancesForAttr =  attributeAndMissingValueInstances.get(attribute);
//		if(missingValueInstancesForAttr == null || missingValueInstancesForAttr.size() == 0){
//			return 0.0;
//		}
//		Double totalWeightOfInstancesWithMissingVals = missingValueInstancesForAttr.sumOfWeights();
//
//		return totalWeightOfInstancesWithMissingVals;
		
		Double sumOfWts = attributeAndMissingValueInstanceSumOfWeights.get(attribute);
		if(sumOfWts == null){
			return 0.0;
		}
		return sumOfWts;
	}
	
	public Map<Value, Double> getTargetCounts(int attributeIndex,
			Value targetValue) {
		Map<Value, Map<Value, Double>> valueAndTargetCounts = valueAndTargetClassCount.get(attributeIndex);
		return valueAndTargetCounts.get(targetValue);
	}

	public Double getErrorOnDistribution() {
		if(sumOfWeights == null || maxTargetValueCount == null){
			System.out.println("WAIT HERE");
		}
		return sumOfWeights - maxTargetValueCount;
	}

	@Override
	public String toString(){
		String newLine = "\n";
		String description = "";
		description += "===========Total number of instances = " + numInstances + newLine;
		description += "===========Sum of weights = " + sumOfWeights + newLine;
		description += "===========Total number of instances with missing values = " + totalInstancesWithMissingValues + newLine;
		description += "===========Target value with max instances: " + maxTargetValue +  newLine;
		description += "===========Count of target value with max instances: " + maxTargetValueCount +  newLine;
		description += "===========Target counts: " + newLine;
		description += valueAndDoublePairDescription(totalTargetCounts.entrySet().iterator(), newLine, 1);
		description += "===========Target counts for each attribute: " + newLine;
		description += attributeAndValueDoublePairDescription(attributeAndTargetClassCounts.entrySet().iterator(), newLine);
		description += "===========Counts for each attribute value: " + newLine;
		description += attributeAndValueDoublePairDescription(attributeValueCounts.entrySet().iterator(), newLine);
		description += "===========Non-missing instance count for each atribute: " + newLine;
		Iterator<Entry<Attribute, Long>> iterator = attributeAndNonMissingValueCount.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Attribute, Long> entry = iterator.next();
			description += "	" + entry.getKey().getName() + ": ";
			description += entry.getValue() + newLine;						
		}
		description += "===========Value and target class count for each attribute by index: " + newLine;
		int attributeIndex = 0;
		for(Map<Value, Map<Value, Double>> valAndTargetClassCount : valueAndTargetClassCount){
			if(valAndTargetClassCount == null){
				continue;
			}
			description += "Attribute index " + (attributeIndex +1) + ": " + newLine;
			Iterator<Entry<Value, Map<Value, Double>>> iterator1 = valAndTargetClassCount.entrySet().iterator();
			while(iterator1.hasNext()){
				Entry<Value, Map<Value, Double>> entry1 = iterator1.next();
				String valName = entry1.getKey().getName();
				if(valName == null){
					continue;
				}
				description += "	Value: " + valName + ": " + newLine;
				Map<Value, Double> targetClassCounts = entry1.getValue();
				description += valueAndDoublePairDescription(targetClassCounts.entrySet().iterator(), newLine, 3);						
			}			
			attributeIndex++;
		}

		description += "===========Attribute and missing value instances (size and sum of weights): " + newLine;
		Iterator<Entry<Attribute, Integer>> attrIterator = attributeAndMissingValueInstanceSizes.entrySet().iterator();
		while(attrIterator.hasNext()){
			Entry<Attribute, Integer> entry = attrIterator.next();
			Attribute attribute = entry.getKey();
			Integer missingValueInstsSize = entry.getValue();
			Double sumOfWts = attributeAndMissingValueInstanceSumOfWeights.get(attribute);
			description += "	Attribute: " + attribute.getName() + ": size = " + missingValueInstsSize + ", sum of wts = " + sumOfWts + newLine;
		}
		
		return description;
	}

	private String valueAndDoublePairDescription(Iterator<Entry<Value, Double>> iterator, String newLine, int numIndents){
		String description = "";
		String indents = "";
		for(int i = 0; i < numIndents; i++){
			indents += "	";
		}
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			String valName = entry.getKey().getName();
			if(valName != null){
				description += indents + valName + ": ";
				description += entry.getValue() + newLine;
			}
		}
		description += newLine;
		return description;
	}

	private String attributeAndValueDoublePairDescription(Iterator<Entry<Attribute, Map<Value, Double>>> iterator, String newLine){
		String description = "";
		while(iterator.hasNext()){
			Entry<Attribute, Map<Value, Double>> entry = iterator.next();
			description += "	Attribute: " + entry.getKey().getName() + newLine;
			description += valueAndDoublePairDescription(entry.getValue().entrySet().iterator(), newLine, 2);
		}
		description += newLine;
		return description;
	}

	public String getDistrbutionDesc() {

		Double numErrors = getErrorOnDistribution();		
		return  MathUtils.roundDouble(sumOfWeights, 2)
				+ (Double.compare(numErrors, 0.0) > 0 ? "/" + MathUtils.roundDouble(numErrors, 2) : "");
	}
}
