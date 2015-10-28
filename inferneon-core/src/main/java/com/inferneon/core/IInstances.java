package com.inferneon.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;
import com.inferneon.core.matrices.IMatrix;
import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.functions.MultilayerNeuralNetwork;

public abstract class IInstances implements Serializable{

	public enum Context{
		STAND_ALONE,
		SPARK,
		FLINK
	}

	public Context context;
	protected List<Attribute> attributes;
	protected int classIndex;

	public IInstances(Context context){
		this.context = context;
	}

	public void setAttributes(List<Attribute> attributes){
		this.attributes = attributes;
		classIndex = attributes.size() -1;   // Default
	}

	public int getClassIndex() {
		return classIndex;
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	public int numClasses(){
		Attribute classAttribute = attributes.get(classIndex);
		return classAttribute.getNumValues();
	}
	
	public abstract IInstances createInstances(List<Attribute> attributes, int classIndex, String sourceURI) throws Exception;	
	public abstract String getContextId();
	public abstract long size();
	public abstract List<Attribute> getAttributes();
	public abstract void addInstance(Instance instance);
	public abstract Double sumOfWeights();
	public abstract Double sumOfWeights(long startIndex, long endIndex);
	public abstract Map<Value, Double> getTargetClassCounts();
	public abstract FrequencyCounts getFrequencyCounts();
	
	//public abstract Long indexOfFirstInstanceWithMissingValueForAttribute(int attributeIndex);
	
	/** 
	 * Splits the instances based on the attribute passed. If an instance does not have a value
	 * for the attribute (missing value), that instance is ignored.
	 */
	public abstract Map<Value, IInstances> splitOnAttribute(Attribute attribute);
	
	public abstract IInstances removeAttribute(Attribute attribute);
	
	public abstract Double getMaxValueLesserThanOrEqualTo(double thresholdValue, Attribute attribute);
	
	public abstract Value valueOfAttributeAtInstance(long index, int attributeIndex);
	
	public abstract void appendAll(IInstances other, double weightFactor);
	
	/**
	 * This implementation should sort instances based on the values of the attribute passed. The attribute should be of numeric (or real) type. 
	 */
	public abstract void sort(Attribute attribute);	
	
	/**
	 * This implementation should return a subset of instances that are already sort based on an attribute through the sort() function. 
	 */
	public abstract IInstances getSubList(long start, long end); 
	
	/**
	 * This implementation returns the number of occurrences of the given value. The relevant attribute should already be known 
	 * to the implementation.
	 */
	public abstract long getNumOccurrencesOfValueInOrderedList(Value value);
	
	/**
	 * This implementation returns the threshold value when it split based on an numeric attribute. The relevant attribute should already be known 
	 * to the implementation.
	 * */	
	public abstract Value getThresholdValueOfSplitsInOrderedList();
	
	public abstract IMatrix matrix(long startRowIndex, long startColumnIndex, long endRowIndex, long endColumnIndex) throws MatrixElementIndexOutOfBounds;
	
	public abstract IMatrix[] matrixAndClassVector(boolean regularize);

	public abstract double trainNeuralNetwork(MultilayerNeuralNetwork network, double learningRate, boolean isStochastic);

}
