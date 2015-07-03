package com.ipsg.inferneon.spark.commonfunctions;

import org.apache.spark.api.java.function.Function2;

import com.inferneon.core.Instance;

public class AddDoubleToInstanceWeight implements Function2<WeightSumWrapper, Instance, WeightSumWrapper>{

	public WeightSumWrapper call(WeightSumWrapper weightSumWrapper, Instance instance)
			throws Exception {
		weightSumWrapper.addWeight(instance.getWeight());
		return weightSumWrapper;
	}
}
