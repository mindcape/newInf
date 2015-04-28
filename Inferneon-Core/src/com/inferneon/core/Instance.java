package com.inferneon.core;

import java.util.List;

public class Instance {

	// Values for this instance. Matches with corresponding attributes
	private List<Value> values;
	
	// Indicates an approximate probability of this instance in a given split
	private double weight = 1.0;
	
	public Instance(List<Value> values){
		this.values = values;
	}

	public Value getValue(int i){
		return values.get(i);
	}
	
	public List<Value> getValues(){
		return values;
	}
	
	public void setWeight(double weight){
		this.weight = weight;
	}

	public double getWeight(){
		return weight;
	}
	
	public Value attributeValue(Attribute attribute){

		List<Value> valuesForAttribute = attribute.getAllValues();

		for(Value value : values){		
			if(valuesForAttribute.contains(value)){
				return value;
			}
		}
		
		// TODO Log warning here: invalid attribute?
		return null;
	}
	
	@Override
	public String toString(){
		String description = "";
		int count = 0;
		for(Value value : values){
			description += value.toString() + (count < values.size() -1?  "," : "");
			count++;
		}
		
		if(Double.compare(weight, 1.0) != 0){
			description += "{" + weight + "}";
		}
		
		return description.trim();
	}	
}
