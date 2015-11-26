package com.ipsg.inferneon.spark.commonfunctions;

import java.io.Serializable;

public class AddErrorWrapper implements Serializable{
	private Double error;
	
	public AddErrorWrapper(Double error){
		this.error = error;
	}
	
	public void addError(Double error){
		this.error += error;
	}
	
	public Double getError(){
		return error;
	}
}
