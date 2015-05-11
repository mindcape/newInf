package com.inferneon.supervised;

import java.util.List;

import com.inferneon.core.Attribute;
import com.inferneon.core.Value;

public class BestAttributeSearchResult {
	
	private Attribute attribute;
	private int splittingPoint;
	private Value threshold;
	private List<Value> splitBeforeThreshold;
	private List<Value> splitAfterThreshold;
	private Double infoGain;	

	public Double getInfoGain() {
		return infoGain;
	}
	
	public BestAttributeSearchResult(Attribute attributeWithNominalValues){
		this.attribute = attributeWithNominalValues;
	}	

	public BestAttributeSearchResult(Attribute attributeWithContinuousValues, Double infoGain, int splittingPoint,  Value threshold,
									List<Value> splitBeforeThreshold,  List<Value> splitAfterThreshold){
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

	public List<Value> getSplitBeforeThreshold() {
		return splitBeforeThreshold;
	}

	public void setSplitBeforeThreshold(List<Value> splitBeforeThreshold) {
		this.splitBeforeThreshold = splitBeforeThreshold;
	}

	public List<Value> getSplitAfterThreshold() {
		return splitAfterThreshold;
	}

	public void setSplitAfterThreshold(List<Value> splitAfterThreshold) {
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
}
