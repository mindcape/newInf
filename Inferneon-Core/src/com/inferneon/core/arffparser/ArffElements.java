package com.inferneon.core.arffparser;

import java.util.List;

import com.inferneon.core.Attribute;

public class ArffElements {
	
	private String relationName;
	private List<Attribute> attributes;
	private String data;
	private int dataStartLine;
	
	public ArffElements(String relationName, List<Attribute> attributes,
			String data, int dataStartLine ) {
		this.relationName = relationName;
		this.attributes = attributes;
		this.data = data;
		this.dataStartLine = dataStartLine;
	}
	
	public String getRelationName() {
		return relationName;
	}
	
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public String getData() {
		return data;
	}
	
	public int getDataStartLine() {
		return dataStartLine;
	}
	
	public void setData(String data) {
		this.data = data;
	}	
}
