package com.ipsg.inferneon.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;



/**
 * Algorithm JPA Entity.
 *
 */
@Entity
@Table(name = "ALGORITHM")
public class Algorithm extends AbstractEntity {
	
    
	private String algorithmName;

	@Column(name = "ALGORITHM_NAME",unique = true, nullable = false, length = 100)
	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
    
    
    
	

}
