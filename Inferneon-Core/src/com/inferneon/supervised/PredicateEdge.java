package com.inferneon.supervised;

import com.inferneon.core.Attribute;
import com.inferneon.core.Value;
import com.inferneon.core.Value.ValueType;

public class PredicateEdge {
	
	private Attribute attribute;
	private Value thresholdValue;
	private boolean isLesserThanThreshold;

	public PredicateEdge(Attribute attribute, Value thresholdValue, boolean isLesserThanThreshold){
		this.attribute = attribute;
		this.thresholdValue = thresholdValue;
		this.isLesserThanThreshold = isLesserThanThreshold;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}

	public Value getThresholdValue() {
		return thresholdValue;
	}
	

	public boolean isLesserThanThreshold() {
		return isLesserThanThreshold;
	}
	
	@Override
	public String toString(){
		String description = isLesserThanThreshold ? "<= " : "> ";
		description += thresholdValue;
		
		return description;
	}

	public boolean test(Value value) {
		Value.ValueType type = value.getType();
		if(type == ValueType.NUMERIC){
			Double number = value.getNumericValueAsDouble();
			double thresholdNumber = thresholdValue.getNumericValueAsDouble();
			if(isLesserThanThreshold){
				if(number.compareTo(thresholdNumber) <= 0){
					return true;
				}
			}
			else{
				if(number.compareTo(thresholdNumber) >= 0){
					return true;
				}
			}
		}
//		else{
//			Double real = value.getReal();
//			Double thresholdReal = thresholdValue.getReal();
//			if(isLesserThanThreshold){
//				if(real.compareTo(thresholdReal) <= 0){
//					return true;
//				}
//			}
//			else{
//				if(real.compareTo(thresholdReal) >= 0){
//					return true;
//				}
//			}
//		}
		
		return false;		
	}
}
