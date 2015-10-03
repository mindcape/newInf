package com.inferneon.core.utils;


public class MathUtils {

	public static double roundDouble(double value, int afterDecimalPoint) {
		double mask = Math.pow(10.0, afterDecimalPoint);
		return (Math.round(value * mask)) / mask;
	}	
}
