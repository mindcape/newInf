package com.ipsg.inferneon.spark.commonfunctions;

import java.io.Serializable;

public class WeightSumWrapper implements Serializable{
	
	private Double weight;
	public WeightSumWrapper(Double weight){
		this.weight = weight;
	}
	
	public void addWeight(Double weight){
		this.weight += weight;
	}
	
	public Double sumOfWeights(){
		return weight;
	}
}
