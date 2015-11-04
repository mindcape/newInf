package com.ipsg.inferneon.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



/**
 * Activity JPA Entity
 *
 */
@Entity
@Table(name = "ACTIVITY")
public class Activity extends AbstractEntity {
	
	@ManyToOne
	private File file;
	
	@ManyToOne
	private Algorithm algorithm;
	
    @Column(name = "STATUS")
	private String status;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Algorithm getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
