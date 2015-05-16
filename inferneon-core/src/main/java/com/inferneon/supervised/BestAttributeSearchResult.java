package com.inferneon.supervised;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;

/**
 * Represents the attribute that is "best" to split on. If the attribute is a continuous value,
 * this class also has information of the splits before and after the splitting point.
 */

public class BestAttributeSearchResult {
	
	private Attribute attribute;
	private int splittingPoint;
	private Value threshold;
	private Instances splitBeforeThreshold;
	private Instances splitAfterThreshold;
	private Double infoGain;
	private Double gainRatio;	
	
	public BestAttributeSearchResult(Attribute attributeWithNominalValues, Double infoGain){
		this.attribute = attributeWithNominalValues;
		this.infoGain = infoGain;
	}	

	public BestAttributeSearchResult(Attribute attributeWithContinuousValues, Double infoGain, int splittingPoint,  Value threshold,
			Instances splitBeforeThreshold,  Instances splitAfterThreshold){
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

	public Instances getSplitBeforeThreshold() {
		return splitBeforeThreshold;
	}

	public void setSplitBeforeThreshold(Instances splitBeforeThreshold) {
		this.splitBeforeThreshold = splitBeforeThreshold;
	}

	public Instances getSplitAfterThreshold() {
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
	
	public int getSplittingPoint() {
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
