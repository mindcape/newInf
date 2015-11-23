package com.ipsg.inferneon.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="ALGORITHM_DATA")
public class AlgorithmData extends AbstractEntity {
	
	@ManyToOne(targetEntity=Activity.class,fetch=FetchType.LAZY)
	@JoinColumn(name = "activity", nullable = false)
	@JsonBackReference
	private Activity activity;
	
	@ManyToOne(targetEntity=Algorithm.class,fetch=FetchType.LAZY)
	@JoinColumn(name = "algorithm", nullable = false)
	@JsonBackReference
	private Algorithm algorithm;
	
	@Column(name="FIELD_NAME")
	private String fieldName;
	
	@Column(name="FIELD_VALUE")
	private String fieldValue;
	
	public AlgorithmData() {
		super();
	}

	public AlgorithmData(Activity activity, Algorithm algorithm, String fieldName, String fieldValue) {
		super();
		this.activity = activity;
		this.algorithm = algorithm;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}
