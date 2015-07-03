package com.ipsg.inferneon.spark.commonfunctions;

import java.util.Iterator;
import java.util.Map;

import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;

import com.inferneon.core.Attribute;
import com.inferneon.core.Value;

public class AddDistributionWrappers implements Function2<Broadcast<DistributionWrapper>, Broadcast<DistributionWrapper>, Broadcast<DistributionWrapper>>{

	public Broadcast<DistributionWrapper> call(Broadcast<DistributionWrapper> bcdw1, Broadcast<DistributionWrapper> bcdw2) throws Exception {

		DistributionWrapper dw1 = bcdw1.getValue();
		DistributionWrapper dw2 = bcdw2.getValue();
		dw1.addToWeight(dw2.getSumOfWeights());				
		dw1.mergeTargetValueCounts(dw2.getTotalTargetCounts());		
		
		//dw1.mergeMissingValueInstances(dw2.getAttributeAndMissingValueInstances());
		dw1.mergeValueAndTargetClassCount(dw2.getValueAndTargetClassCountList());	
		
		Map<Attribute, Map<Value, Double>> attributeAndTargetClassCounts = dw2.getAttributeAndTargetClassCounts();

		Iterator<Attribute> iterator = attributeAndTargetClassCounts.keySet().iterator();
		while(iterator.hasNext()){
			Attribute atr = iterator.next();
			if(atr.getName().equals("Outlook")){
				System.out.println("*******dw2 hashcode = " + dw2.hashCode() + "Hashcode of first attribute in WOKER ADDDISTRIBUTIONS : " 
							+ atr.hashCode());
			}
		}
		
		dw1.mergeAttributeAndTargetClassCounts(attributeAndTargetClassCounts);			
		dw1.mergeAttributeAndNonMissingValueCount(dw2.getAttributeAndNonMissingValueCount());			
		dw1.mergeAttributeValueCounts(dw2.getAttributeValueCounts());

		return bcdw1;
	}	
}
