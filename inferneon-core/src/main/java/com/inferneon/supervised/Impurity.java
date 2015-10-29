package com.inferneon.supervised;

import com.inferneon.core.Attribute;

public class Impurity {

	public static final int MIN_NUM_INSTANCES = 4;

	private Attribute attribute;
	private int impurityOrder;
	private double impurityValue;
	private double numInstances;                   // number of total instances 
	private double numOnLeftGroup;                  // number of instances in the left group 
	private double numOnRightGroup;                  // number of instances in the right group 
	private double sumOnLeftGroup;                  // sum of the left group  
	private double sumOnRightGroup;                  // sum of the right group 
	private double squaredSumOnLeftGroup;                 // squared sum of the left group 
	private double squaredSumOnRightGroup;                 // squared sum of the right group 
	private double standardDevLeftGroup;                 // standard deviation of the left group 
	private double standardDevRightGroup;                 // standard deviation of the right group 
	private double varianceOnLeftGroup;                  // variance of the left group 
	private double varianceOnRightGroup;                  // variance of the right group 
	private double standardDevOnSample;                  // overall standard deviation 
	private double varianceOnSample;                  // overall variance
	
	private double splitValue;

	public Impurity(Attribute attribute, int impurityOrder, double numInstances, double numOnLeftGroup, double numOnRightGroup, 
			double sumOnLeftGroup, double sumOnRightGroup, double squaredSumOnLeftGroup, double squaredSumOnRightGroup,
			double varianceOnSample, double standardDevOnSample){
		this.attribute = attribute;
		this.impurityOrder = impurityOrder;
		this.numInstances = numInstances;
		this.numOnLeftGroup = numOnLeftGroup;
		this.numOnRightGroup = numOnRightGroup;
		this.sumOnLeftGroup = sumOnLeftGroup;
		this.sumOnRightGroup = sumOnRightGroup;
		this.squaredSumOnLeftGroup = squaredSumOnLeftGroup;
		this.squaredSumOnRightGroup = squaredSumOnRightGroup;
		this.varianceOnSample = varianceOnSample;
		this.standardDevOnSample = standardDevOnSample;
		
		updateStats();
	}

	public double updateNext(double value) {

		numOnLeftGroup += 1;
		numOnRightGroup -= 1;
		sumOnLeftGroup += value;
		sumOnRightGroup -= value;
		squaredSumOnLeftGroup += value*value;
		squaredSumOnRightGroup -= value*value;
		
		updateStats();
		
		return impurityValue;
	}

	private void updateStats() {
		if(numOnLeftGroup <= 0.0){
			varianceOnLeftGroup =0.0;
			standardDevLeftGroup =0.0;
		}
		else {
			varianceOnLeftGroup = Math.abs((numOnLeftGroup * squaredSumOnLeftGroup -sumOnLeftGroup * sumOnLeftGroup) /
					(numOnLeftGroup * numOnLeftGroup));
			standardDevLeftGroup = Math.sqrt(varianceOnLeftGroup);
		}
		
		if(numOnRightGroup <= 0.0){
			varianceOnRightGroup = 0.0;
			standardDevRightGroup =0.0;
		}
		else {
			varianceOnRightGroup = Math.abs((numOnRightGroup * squaredSumOnRightGroup -sumOnRightGroup * sumOnRightGroup) /
					(numOnRightGroup * numOnRightGroup));
			standardDevRightGroup = Math.sqrt(varianceOnRightGroup);
		}

		double sampleValue = 0.0, leftValue = 0.0, rightValue = 0.0;
		if(impurityOrder == 1) {
			sampleValue = varianceOnSample; leftValue = varianceOnLeftGroup; rightValue = varianceOnRightGroup;
		} 
		else {
			sampleValue  = Math.pow(varianceOnSample, 1.0/impurityOrder);
			leftValue = Math.pow(varianceOnLeftGroup, 1.0/impurityOrder);
			rightValue = Math.pow(varianceOnRightGroup, 1.0/impurityOrder);
		}

		if(numOnLeftGroup <= 0.0 || numOnRightGroup <= 0.0){
			impurityValue = 0.0;
		}
		else { 
			impurityValue = sampleValue - (numOnLeftGroup/(double)numInstances)*leftValue - 
					  (numOnRightGroup/(double)numInstances)*rightValue;
		}
		
	}

	public double getNumInstances() {
		return numInstances;
	}

	public double getNumOnLeftGroup() {
		return numOnLeftGroup;
	}

	public double getNumOnRightGroup() {
		return numOnRightGroup;
	}

	public double getSumOnLeftGroup() {
		return sumOnLeftGroup;
	}

	public double getSumOnRightGroup() {
		return sumOnRightGroup;
	}

	public double getSquaredSumOnLeftGroup() {
		return squaredSumOnLeftGroup;
	}

	public double getSquaredSumOnRightGroup() {
		return squaredSumOnRightGroup;
	}

	public double getStandardDevLeftGroup() {
		return standardDevLeftGroup;
	}

	public double getStandardDevRightGroup() {
		return standardDevRightGroup;
	}

	public double getVarianceOnLeftGroup() {
		return varianceOnLeftGroup;
	}

	public double getVarianceOnRightGroup() {
		return varianceOnRightGroup;
	}

	public double getStandardDevOnSample() {
		return standardDevOnSample;
	}

	public double getVarianceOnSample() {
		return varianceOnSample;
	}

	public double getImpurityValue(){
		return impurityValue;
	}
	
	public Attribute getAttribute(){
		return attribute;
	}
	

	public double getSplitValue() {
		return splitValue;
	}

	public void setSplitValue(double splitValue) {
		this.splitValue = splitValue;
	}
	
	@Override
	public String toString(){
		StringBuffer description = new StringBuffer();

		description.append("Impurity values:\n");
		description.append("    Number of total instances:\t" + numInstances + "\n");
		description.append("    Splitting attribute:\t\t" + attribute.getName() + "\n");
		description.append("    Number of the instances in the left:\t" + numOnLeftGroup + "\n");
		description.append("    Number of the instances in the right:\t" + numOnRightGroup + "\n");
		description.append("    Sum of the left:\t\t\t" + sumOnLeftGroup + "\n");
		description.append("    Sum of the right:\t\t\t" + sumOnRightGroup + "\n");
		description.append("    Squared sum of the left:\t\t" + squaredSumOnLeftGroup + "\n"); 
		description.append("    Squared sum of the right:\t\t" + squaredSumOnRightGroup + "\n");
		description.append("    Standard deviation of the left:\t" + standardDevLeftGroup + "\n");
		description.append("    Standard deviation of the right:\t" + standardDevRightGroup + "\n");
		description.append("    Variance of the left:\t\t" + varianceOnLeftGroup + "\n");
		description.append("    Variance of the right:\t\t" + varianceOnRightGroup + "\n");
		description.append("    Overall standard deviation:\t\t" + standardDevOnSample + "\n");
		description.append("    Overall variance:\t\t\t" + varianceOnSample + "\n");
		description.append("    Impurity (order " + impurityOrder + "):\t\t" + impurityValue + "\n");

		return description.toString();
	}
	
	@Override
	public Impurity clone(){
		Impurity impurity = new Impurity(attribute, impurityOrder, numInstances, numOnLeftGroup, numOnRightGroup,
				sumOnLeftGroup, sumOnRightGroup, squaredSumOnLeftGroup, squaredSumOnRightGroup, varianceOnSample, standardDevOnSample);
		impurity.updateStats();
		
		return impurity;
	}
}
