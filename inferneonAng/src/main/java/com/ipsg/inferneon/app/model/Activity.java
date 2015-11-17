package com.ipsg.inferneon.app.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;



/**
 * Activity JPA Entity
 *
 */
@Entity
@Table(name = "ACTIVITY")
public class Activity extends AbstractEntity {
	
	@Column(name="ACTIVITY_TYPE")
	private String activityType;
	
	@Column(name = "STATUS")
	private String status;
	
    @Column(name = "START_TS")
    private Timestamp startTS;
    
    @Column(name = "END_TS")
    private Timestamp endTs;
    
    @ManyToOne(targetEntity=Project.class)
    @JoinColumn(name = "project", referencedColumnName = "id", nullable = false)
    @JsonBackReference
	private Project project;
    
    
    
	public Activity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Activity(ActivityType activityType, ActivityStatus status, Timestamp startTS, Project project) {
		super();
		this.activityType = activityType.name();
		this.status = status.name();
		this.startTS = startTS;
		this.project = project;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getStartTS() {
		return startTS;
	}
	public void setStartTS(Timestamp startTS) {
		this.startTS = startTS;
	}
	public Timestamp getEndTs() {
		return endTs;
	}
	public void setEndTs(Timestamp endTs) {
		this.endTs = endTs;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
}
