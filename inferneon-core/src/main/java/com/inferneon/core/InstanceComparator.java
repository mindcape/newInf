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

		int attrIndex = attributes.indexOf(attribute);
		Value value1 = instance1.getValue(attrIndex);
		Value value2 = instance2.getValue(attrIndex);
		
		ValueComparator valueComparator = new ValueComparator();
		return valueComparator.compare(value1, value2);
		
	}
}
