package com.inferneon.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


public class Project implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int projectid;
	private String project_name;
	private Date date_created;
	private int id;
	private User user;
	 private Set<Attributes> attributes;
	 private Set<Activities> activities;
	 private Set<FileUpload> fileupload;
	public Project() {

	}


	public Project(int projectid, String project_name, Date date_created,
			int id, User user, Set<Attributes> attributes,Set<FileUpload> fileupload,
			Set<Activities> activities) {
		super();
		this.projectid = projectid;
		this.project_name = project_name;
		this.date_created = date_created;
		this.id = id;
		this.user = user;
		this.attributes = attributes;
		this.activities = activities;
		this.fileupload=fileupload;
	}


	public Set<FileUpload> getFileupload() {
		return fileupload;
	}


	public void setFileupload(Set<FileUpload> fileupload) {
		this.fileupload = fileupload;
	}


	public Set<Activities> getActivities() {
		return activities;
	}


	public void setActivities(Set<Activities> activities) {
		this.activities = activities;
	}


	public Set<Attributes> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<Attributes> attributes) {
		this.attributes = attributes;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getprojectid() {
		return projectid;
	}

	public void setprojectid(int projectid) {
		this.projectid = projectid;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setproject_name(String project_name) {
		this.project_name = project_name;
	}

	public Date getdate_created() {
		return date_created;
	}

	public void setdate_created(Date date_created) {
		this.date_created = date_created;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Project [projectid=" + projectid + ", project_name="
				+ project_name + ", date_created=" + date_created + ", userId="
				+ id + "]";
	}

}