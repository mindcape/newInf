package com.ipsg.inferneon.spark.commonfunctions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.spark.api.java.JavaRDD;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Value;
import com.ipsg.inferneon.spark.utils.SparkUtils;

public class DistributionWrapper implements Serializable{

	private List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCountList = new ArrayList<Map<Value,Map<Value,Double>>>(); 

	private Map<Attribute, Map<Value, Double>> attributeAndTargetClassCounts = new HashMap<Attribute, Map<Value,Double>>();

	private Map<Attribute, Long> attributeAndNonMissingValueCount = new HashMap<Attribute, Long>();

	private Map<Attribute, Map<Value, Double>> attributeValueCounts = new HashMap<Attribute, Map<Value,Double>>();

	//private Map<Attribute, JavaRDD<Instance>> attributeAndMissingValueInstances = new HashMap<Attribute, JavaRDD<Instance>>();

	//private JavaRDD<Instance> instancesWithMissingvalues;

	private Map<Value, Double> totalTargetCounts = new HashMap<Value, Double>();

	private Double sumOfWeights = 0.0;
	
	private List<Attribute> attributes;
	
	private int classIndex;
	
	public DistributionWrapper(List<Attribute> attributes, int classIndex){
		
		System.out.println("Hashcode of first attribute in DRIVER CODE (?) in  distribution wrapper constructor: " + attributes.get(0).hashCode());
		
		this.attributes = attributes;
		this.classIndex = classIndex;
	}
		
	public void addToWeight(double weight){
		this.sumOfWeights += weight;
	}

	public void addToTargetValueCounts(Value targetClassValue, double instanceWeight) {
		System.out.println("ADDING TO TOTAL TARGET VALUE COUNTS");
		Double currentValue = totalTargetCounts.get(targetClassValue);
		if(currentValue == null){
			totalTargetCounts.put(targetClassValue, instanceWeight);
		}
		else{
			totalTargetCounts.put(targetClassValue, instanceWeight + currentValue);
		}		
	}

//	public void addToMissingInstances(Attribute attribute, Instance instance) {
//		
//		JavaRDD<Instance> newInsRDD = SparkUtils.createNewRDDBasedOnAInstance(instance);
//		
//		JavaRDD<Instance> missingValueInstances = attributeAndMissingValueInstances.get(attribute);
//		if(missingValueInstances == null){
//			missingValueInstances = newInsRDD;
//		}
//		else{
//			missingValueInstances = missingValueInstances.union(newInsRDD);
//		}
//		attributeAndMissingValueInstances.put(attribute, missingValueInstances);
//		
//		JavaRDD<Instance> newInsRDDForInsWithMissingVals = SparkUtils.createNewRDDBasedOnAInstance(instance);
//		if(instancesWithMissingvalues == null){
//			instancesWithMissingvalues = newInsRDDForInsWithMissingVals;
//		}
//		else{
//			instancesWithMissingvalues = instancesWithMissingvalues.union(newInsRDDForInsWithMissingVals);
//		}
//	}

