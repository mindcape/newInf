package com.inferneon.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attribute {
	
	public enum Type {
		NOMINAL,
		NUMERIC
	}
	
	public enum NumericType {
		INTEGER,
		REAL
	}
	
	private Type type; 	
	private NumericType numericType;
	private String name;
	private List<Value> allNominalValues;	
	private Map<String, Value> nominalValues;
	
	public Attribute(String name, List<String> valueNames){
		this.name = name;
		type = Type.NOMINAL;		
		nominalValues = new HashMap<String, Value>();		
		allNominalValues = new ArrayList<>();
		
		for(String valueName : valueNames){
			Value val = new Value(valueName);
			nominalValues.put(valueName, val);
			allNominalValues.add(val);
		}		
	}
	
	public Attribute(String name, NumericType numericType){
		this.name = name;
		type = Type.NUMERIC;
		this.numericType = numericType;
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public Map<String, Value> getNominalValues() {
		return nominalValues;
	}


	public void setNominalValues(Map<String, Value> nominalValues) {
		this.nominalValues = nominalValues;
	}


	public void setAllValues(List<Value> allValues) {
		this.allNominalValues = allValues;
	}
	
	public List<Value> getAllValues(){
		return allNominalValues;
	}
	
	public Value getNominalValueForName(String name){
		return nominalValues.get(name);
	}
	
	public int getNumValues(){
		return allNominalValues.size();
	}
	
	@Override
	public String toString(){
		String description = name;
		
		if(type == Type.NOMINAL){
			description += " {";
			
			int valueCount = 0;
			for(Value value : allNominalValues){
				description += value.toString() + (valueCount++ < allNominalValues.size() -1 ? "," : "");
			}
			
			description += "}";
		}		
		else{
			description += " numeric";
		}
		return description;		
	}	
}
