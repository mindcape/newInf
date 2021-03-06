package com.ipsg.inferneon.app.model;

import java.sql.Timestamp;
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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;



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
    
    @ManyToOne(targetEntity=Project.class,fetch=FetchType.LAZY)
    @JoinColumn(name = "project", referencedColumnName = "id", nullable = false)
    @Fetch(FetchMode.SELECT)
    @JsonBackReference(value="project-act")
	private Project project;
    
    @OneToMany(mappedBy = "activity", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference(value="activity-alg")
    private List<AlgorithmData> algorithmData = new ArrayList<>();
    
    
    @OneToMany(mappedBy = "activity", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference(value="activity-files")
    private List<BigFile> files = new ArrayList<>();
    
	public Activity() {
		super();
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
	public String getActivityType() {
		return activityType;
	}
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public List<AlgorithmData> getAlgorithmData() {
		return algorithmData;
	}

	public void setAlgorithmData(List<AlgorithmData> algorithmData) {
		this.algorithmData = algorithmData;
	}

	public List<BigFile> getFiles() {
		return files;
	}

	public void setFiles(List<BigFile> files) {
		this.files = files;
	}
	
}
