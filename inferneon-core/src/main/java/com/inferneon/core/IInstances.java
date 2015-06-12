package com.inferneon.core;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.inferneon.supervised.FrequencyCounts;

public abstract class IInstances {

	public enum Context{
		STAND_ALONE,
		SPARK,
		FLINK
	}

	public Context context;
	private List<Attribute> attributes;
	private int classIndex;

	public IInstances(Context context){
		this.context = context;
	}

	public void setAttributes(List<Attribute> attributes){
		this.attributes = attributes;
		classIndex = attributes.size() -1;   // Default
	}

	public List<Attribute> getAttributes(){
		return attributes;
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
	public abstract IInstances createInstances(InputStream inputStream, List<Attribute> attributes, int classIndex) throws Exception;
	
	public abstract long size();
	public abstract void addInstance(Instance instance);
	public abstract Double sumOfWeights();
	public abstract Double sumOfWeights(long startIndex, long endIndex);
	public abstract FrequencyCounts getFrequencyCounts();
	
	public abstract Long indexOfFirstInstanceWithMissingValueForAttribute(int attributeIndex);
	
	/** 
	 * Splits the instances based on the attribute passed. If an instance does not have a value
	 * for the attribute (missing value), that instance is ignored.
	 */
	public abstract Map<Value, IInstances> splitOnAttribute(Attribute attribute);
	
	public abstract IInstances removeAttribute(Attribute attribute);
	
	public abstract Double getMaxValueLesserThanOrEqualTo(double thresholdValue, Attribute attribute);
	
	public abstract void sort(Attribute attribute);
	
	public abstract IInstances getSubList(long start, long end); 
	
	public abstract Value valueOfAttributeAtInstance(long index, int attributeIndex);
	
	public abstract Map<Value, Double> getWeightedEntropyForValuesSubset(int attributeIndex, Map<Value, Map<Value, Double>> targetClassCount);
	
	public abstract void appendAll(IInstances other, double weightFactor);

}
