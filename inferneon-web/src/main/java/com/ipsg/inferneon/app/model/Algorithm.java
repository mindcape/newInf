package com.ipsg.inferneon.app.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;



/**
 * Algorithm JPA Entity.
 *
 */
@Entity
@Table(name = "ALGORITHM")
@NamedQueries({
    @NamedQuery(
            name = Algorithm.LoadAlgorithFormFieldsByName,
            query = "select al from Algorithm al,FormField ff,FieldKeyValue fkv where al.algorithmName = :algorithmname"
    )
})
public class Algorithm extends AbstractEntity {
	
	public static final String LoadAlgorithFormFieldsByName  = "LoadAlgorithFormFieldsByName";
	
	@Column(name = "ALGORITHM_NAME", unique = true, nullable = false, length = 100)
	private String algorithmName;

	@Column(name = "REPORT_TYPE", nullable = false, length = 100)
	public String reportType;

	@OneToMany(mappedBy = "algorithm", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<FormField> fields = new ArrayList<>();
	
	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public List<FormField> getFields() {
		return fields;
	}

	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "Algorithm [algorithmName=" + algorithmName + ", reportType=" + reportType + "]";
	}

	
}
