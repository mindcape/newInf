package com.ipsg.inferneon.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "FormField")
public class FormField extends AbstractEntity {
	
	@Column(name = "NAME", nullable = false, length = 100)
	private String name;
	
	@Column(name = "TYPE",nullable = false, length = 100)
	private String type;
	
	@Column(name = "LABEL", nullable = false, length = 100)
	private String label;
	
	@Column(name = "VALIDATION_REQ")
	private Boolean validationRequired;
	
	@Transient
	private String selectedValue;
	
	
	public FormField() {
		super();
	}

	public FormField(String name, String type, String label, Boolean validationRequired) {
		super();
		this.name = name;
		this.type = type;
		this.label = label;
		this.validationRequired = validationRequired;
	}

	@OneToMany(mappedBy = "fieldId", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
	private List<FieldKeyValue> keyValues = new ArrayList<>();
    
    @ManyToOne(targetEntity=Algorithm.class)
    @JoinColumn(name = "algorithm", nullable = false)
    @JsonBackReference
	private Algorithm algorithm;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	

	public Boolean getValidationRequired() {
		return validationRequired;
	}

	public void setValidationRequired(Boolean validationRequired) {
		this.validationRequired = validationRequired;
	}

	public List<FieldKeyValue> getKeyValues() {
		return keyValues;
	}

	public void setKeyValues(List<FieldKeyValue> keyValues) {
		this.keyValues = keyValues;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public String toString() {
		return "FormField [name=" + name + ", type=" + type + ", label=" + label + ", validationRequired="
				+ validationRequired + ", algorithm=" + algorithm + "]";
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

    
    
}
