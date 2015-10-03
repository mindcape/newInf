package com.inferneon.core.exceptions;

public class DeterminantForNonSquareMatrix extends Exception{
	private String message;

	public DeterminantForNonSquareMatrix(String message){
		super(message);
		this.message = message;
	}	
}
