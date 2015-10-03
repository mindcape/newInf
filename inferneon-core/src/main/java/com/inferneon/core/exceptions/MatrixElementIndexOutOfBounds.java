package com.inferneon.core.exceptions;

public class MatrixElementIndexOutOfBounds extends Exception{
	private String message;

	public MatrixElementIndexOutOfBounds(String message, boolean isRow, long rowNum, long mRows){
		
		super(message);
		this.message = message;
	}	
}
