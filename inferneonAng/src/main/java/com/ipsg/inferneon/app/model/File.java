package com.ipsg.inferneon.app.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "FILE")
public class File extends AbstractEntity {
    @Column(name = "FILE_NAME")
	private String fileName;
    
    @Column(name = "DATE_CREATED")
	private Date dateCreated;
    
    @Column(name = "UPLOAD_TS")
	private Timestamp uploadTime;
    
    @Column(name = "FILE_LOC")
	private String fileLoc;
    
    @Column(name = "EXT_FILE_SOURCE")
	private String extFileParams;
	
	@ManyToOne
	private Project project;
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Timestamp getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Timestamp uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getFileLoc() {
		return fileLoc;
	}
	public void setFileLoc(String fileLoc) {
		this.fileLoc = fileLoc;
	}
	public String getExtFileParams() {
		return extFileParams;
	}
	public void setExtFileParams(String extFileParams) {
		this.extFileParams = extFileParams;
	}
}
