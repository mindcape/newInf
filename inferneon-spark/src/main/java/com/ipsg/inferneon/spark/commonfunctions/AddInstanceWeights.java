package com.ipsg.inferneon.spark.commonfunctions;

import org.apache.spark.api.java.function.Function2;

import com.inferneon.core.Instance;

public class AddInstanceWeights implements Function2<WeightSumWrapper, WeightSumWrapper, WeightSumWrapper>{

	public WeightSumWrapper call(WeightSumWrapper value1, WeightSumWrapper value2)
			throws Exception {
		value1.addWeight(value2.sumOfWeights());
		return value1;
	}

	
}
