package com.inferneon.supervised;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;

/**
 * Represents the attribute that is "best" to split on. If the attribute is a continuous value,
 * this class also has information of the splits before and after the splitting point.
 */

public class BestAttributeSearchResult {
	
	private Attribute attribute;
	private long splittingPoint;
	private Value threshold;
	private IInstances splitBeforeThreshold;
	private IInstances splitAfterThreshold;
	private Double infoGain;
	private Double gainRatio;	
	
	public BestAttributeSearchResult(Attribute attributeWithNominalValues, Double infoGain){
		this.attribute = attributeWithNominalValues;
		this.infoGain = infoGain;
	}	

	public BestAttributeSearchResult(Attribute attributeWithContinuousValues, Double infoGain, long splittingPoint,  Value threshold,
			IInstances splitBeforeThreshold,  IInstances splitAfterThreshold){
		this.attribute = attributeWithContinuousValues;
		this.infoGain = infoGain;
		this.splittingPoint = splittingPoint;
		this.threshold = threshold;
		this.splitBeforeThreshold = splitBeforeThreshold;
		this.splitAfterThreshold = splitAfterThreshold;
	}
	
	public Value getThreshold() {
		return threshold;
	}

	public void setThreshold(Value threshold) {
		this.threshold = threshold;
	}

	public IInstances getSplitBeforeThreshold() {
		return splitBeforeThreshold;
	}

	public void setSplitBeforeThreshold(Instances splitBeforeThreshold) {
		this.splitBeforeThreshold = splitBeforeThreshold;
	}

	public IInstances getSplitAfterThreshold() {
		return splitAfterThreshold;
	}

	public void setSplitAfterThreshold(Instances splitAfterThreshold) {
		this.splitAfterThreshold = splitAfterThreshold;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}
	
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	
	public long getSplittingPoint() {
		return splittingPoint;
	}
	
	public void setSplittingPoint(int splittingPoint) {
		this.splittingPoint = splittingPoint;
	}	
	
	public double getInfoGain(){
		return infoGain;		
	}

	public void setGainRatio(double gainRatio) {
		this.gainRatio = gainRatio;
	}
	
	public Double getGainRatio() {
		return gainRatio;
	}
}
