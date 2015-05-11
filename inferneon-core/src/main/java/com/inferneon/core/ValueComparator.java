package com.inferneon.core;

import java.util.Comparator;

public class ValueComparator  implements Comparator<Value>{

	@Override
	public int compare(Value value1, Value value2) {
		Value.ValueType valueType = value1.getType();
		if(valueType == Value.ValueType.NOMINAL){
			String name1 = value1.getName(); 
			String name2 = value2.getName();			
			return name1.compareTo(name2);			
		}
		else if(valueType== Value.ValueType.REAL){
			Double val1 = value1.getReal();
			Double val2 = value2.getReal();
			return val1.compareTo(val2);
		}
		else{
			Long val1 = value1.getNumber();
			Long val2 = value2.getNumber();
			return val1.compareTo(val2);
		}
	}
}
