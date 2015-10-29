package com.inferneon.supervised;

import java.util.Map;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;

public class DecisionTreeNode {
	
	private enum Type{ATTRIBUTE, VALUE, IMPURITY};	
	
	private Type type;	
	private Attribute attribute;
	private Value value;
	private Impurity impurity;
	private Double numInstances;	
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
	
	public Double getNumInstances() {
		return numInstances;
	}

	public boolean isLeaf() {
		return type == Type.VALUE;
	}

	public String toString(){
		if(type == Type.ATTRIBUTE || type == Type.IMPURITY){
			return attribute.getName();
		}
		else if(type == Type.VALUE){
			// Is a leaf, also display the target class distribution
			return value.getName();
		}
		
		return "UNKNOWN";
		
	}
}
