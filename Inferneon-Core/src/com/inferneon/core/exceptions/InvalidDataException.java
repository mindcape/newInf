package com.inferneon.core.exceptions;

public class InvalidDataException extends Exception{

	private String message;
	
	public InvalidDataException(String message){
		super(message);
		this.message = message;
	}	
}
