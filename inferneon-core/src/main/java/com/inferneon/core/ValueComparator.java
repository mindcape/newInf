package com.inferneon.core;

import java.io.Serializable;
import java.util.Comparator;

import com.inferneon.core.Value.ValueType;

public class ValueComparator  implements Comparator<Value>, Serializable{

	@Override
	public int compare(Value value1, Value value2) {
		if(value1.getType() == Value.ValueType.NOMINAL){
			String name1 = value1.getName(); 
			String name2 = value2.getName();			
			return name1.compareTo(name2);			
		}
		
		Value.ValueType valueType1 = value1.getType();
		Value.ValueType valueType2 = value2.getType();
		
		if(valueType1 == ValueType.MISSING){
			if(valueType2 == ValueType.MISSING){
				return 0;
			}
			else{
				return 1;
			}
		}
		else{
			if(valueType2 == ValueType.MISSING){
				return -1;
			}
		}
				
		if(valueType1== Value.ValueType.REAL){
			Double val1 = value1.getReal();
			Double val2 = value2.getReal();
			return val1.compareTo(val2);
		}
		else {
			Long val1 = value1.getNumber();
			Long val2 = value2.getNumber();
			return val1.compareTo(val2);
		}		
	}
}
