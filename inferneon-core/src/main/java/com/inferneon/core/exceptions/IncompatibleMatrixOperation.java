package com.inferneon.core.exceptions;

public class IncompatibleMatrixOperation extends Exception {

	private String message;

	public IncompatibleMatrixOperation(String message){

		super(message);
	}	

	public IncompatibleMatrixOperation(String message, long m1, long n1, long m2, long n2){

		super(message);
		this.message = message;
	}	
}
