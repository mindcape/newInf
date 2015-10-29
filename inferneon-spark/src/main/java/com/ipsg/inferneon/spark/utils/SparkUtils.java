package com.ipsg.inferneon.spark.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.inferneon.core.Instance;
import com.ipsg.inferneon.spark.commonfunctions.AddDistributionWrappers;
import com.ipsg.inferneon.spark.commonfunctions.AddDoubleToInstanceWeight;
import com.ipsg.inferneon.spark.commonfunctions.AddInstanceWeights;
import com.ipsg.inferneon.spark.commonfunctions.WeightSumWrapper;

public class SparkUtils {
	
	public static JavaRDD<Instance> createNewRDDBasedOnAInstance(Instance instance){
		JavaSparkContext javaSparkContext = JavaSparkContextSingleton.getInstance();
		List<Instance> listForNewInstance = new ArrayList<Instance>();
		listForNewInstance.add(instance);
		JavaRDD<Instance> newInsRDD = javaSparkContext.parallelize(listForNewInstance);		
		
		return newInsRDD;
	}

	public static Double getSumOfWeights(JavaRDD<Instance> instances){
		WeightSumWrapper initial = new WeightSumWrapper(0.0);
		WeightSumWrapper total =  instances.aggregate(initial, new AddDoubleToInstanceWeight(), new AddInstanceWeights());
		
		return total.sumOfWeights();
	}	
}