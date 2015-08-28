package com.inferneon.supervised.utils;


public class DescriptiveTreeEdge {
	private String name;
	
	public DescriptiveTreeEdge(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}		
	
	@Override
	public String toString(){
		return name;
	}
}
