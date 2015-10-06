package com.inferneon.model;

import java.util.HashSet;
import java.util.Set;

public class attributeNames {
	private int Attribute_name_id;
	private String Attribute_name;
	private Attributes attributes;
	private int Attributeid;
	private String Attribute_type;
	private Set<attributeNominalValues> attributeNominalValues=new HashSet<attributeNominalValues>(0);
	public attributeNames(){
		
	}
	public attributeNames(int Attributeid,int attribute_name_id, String Attribute_name,
			Attributes attributes) {
		super();
		Attribute_name_id = attribute_name_id;
		this.Attribute_name = Attribute_name;
		this.attributes = attributes;
		this.Attributeid=Attributeid;
	}

	public int getAttributeid() {
		return Attributeid;
	}
	public void setAttributeid(int attributeid) {
		Attributeid = attributeid;
	}
	public int getAttribute_name_id() {
		return Attribute_name_id;
	}

	public void setAttribute_name_id(int attribute_name_id) {
		Attribute_name_id = attribute_name_id;
	}

	public String getAttribute_name() {
		return Attribute_name;
	}

	public void setAttribute_name(String Attribute_name) {
		this.Attribute_name = Attribute_name;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}
	public String getAttribute_type() {
		return Attribute_type;
	}
	public void setAttribute_type(String attribute_type) {
		Attribute_type = attribute_type;
	}
	public Set<attributeNominalValues> getAttributeNominalValues() {
		return attributeNominalValues;
	}
	public void setAttributeNominalValues(
			Set<attributeNominalValues> attributeNominalValues) {
		this.attributeNominalValues = attributeNominalValues;
	}
	

}
