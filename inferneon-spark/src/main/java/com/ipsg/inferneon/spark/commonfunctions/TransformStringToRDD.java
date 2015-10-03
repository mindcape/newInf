package com.ipsg.inferneon.spark.commonfunctions;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.Function;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Value;
import com.inferneon.core.exceptions.InvalidDataException;

public class TransformStringToRDD implements Function<String, Instance>{

	private List<Attribute> attributes;
	private boolean discardFirstRow;

	public TransformStringToRDD(List<Attribute> attributes, boolean discardFirstRow){
		this.attributes = attributes;
		this.discardFirstRow = discardFirstRow;
	}

	public Instance call(String csvInstance) throws Exception {
		
		if(discardFirstRow){
			//TODO How to ignore this?
			// continue;?
		}

		int count = 0;
		String[] elements = csvInstance.split(",");
		int attrCount = 0;
		List<Value> valuesForInstance = new ArrayList<Value>();
		for(Attribute attribute : attributes){
			Value val = null;
			String strValue = elements[attrCount++].trim();
			if(strValue.equals("?")){
				val = new Value();
				valuesForInstance.add(val);
				continue;
			}

			Attribute.Type attributeType = attribute.getType();
			if(attributeType == Attribute.Type.NOMINAL){
				val = attribute.getNominalValueForName(strValue);
				if(val == null){
					throw new InvalidDataException("Unknown nominal value for attribute " + attribute.getName() + " with value " + strValue + " at row " + count);
				}
			}
			else if(attributeType == Attribute.Type.NUMERIC){
				try{
					Long number = Long.valueOf(strValue);
					val = new Value(number);
				}
				catch(NumberFormatException nfe1){
					try{
						Double realNumber = Double.valueOf(strValue);
						val = new Value(realNumber);
					}
					catch(NumberFormatException nfe2){		
						throw new InvalidDataException("Invalid value for numeric attribute " + attribute.getName() + " with value " + strValue + " at row " + count);
					}
				}

			}
			valuesForInstance.add(val);
		}

		Instance instance = new Instance(valuesForInstance);
		return instance;
	}

}
