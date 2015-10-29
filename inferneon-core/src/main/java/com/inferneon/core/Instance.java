package com.inferneon.core;

import java.io.Serializable;
import java.util.List;

public class Instance implements Serializable{

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

	public boolean hasMissingValues() {
		for(Value value : values){
			if(value.getType() == Value.ValueType.MISSING){
				return true;
			}
		}
		return false;
	}	
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Instance)){
			return false;
		}
		
		Instance other = (Instance) obj;
		List<Value> valuesOfOther = other.getValues();
		if(values.size() != valuesOfOther.size()){
			return false;
		}
		
		int valueCount = 0;
		for(Value value : values){
			Value valOfOther = valuesOfOther.get(valueCount);
			if(!(value.equals(valOfOther))){
				return false;
			}
			valueCount++;
		}
		
		return true;
	}		
}
