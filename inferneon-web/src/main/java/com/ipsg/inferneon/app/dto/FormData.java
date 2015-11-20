package com.ipsg.inferneon.app.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ipsg.inferneon.app.model.FormField;

public class FormData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6324392572095913689L;
	
	
	List<FormField> formFields = new ArrayList<>();


	public List<FormField> getFormFields() {
		return formFields;
	}


	public void setFormFields(List<FormField> formFields) {
		this.formFields = formFields;
	}


	

	
	
	
	
}
