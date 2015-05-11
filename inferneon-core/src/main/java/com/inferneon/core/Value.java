package com.inferneon.core;

public class Value{
	
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
	
	public static boolean valuesAreIdentical(Value value1, Value value2){
		
		if(value1 == value2){
			return true;
		}
		
		ValueType type = value1.getType();
		if(type != value2.getType()){
			return false;
		}
		
		if(type == ValueType.NOMINAL){
			if(!value1.getName().equals(value2.getName())){
				return false;
			}
		}
		else if(type == ValueType.NUMERIC){
			if(value1.getNumber() != value2.getNumber()){
				return false;
			}
		}
		else if(type == ValueType.REAL){
			if(Double.compare(value1.getReal(), value2.getReal()) != 0){
				return false;
			}
		}
		
		return true;
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
}
