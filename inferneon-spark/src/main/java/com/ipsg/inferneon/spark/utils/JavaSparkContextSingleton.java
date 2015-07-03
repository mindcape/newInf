package com.ipsg.inferneon.spark.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class JavaSparkContextSingleton {

	private static JavaSparkContext javaSparkContext;
	
	private JavaSparkContextSingleton(){};
	
	public static JavaSparkContext getInstance(){
		if(javaSparkContext == null){
			SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Test");
			javaSparkContext = new JavaSparkContext(sparkConf);
		}
		
		return javaSparkContext;
	}	
}
