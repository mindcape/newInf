package com.ipsg.inferneon.app.dto;

public class FormField {

	private String fieldName;
	private String fieldType;
	public enum Type {
		TEXT,
		SELECT,
		RADIO,
		CHECKBOX;
	};
	private String fieldLabel;
	private boolean validationRequired;
	
	public FormField(String fieldName, Type type, String fieldLabel, boolean b) {
		this.fieldName = fieldName;
		this.fieldLabel = fieldLabel;
		this.fieldType = type.name();
		this.validationRequired = b;
	}
	
	
	public Object withOption(String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldLabel() {
		return fieldLabel;
	}
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public String getFieldType() {
		return fieldType;
	}


	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}


	public boolean isValidationRequired() {
		return validationRequired;
	}


	public void setValidationRequired(boolean validationRequired) {
		this.validationRequired = validationRequired;
	}
		
	
	
	
}
