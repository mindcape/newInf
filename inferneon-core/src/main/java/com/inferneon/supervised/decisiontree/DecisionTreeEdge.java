package com.inferneon.supervised.decisiontree;

import org.jgraph.graph.DefaultEdge;

import com.inferneon.core.Value;

public class DecisionTreeEdge extends DefaultEdge{

	public enum Type {
		VALUE,
		PREDICATE
	}

	private Type type;
	private Value value;
	private PredicateEdge predicateEdge;

	public Type getType() {
		return type;
	}

	public PredicateEdge getPredicateEdge() {
		return predicateEdge;
	}

	public DecisionTreeEdge(Value value){
		this.type = Type.VALUE;
		this.value = value;
	}

	public DecisionTreeEdge(PredicateEdge predicateEdge){
		this.type = Type.PREDICATE;
		this.predicateEdge = predicateEdge;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}	

	@Override
	public String toString(){
		if(type == Type.VALUE){
			return value.getName();
		}
		else{
			return predicateEdge.toString();
		}
	}

	@Override
	public boolean equals(Object object){
		if(!(object instanceof DecisionTreeEdge)){
			return false;
		}

		DecisionTreeEdge other = (DecisionTreeEdge) object;
		
		if(type != other.getType()){
			return false;
		}
		
		if(type == Type.VALUE){
			if(this.value.equals(other.getValue())){
				return true;
			}
		}
		else{
			if(this.predicateEdge.toString().equals(other.getPredicateEdge().toString())){
				return true;
			}
		}
		
		return false;
	}	
}
