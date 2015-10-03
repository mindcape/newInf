package com.inferneon.core.exceptions;

public class IllegalOperationOnSigularMatrix extends Exception{
	private String message;

	public IllegalOperationOnSigularMatrix(String message){
		super(message);
		this.message = message;
	}	
}
