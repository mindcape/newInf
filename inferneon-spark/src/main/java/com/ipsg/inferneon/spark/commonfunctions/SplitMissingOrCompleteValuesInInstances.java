package com.ipsg.inferneon.spark.commonfunctions;

import java.util.List;

import org.apache.spark.api.java.function.Function;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Value;

public class SplitMissingOrCompleteValuesInInstances implements Function<Instance, Boolean>{

	private List<Attribute> attributes;
	private boolean forMissing;

	public SplitMissingOrCompleteValuesInInstances(List<Attribute> attributes, boolean forMissing){
		this.attributes = attributes;
		this.forMissing = forMissing;
	}

	public Boolean call(Instance instance) throws Exception {

		List<Value> values = instance.getValues();
		for(int i = 0; i < values.size(); i++){
			Value value = instance.getValue(i);
			if(forMissing){
				if(value.getType() == Value.ValueType.MISSING){
					return true;
				}
			}
			else{
				if(value.getType() == Value.ValueType.MISSING){
					return false;
				}
			}
		}		
		
		if(forMissing){
			return false;
		}
		else{
			return true;
		}
	}
}
