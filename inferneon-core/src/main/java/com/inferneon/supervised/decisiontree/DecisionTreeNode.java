package com.inferneon.supervised.decisiontree;

import com.inferneon.core.Attribute;
import com.inferneon.core.Value;
import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.linearregression.LinearModel;

public class DecisionTreeNode {
	
	protected enum Type{ATTRIBUTE, VALUE, IMPURITY, LINEAR_MODEL};	
	
	private Type type;	
	private Attribute attribute;
	private Value value;
	private LinearModel linearModel;
	private Impurity impurity;
	private double numInstances;	
	private FrequencyCounts frequencyCounts;

	public DecisionTreeNode(FrequencyCounts frequencyCounts, Attribute attribute){
		this.attribute = attribute;
		type = Type.ATTRIBUTE;
		this.numInstances = frequencyCounts.getSumOfWeights();
		this.frequencyCounts = frequencyCounts; 
	}
	
	public DecisionTreeNode(FrequencyCounts frequencyCounts, Value value){
		this.value = value;
		type = Type.VALUE;
		this.numInstances = frequencyCounts.getSumOfWeights();
		this.frequencyCounts = frequencyCounts;
	}
	
	public DecisionTreeNode(Impurity impurity, Attribute attribute){
		this.impurity = impurity;
		this.attribute = attribute;
		type = Type.IMPURITY;
		this.numInstances = impurity.getNumInstances();
	}
	
	public DecisionTreeNode(LinearModel linearModel){
		this.linearModel = linearModel;
		type = Type.LINEAR_MODEL;
		this.numInstances = linearModel.getNumInstances();
	}

	public FrequencyCounts getFrequencyCounts() {
		return frequencyCounts;
	}

	public void setFrequencyCounts(FrequencyCounts frequencyCounts) {
		this.frequencyCounts = frequencyCounts;
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
	
	public LinearModel getLinearModel(){
		return linearModel;
	}
	
	public Double getNumInstances() {
		return numInstances;
	}

	public boolean isLeaf() {
		return type == Type.VALUE || type == Type.LINEAR_MODEL;
	}

	public String toString(){
		if(type == Type.ATTRIBUTE || type == Type.IMPURITY){
			return attribute.getName();
		}
		else if(type == Type.VALUE || type == Type.LINEAR_MODEL){
			// Is a leaf, also display the target class distribution
			if(type == Type.VALUE){
				return value.getName();
			}
			else{
				return linearModel.toString();
			}
		}
		else if( type == Type.LINEAR_MODEL){
			return linearModel.toString();
		}
		
		return "UNKNOWN";		
	}
}
