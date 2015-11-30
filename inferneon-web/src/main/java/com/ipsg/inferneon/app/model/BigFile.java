package com.ipsg.inferneon.app.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name = "BIGFILE")
@NamedQueries({
    @NamedQuery(
            name = BigFile.FindFilesByProjectId,
            query = "select fl from BigFile fl where fl.activity.activityType = 'FileUpload' and fl.activity.project.id = :projectId"
    ), 
    @NamedQuery(
            name = BigFile.FindFileByNameAndProjectName,
            query = "select fl from BigFile fl where fl.fileName = :filename and fl.activity.project.projectName = :projectname"
    )
})
public class BigFile extends AbstractEntity {
	public static final String FindFilesByProjectId = "BigFile.FindFilesByProjectId";
	public static final String FindFileByNameAndProjectName = "FindFileByNameAndProjectName";
	
    @Column(name = "FILE_NAME")
	private String fileName;
    
    @Column(name = "CREATE_TS")
	private Date dateCreated;
    
    @Column(name = "UPLOAD_TS")
	private Timestamp uploadTime;
    
    @Column(name = "FILE_LOC")
	private String fileLoc;
    
    @Column(name = "EXT_FILE_SOURCE")
	private String extFileParams;
	
    @ManyToOne(targetEntity=Activity.class,fetch=FetchType.LAZY)
	@JoinColumn(name = "activity", nullable = false)
	@JsonBackReference
	private Activity activity;
	
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

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	@Override
	    public boolean equals(Object o) {
	    	BigFile newFile = (BigFile)o;
	    	return this.fileName.equals(newFile.getFileName());    	
	    }
}
