package com.ipsg.inferneon.spark;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;

public class OrderedInstances extends SparkInstances {
	private Instance firstInstance;
	private Instance lastInstance;
	
	public OrderedInstances(List<Attribute> attributes, int classIndex, JavaRDD<Instance> rddInstances, 
			long size, double sumOfWts, Instance firstInstance, Instance lastInstance){
		super(attributes, classIndex, rddInstances, size, sumOfWts);
		this.firstInstance = firstInstance;
		this.lastInstance = lastInstance;
	}

	public Instance getFirstInstance() {
		return firstInstance;
	}

	public Instance getLastInstance() {
		return lastInstance;
	}
}
