package com.inferneon.core;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.inferneon.supervised.FrequencyCounts;

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

	protected Value getMaxTargetValue(Map<Value, Double> totalTargetCounts) {

		Double maxValue = 0.0;
		Value valueWithMaxInstances = null;
		Set<Entry<Value, Double>> entries = totalTargetCounts.entrySet();
		Iterator<Entry<Value, Double>> iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Value value = entry.getKey();
			Double numInstances = entry.getValue();

			if(Double.compare(numInstances, maxValue) > 0){
				maxValue = numInstances;
				valueWithMaxInstances = value;
			}
		}

		return valueWithMaxInstances;		
	}
	
	public abstract IInstances createInstances(List<Attribute> attributes, int classIndex, String sourceURI) throws Exception;	
	public abstract String getContextId();
	public abstract long size();
	public abstract List<Attribute> getAttributes();
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
	
	/**
	 * For a continuous-valued attribute, computes the total counts for each target value.
	 * 
	 */
	public abstract Map<Value, Double> cummulativeTargetClassCountForContinuousValuedAttribute(int attributeIndex, Map<Value, Map<Value, Double>> targetClassCount);
	
	public abstract void appendAll(IInstances other, double weightFactor);

}
