package com.ipsg.inferneon.app.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ipsg.inferneon.app.model.FormField;

public class FormInput implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6324392572095913689L;
	
	
	List<FormField> formFields = new ArrayList<>();
	Long projectId ;
	Long algorithmId;
	

	public FormInput() {
		// TODO Auto-generated constructor stub
	}


	public FormInput(List<FormField> formFields, Long projectId,
			Long algorithmId) {
		super();
		this.formFields = formFields;
		this.projectId = projectId;
		this.algorithmId = algorithmId;
	}


	public List<FormField> getFormFields() {
		return formFields;
	}


	public void setFormFields(List<FormField> formFields) {
		this.formFields = formFields;
	}


	public Long getProjectId() {
		return projectId;
	}


	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}


	public Long getAlgorithmId() {
		return algorithmId;
	}


	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}


	
	
}
