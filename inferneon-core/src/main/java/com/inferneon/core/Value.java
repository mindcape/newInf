package com.inferneon.core;

import java.io.Serializable;

public class Value implements Serializable{
	
	private String name;
	private Double real;
	private Long number;
	
	public enum ValueType {
		NOMINAL,
		NUMERIC,
		REAL, 
		MISSING
	}
	
	private ValueType type;
	
	public ValueType getType() {
		return type;
	}
	
	public Value(){
		type = ValueType.MISSING;
	}
	
	public Value(String name){
		type = ValueType.NOMINAL;
		this.name = name;
	}

	public Value(Double real){
		type = ValueType.REAL;
		this.real = real;
	}
	
	public Value(Long number){
		type = ValueType.NUMERIC;
		this.number = number;
	}
	
	public String getName() {
		return name;
	}
	
	public Double getReal() {
		return real;
	}
	
	public Long getNumber() {
		return number;
	}
	
	@Override
	public String toString(){
		if(type == ValueType.NOMINAL){
			return name;
		}
		else if(type == ValueType.NUMERIC){
			return "" +number;
		}
		else if(type == ValueType.REAL){
			return "" + real;
		}
		else{
			return "?";
		}
	}
	
	@Override
	public  boolean equals(Object otherObject){
		
		if(!(otherObject instanceof Value)){
			return false;
		}
		
		Value value2 = (Value) otherObject;
		
		if(this == value2){
			return true;
		}
		
		if(type != value2.getType()){
			return false;
		}
		
		if(type == ValueType.NOMINAL){
			if(!name.equals(value2.getName())){
				return false;
			}
		}
		else if(type == ValueType.NUMERIC){
			if(getNumber().longValue() != value2.getNumber().longValue()){
				return false;
			}
		}
		else if(type == ValueType.REAL){
			if(Double.compare(getReal(), value2.getReal()) != 0){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode(){
		int hash = 3;
		
		String valRepresentation = "";
		if(type == ValueType.NOMINAL){
			valRepresentation = name;
		}
		else if(type == ValueType.NUMERIC){
			valRepresentation = "" + getNumber();
		}
		else if(type == ValueType.REAL){
			valRepresentation = "" + getReal();
		}
		
		hash = 7 * hash + valRepresentation.hashCode();
		return hash;
	}
	
	public Double getNumericValueAsDouble(){
		if(type == ValueType.NUMERIC){
			double num = (double) number;
			return num;
		}
		else if(type == ValueType.REAL){
			return real;
		}
		return null;
	}	
	
	public boolean isLesserThan(Value otherValue){
		if(type == ValueType.NUMERIC){
			long thisNum = getNumber().longValue();
			long otherNum = otherValue.getNumber().longValue();
			if(thisNum < otherNum){
				return true;
			}
		}
		else if(type == ValueType.REAL){
			if(Double.compare(getReal(), otherValue.getReal()) < 0){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isGreaterThan(Value otherValue){
		return !this.equals(otherValue) &&  !isLesserThan(otherValue);
	}
	
}
