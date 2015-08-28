package com.inferneon.supervised.utils;

public class DescriptiveTreeNode {
	private String name;
	private String leafNodeDistribution;
	private boolean isLeaf;
	
	public DescriptiveTreeNode(String name){
		this.name = name;
	}
	
	public DescriptiveTreeNode(String name, String leafNodeDistribution){
		this.isLeaf = true;
		this.name = name;
		this.leafNodeDistribution = leafNodeDistribution;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	public String getLeafDistribution(){
		return leafNodeDistribution;
	}
	
	public boolean isLeaf(){
		return isLeaf;
	}
	
	public void setLeafDistribution(String leafNodeDistribution){
		this.leafNodeDistribution = leafNodeDistribution;
		this.isLeaf = true;
	}
}
