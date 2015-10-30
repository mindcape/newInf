package com.inferneon.supervised.linearregression;

public class LinearModel {

	private double[] parameters;	
	private long numInstances;
	private double aveClassalueOfInstances;
	
	public long getNumInstances() {
		return numInstances;
	}

	public double getAveClassalueOfInstances() {
		return aveClassalueOfInstances;
	}
	
	public LinearModel(long numInstances, double aveClassalueOfInstances){
		this.numInstances = numInstances;
		this.aveClassalueOfInstances = aveClassalueOfInstances;
	}
	
	public double[] getParameters(){
		return parameters;
	}
	
	@Override
	public String toString(){
		String description = "LM:";
		description += "(" + numInstances + " / " + aveClassalueOfInstances + ")";
		
		return description;
	}	
}
