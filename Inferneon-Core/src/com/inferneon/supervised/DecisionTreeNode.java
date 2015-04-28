package com.inferneon.supervised;

import java.util.Map;

import com.inferneon.core.Attribute;
import com.inferneon.core.Value;

public class DecisionTreeNode {
	
	private enum Type{ATTRIBUTE, VALUE};	
	
	private Type type;	
	private Attribute attribute;
	private Value value;
	private Double numInstances;	
	private Map<Value, Double> targetClassCounts;
	
	public DecisionTreeNode(Attribute attribute, Double numInstances, Map<Value, Double> targetClassCounts){
		this.attribute = attribute;
		type = Type.ATTRIBUTE;
		this.numInstances = numInstances;
		this.targetClassCounts = targetClassCounts;
	}
	
	public DecisionTreeNode(Value value, Double numInstances, Map<Value, Double> targetClassCounts){
		this.value = value;
		type = Type.VALUE;
		this.numInstances = numInstances;
		this.targetClassCounts = targetClassCounts;
	}

	public Attribute getAttribute() {
		return attribute;
	}	
	
	public Type getType() {
		return type;
	}

	public Value getValue() {
		return value;
	}
	
	public Double getNumInstances() {
		return numInstances;
	}

	public Map<Value, Double> getTargetClassCounts() {
		return targetClassCounts;
	}
	
	public String toString(){
		if(type == Type.ATTRIBUTE){
			return attribute.getName();
		}
		else if(type == Type.VALUE){
			// Is a leaf, also display the target class distribution
			return value.getName();
		}
		
		return "UNKNOWN";
	}
}
