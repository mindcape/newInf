package com.ipsg.inferneon.spark.commonfunctions;

import org.apache.spark.api.java.function.Function2;

public class AddInstanceErrors implements Function2<AddErrorWrapper, AddErrorWrapper, AddErrorWrapper>{

	public AddErrorWrapper call(AddErrorWrapper value1, AddErrorWrapper value2)
			throws Exception {
		value1.addError(value2.getError());
		return value1;
	}
}
