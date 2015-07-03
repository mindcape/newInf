package com.ipsg.inferneon.spark.commonfunctions;

import org.apache.spark.api.java.function.Function;

import com.inferneon.core.Instance;
import com.inferneon.core.Value;

public class MissingInstancesForAttribute implements Function<Instance, Boolean> {
	
	private int attributeIndex;
	
	public MissingInstancesForAttribute(int attributeIndex){
		this.attributeIndex = attributeIndex;
	}
	
	public Boolean call(Instance instance) throws Exception {
		if(instance.getValue(attributeIndex).getType() == Value.ValueType.MISSING){
			return true;
		}
			
		return false;
	}

}
