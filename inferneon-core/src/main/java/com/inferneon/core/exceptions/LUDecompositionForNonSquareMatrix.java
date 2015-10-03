package com.inferneon.core.exceptions;

public class LUDecompositionForNonSquareMatrix extends Exception{
	private String message;

	public LUDecompositionForNonSquareMatrix(String message){
		super(message);
		this.message = message;
	}	
}