	public void setValueAndTargetClassCount(int attributeIndex, Value value, Instance instance, Value targetClassValue) {
		
		Map<Value, Map<Value,Double>> valueAndTargetClassCount = null;
		
		if(valueAndTargetClassCountList.size() <= attributeIndex){
			valueAndTargetClassCount = new HashMap<Value, Map<Value,Double>>();
			valueAndTargetClassCountList.add(attributeIndex, valueAndTargetClassCount);
		}
		else{
			valueAndTargetClassCount = valueAndTargetClassCountList.get(attributeIndex);
		}
		
		if(valueAndTargetClassCount == null){
			valueAndTargetClassCount = new HashMap<Value, Map<Value,Double>>();
			valueAndTargetClassCountList.add(attributeIndex, valueAndTargetClassCount);
		}
				
		Map<Value, Double> targetClassCount = valueAndTargetClassCount.get(value);
		if(targetClassCount == null){
			targetClassCount = new HashMap<Value, Double>();
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
	}

	public void updateAttributeAndTargetClassCounts(Attribute currentAttribute, Instance instance, Value targetClassValue) {
		
		Map<Value, Double> targetClassCountsForAttribute = attributeAndTargetClassCounts.get(currentAttribute);
		if(targetClassCountsForAttribute == null){
			targetClassCountsForAttribute = new HashMap<Value, Double>();
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
	}

	public void updateAttributeAndNonMissingValueCount(Attribute currentAttribute) {
		Long nonMissingValueCountForAttribute = attributeAndNonMissingValueCount.get(currentAttribute); 
		if(nonMissingValueCountForAttribute == null){
			attributeAndNonMissingValueCount.put(currentAttribute, new Long(1));
		}
		else{
			attributeAndNonMissingValueCount.put(currentAttribute, nonMissingValueCountForAttribute + 1);
		}
	}

	public void updateAttributeValueCounts(Value value, Instance instance, Attribute attribute) {
		// Update the attribute's value count
		Map<Value, Double> attrValCount = attributeValueCounts.get(attribute);
		if(attrValCount == null){
			attrValCount = new HashMap<Value, Double>();
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
		attributeValueCounts.put(attribute, attrValCount);
	}
		
	public void mergeTargetValueCounts(Map<Value, Double> otherTotalTargetCounts) {
		if(totalTargetCounts.size() == 0){
			totalTargetCounts = otherTotalTargetCounts;
			return;
		}
		
		Iterator<Entry<Value, Double>> iterator = totalTargetCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Value targetValue = entry.getKey();
			Double currentCount = entry.getValue();
			
			Double countFromOther = otherTotalTargetCounts.get(targetValue);
			if(countFromOther != null){
				totalTargetCounts.put(targetValue, currentCount + countFromOther);
			}			
		}
	}

	public void mergeValueAndTargetClassCount(List<Map<Value, Map<Value, Double>>> otherValueAndTargetClassCountList) {
		
		if(valueAndTargetClassCountList.size() == 0){
			valueAndTargetClassCountList = otherValueAndTargetClassCountList;
			return;
		}
		
		int attributeCount = 0;
		for(Map<Value, Map<Value, Double>> valueAndTargetClassCount : valueAndTargetClassCountList){			
			Map<Value, Map<Value, Double>> otherValueAndTargetClassCount = otherValueAndTargetClassCountList.get(attributeCount);
			
			if(otherValueAndTargetClassCount != null){
				Iterator<Entry<Value, Map<Value, Double>>> iterator = valueAndTargetClassCount.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<Value, Map<Value, Double>> entry = iterator.next();
					Value value = entry.getKey();
					Map<Value, Double> targetCounts = entry.getValue();
					
					Map<Value, Double> otherTargetCounts = otherValueAndTargetClassCount.get(value);
					if(otherTargetCounts != null){
						Iterator<Entry<Value, Double>> iterator2 = targetCounts.entrySet().iterator();
						while(iterator2.hasNext()){
							Entry<Value, Double> entry2 = iterator2.next();
							Value attrValue = entry2.getKey();
							Double targetCount = entry2.getValue();
							
							Double otherTargetCount = otherTargetCounts.get(attrValue);
							if(otherTargetCount != null){
								targetCounts.put(attrValue, targetCount + otherTargetCount);
							}
							
						}
					}					
				}
			}
						
			attributeCount++;
		}		
	}

	public void mergeAttributeAndTargetClassCounts(Map<Attribute, Map<Value, Double>> otherAttributeAndTargetClassCounts) {
		
		if(attributeAndTargetClassCounts.size() == 0){
			attributeAndTargetClassCounts = otherAttributeAndTargetClassCounts;
			return;
		}
		
		Iterator<Entry<Attribute, Map<Value, Double>>> iterator = attributeAndTargetClassCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Attribute, Map<Value, Double>> entry = iterator.next();
			Attribute attribute = entry.getKey();
			Map<Value, Double> targetCounts = entry.getValue();
			
			Map<Value, Double> otherTargetCounts = otherAttributeAndTargetClassCounts.get(attribute);
			if(otherTargetCounts != null){
				Iterator<Entry<Value, Double>> iterator2 = targetCounts.entrySet().iterator();
				while(iterator2.hasNext()){
					Entry<Value, Double> entry2 = iterator2.next();
					Value value = entry2.getKey();
					Double count = entry2.getValue();
					
					Double otherCount = otherTargetCounts.get(value);
					if(otherCount != null){
						targetCounts.put(value, count + otherCount);
					}
				}				
			}
		}		
	}

	public void mergeAttributeAndNonMissingValueCount(Map<Attribute, Long> otherAttributeAndNonMissingValueCount) {
		if(attributeAndNonMissingValueCount.size() == 0){
			attributeAndNonMissingValueCount = otherAttributeAndNonMissingValueCount;
			return;
		}
		
		Iterator<Entry<Attribute, Long>> iterator = attributeAndNonMissingValueCount.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Attribute, Long> entry = iterator.next();
			Attribute attribute = entry.getKey();
			Long count = entry.getValue();
			
			Long otherCount = otherAttributeAndNonMissingValueCount.get(attribute);
			if(otherCount != null){
				attributeAndNonMissingValueCount.put(attribute, count + otherCount);
			}			
		}		
	}

	public void mergeAttributeValueCounts(Map<Attribute, Map<Value, Double>> otherAttributeValueCounts) {
		if(attributeValueCounts.size() == 0){
			attributeValueCounts = otherAttributeValueCounts;
			return;
		}
		
		Iterator<Entry<Attribute, Map<Value, Double>>> iterator = attributeValueCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Attribute, Map<Value, Double>> entry = iterator.next();
			Attribute attribute = entry.getKey();
			Map<Value, Double> counts = entry.getValue();
			
			Map<Value, Double> otherCounts = otherAttributeValueCounts.get(attribute);
			if(otherCounts != null){
				Iterator<Entry<Value, Double>> iterator2 = counts.entrySet().iterator();
				while(iterator2.hasNext()){
					Entry<Value, Double> entry2 = iterator2.next();
					Value value = entry2.getKey();
					Double count = entry2.getValue();
					
					Double otherCount = otherCounts.get(value);
					if(otherCount != null){
						counts.put(value, count + otherCount);
					}
				}				
			}
		}			
	}
	
	public Double getSumOfWeights() {
		return sumOfWeights;
	}	
	
	public Map<Value, Double> getTotalTargetCounts() {
		return totalTargetCounts;
	}
	
	public List<Map<Value, Map<Value, Double>>> getValueAndTargetClassCountList() {
		return valueAndTargetClassCountList;
	}

	public Map<Attribute, Map<Value, Double>> getAttributeAndTargetClassCounts() {
		return attributeAndTargetClassCounts;
	}

	public Map<Attribute, Long> getAttributeAndNonMissingValueCount() {
		return attributeAndNonMissingValueCount;
	}

	public Map<Attribute, Map<Value, Double>> getAttributeValueCounts() {
		return attributeValueCounts;
	}

//	public JavaRDD<Instance> getInstancesWithMissingvalues() {
//		return instancesWithMissingvalues;
//	}
}
