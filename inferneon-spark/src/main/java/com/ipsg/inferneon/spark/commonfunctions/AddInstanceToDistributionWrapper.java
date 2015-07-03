package com.ipsg.inferneon.spark.commonfunctions;

import java.util.List;

import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Value;
import com.inferneon.core.Value.ValueType;

public class AddInstanceToDistributionWrapper implements Function2<Broadcast<DistributionWrapper>, Instance, Broadcast<DistributionWrapper>>{

	//private List<Attribute> attributes;
	private Broadcast<List<Attribute>> broadcastAttrs;

	//public AddInstanceToDistributionWrapper(List<Attribute> attributes){
	public AddInstanceToDistributionWrapper(Broadcast<List<Attribute>> broadcastAttrs){
		//this.attributes = attributes;
		this.broadcastAttrs = broadcastAttrs;
		List<Attribute> attributes = broadcastAttrs.getValue();
		System.out.println("Hashcode of first attribute in DRIVER CODE in constructor of AddInstanceToDistributionWrapper: " + attributes.get(0).hashCode());


	}

	public Broadcast<DistributionWrapper> call(Broadcast<DistributionWrapper> bcDw, Instance instance) throws Exception {

		List<Attribute> attributes = broadcastAttrs.getValue();

		DistributionWrapper distributionWrapper = bcDw.getValue();
		System.out.println("DistributionWr hascode = " + distributionWrapper.hashCode() + "Hashcode of first attribute in CLOSURE call() function of AddInstanceToDistributionWrapper: " + attributes.get(0).hashCode());

		int numAttributes = attributes.size();
		for(int attributeIndex = 0; attributeIndex < numAttributes -1; attributeIndex++){
			Value value = instance.getValue(attributeIndex);
			Value targetClassValue = instance.getValue(attributes.size() -1);
			Attribute currentAttribute = attributes.get(attributeIndex);

			if(attributeIndex == 0){
				double instanceWeight = instance.getWeight();
				distributionWrapper.addToWeight(instanceWeight);				
				distributionWrapper.addToTargetValueCounts(targetClassValue, instanceWeight);				
			}

			if(value.getType() == ValueType.MISSING){	
				//distributionWrapper.addToMissingInstances(currentAttribute, instance);
				continue;
			}
			distributionWrapper.setValueAndTargetClassCount(attributeIndex, value, instance, targetClassValue);			
			distributionWrapper.updateAttributeAndTargetClassCounts(currentAttribute, instance, targetClassValue);			
			distributionWrapper.updateAttributeAndNonMissingValueCount(currentAttribute);			
			distributionWrapper.updateAttributeValueCounts(value, instance, currentAttribute);
		}

		return bcDw;
	}
}
