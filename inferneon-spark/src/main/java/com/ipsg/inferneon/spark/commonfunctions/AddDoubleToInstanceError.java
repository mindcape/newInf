package com.ipsg.inferneon.spark.commonfunctions;

import org.apache.spark.api.java.function.Function2;

import com.inferneon.core.Instance;

public class AddDoubleToInstanceError  
		implements Function2<AddErrorWrapper, Instance, AddErrorWrapper>{

	private int  numAttributes;
	private double[] parameters;
	private int classIndex;
	private int featureIndex;
	
	public AddDoubleToInstanceError(int numAttributes, double[] parameters, 
					int classIndex, int featureIndex){
		 this.numAttributes = numAttributes;
		 this.parameters = parameters;
		 this.classIndex = classIndex;
		 this.featureIndex = featureIndex;
	}
	
	public AddErrorWrapper call(AddErrorWrapper addErrorWrapper, Instance instance)
			throws Exception {
		
		double crossProduct = instance.dotProd(numAttributes, parameters, classIndex);
		double actualVal = instance.getValue(classIndex).getNumericValueAsDouble();
		
		double error = crossProduct - actualVal;
		error *= instance.getValue(featureIndex).getNumericValueAsDouble();
		addErrorWrapper.addError(error);
		return addErrorWrapper;
	}

}
