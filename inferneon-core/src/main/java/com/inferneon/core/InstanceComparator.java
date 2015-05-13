package com.inferneon.core;

import java.util.Comparator;
import java.util.List;

import com.inferneon.core.Value.ValueType;

public class InstanceComparator implements Comparator<Instance>{

	private Attribute attribute;
	private List<Attribute> attributes;	

	public InstanceComparator(Attribute attribute,List<Attribute> attributes) {
		this.attribute = attribute;
		this.attributes = attributes;
	}


	@Override
	public int compare(Instance instance1, Instance instance2) {

		Value value1 = null;
		Value value2 = null;
		if(Attribute.Type.NOMINAL == attribute.getType()){
			value1 = instance1.attributeValue(attribute);
			value2 = instance2.attributeValue(attribute);

			String name1 = value1.getName(); 
			String name2 = value2.getName();			
			return name1.compareTo(name2);			
		}
		
		int attrIndex = attributes.indexOf(attribute);
		value1 = instance1.getValue(attrIndex);
		Value.ValueType valueType1 = value1.getType();
		value2 = instance2.getValue(attrIndex);
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
