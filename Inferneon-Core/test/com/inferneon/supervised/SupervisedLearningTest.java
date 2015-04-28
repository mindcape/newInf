package com.inferneon.supervised;

import java.util.ArrayList;
import java.util.List;

import com.inferneon.core.Attribute;
import com.inferneon.core.Value;

public class SupervisedLearningTest {

	protected List<Attribute> createAttributesWithNominalValues(
			List<String> attrNames, int[] lengths,
			List<String> attrNominalValues) {

		List<Attribute> attributes = new ArrayList<>();
		int count = 0, startIndex = 0;
		for(String attrName : attrNames){

			List<String> namesForAttr = attrNominalValues.subList(startIndex, startIndex + lengths[count]);

			startIndex = startIndex + lengths[count];

			Attribute attr = new Attribute(attrName, namesForAttr);
			attributes.add(attr);

			count++;

		}		
		return attributes;		
	}

	protected List<Attribute> createAttributesWithContinuousValues(Attribute.NumericType numericType, 
			List<String> continuousValuedAttributeNames){
		List<Attribute> attributes = new ArrayList<>();

		for(String attrName : continuousValuedAttributeNames){
			Attribute attr = new Attribute(attrName, numericType);
			attributes.add(attr);
		}

		return attributes;
	}

	protected List<Value> getValueListForTestInstance(List<Attribute> attributes, String ... strVals) {
		int numAttrs = attributes.size();

		List<Value> vals = new ArrayList<>(numAttrs);

		for(int i = 0 ; i < numAttrs -1; i++){
			Attribute attr = attributes.get(i);
			Value value = null;
			if(attr.getType() == Attribute.Type.NOMINAL){
				value =  attr.getNominalValueForName(strVals[i]);
			}
			else{
				try{		
					Long longVal = Long.valueOf(strVals[i]);
					value = new Value(longVal);
				}
				catch(NumberFormatException nfe1){
					try{						
						Double doubleValue = Double.valueOf(strVals[i]);
						value = new Value(doubleValue);		
					}
					catch(NumberFormatException nfe2){
						// TODO Log warning here.
					}
				}
			}
			vals.add(value);
		}

		vals.add(null);		
		return vals;		
	}	
}
