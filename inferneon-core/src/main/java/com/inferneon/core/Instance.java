package com.inferneon.core;

import java.io.Serializable;
import java.util.List;

public class Instance implements Serializable{
	private double[] m_AttValues;
	// Values for this instance. Matches with corresponding attributes
	private List<Value> values;

	// Indicates an approximate probability of this instance in a given split
	private double weight = 1.0;

	public Instance(List<Value> values){
		this.values = values;
	}

	public Instance(int numAttributes){
		m_AttValues = new double[numAttributes];
		for (int i = 0; i < m_AttValues.length; i++) {
			m_AttValues[i] = Double.NaN;;
		}
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

	public void setValue(int attIndex, double value) {
		 double[] newValues = new double[m_AttValues.length];
	    System.arraycopy(m_AttValues, 0, newValues, 0, m_AttValues.length);
		m_AttValues = newValues;
		m_AttValues[attIndex] = value;
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
	
	public double dotProd(Instance inst, int numAttributes, double[] parameters, int classIndex) {
		double prod = 0.0;
		for (int k=0; k <= numAttributes-1; k++){
			if(k == classIndex){
				continue;
			}
			prod += parameters[k] * inst.getValue(k).getNumericValueAsDouble(); 
		}
		return prod;
	}

}
