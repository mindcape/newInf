package com.ipsg.inferneon.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "FieldKeyValue")
public class FieldKeyValue {
	
    @Id
    @GeneratedValue
	private Long id;
	
	@Column(name="KEY", nullable=false)
	private String key;
	
	@Column(name="VALUE",nullable=false)
	private String value;
	
	
	
	public FieldKeyValue() {
		super();		
	}


	public FieldKeyValue(String key, String value,FormField fieldId) {
		super();
		this.key = key;
		this.value = value;
		this.fieldId = fieldId;
	}
	@ManyToOne(targetEntity=FormField.class)
    @JoinColumn(name = "fieldId", referencedColumnName = "id", nullable = false)
    @JsonBackReference
	private FormField fieldId;


	
	
    public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public FormField getFieldId() {
		return fieldId;
	}


	public void setFieldId(FormField fieldId) {
		this.fieldId = fieldId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldKeyValue other = (FieldKeyValue) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}


	
    
    

}
