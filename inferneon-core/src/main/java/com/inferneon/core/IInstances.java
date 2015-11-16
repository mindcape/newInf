package com.inferneon.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;
import com.inferneon.core.matrices.IMatrix;
import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.decisiontree.Impurity;
import com.inferneon.supervised.neuralnetwork.MultilayerNeuralNetwork;

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
	
	public Attribute getAttributeByName(String attributeName){
		for(Attribute attribute : attributes){
			if(attribute.getName().equals(attributeName)){
				return attribute;
			}
		}
		
		return null;
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
	
	/* Replaces any missing values for all attributes with the value that has the maximum occurrence for the attribute.
	 * Takes the value counts of each attribute as argument. If null, is passed, the value counts are computed.
	 */
	public abstract IInstances insertMissingNominalValuesWithModes(Map<Attribute, Map<Value, Double>> attributesAndValueCounts);
	
	/**
	 * Computes the mean based on the attribute that is passed. It is assumed that the attribute passed is 
	 * continuous-valued, else behavior is undefined. The computation is limited to the instances in the range defined
	 * by the start and end indexes.
	 */
	public abstract double mean(Attribute attribute, long startIndex, long endIndex);
	
	/**
	 * Computes the standard deviation based on the attribute that is passed. It is assumed that the attribute passed is 
	 * continuous-valued, else behavior is undefined. The computation is limited to the instances in the range defined
	 * by the start and end indexes.
	 */
	public abstract double standardDeviation(Attribute attribute, long startIndex, long endIndex);
	
	
	/** 
	 * Splits the instances based on the attribute passed. If an instance does not have a value
	 * for the attribute (missing value), that instance is ignored.
	 */
	public abstract Map<Value, IInstances> splitOnAttribute(Attribute attribute);
	
	public abstract IInstances removeAttribute(Attribute attribute);
	
	public abstract Double getMaxValueLesserThanOrEqualTo(double thresholdValue, Attribute attribute);
	
	public abstract Value valueOfAttributeAtInstance(long index, int attributeIndex);
	
	public abstract void appendAll(IInstances other, double weightFactor);
	
	public abstract IInstances convertNominalToSyntheticBinaryAttributes();
	
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

	public abstract double trainNeuralNetwork(MultilayerNeuralNetwork network, double learningRate, double momentum,int numEpoch, boolean isStochastic);
	
	/**
	 * Computes simple statistical values like sum of values, sum of squared values, variance, standard deviation
	 */
	public abstract Impurity initializeImpurity(Attribute attribute, long partitionIndex, int impurityOrder);
	
	/**
	 * Updates the impurity object that is passed as argument by iterating over the instances specified in the range for the given attribute
	 */
	
	public abstract Impurity updateImpurity(long startIndex, long endIndex, Impurity impurity);

	public abstract double partialDerivativeOfCostFunctionForLinearRegression(double[] parameters, int featureIndex);
	
	public abstract double[] gradientDescentForLinearRegression(
			double[] linearRegressionParams, int numIterations, Double stepSize);

	public abstract double[] stochasticGradientDescentForLinearRegression(
			double[] linearRegressionParams, int numIterations, Double stepSize); 
	
	/*
	 * updates the parameters for each instance in StochasticGradientDescent.
	 * It can be used for both stand-alone and spark.
	 */
	public double[] updateParams(Instance instance, double[] linearRegressionParams, Double stepSize) {
		classIndex = getClassIndex();
		double prod = instance.dotProd(instance, attributes.size(), linearRegressionParams, classIndex);
		double yReal = instance.getValue(classIndex).getNumericValueAsDouble();
		double z = yReal - prod;
		double factor = stepSize * z;
		
		 // Update coefficients for attributes
        int n = instance.getValues().size();
        for (int w = 0; w < n; w++) {
          if (w != classIndex) {
        	  linearRegressionParams[w] += factor * instance.getValue(w).getNumericValueAsDouble();
        	  System.out.println("updation of weights initialParams[w]->"+w +"->"+linearRegressionParams[w]);
          }
        }
		return linearRegressionParams;
		
	}
	
}
