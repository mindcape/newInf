package com.inferneon.model;

import java.util.Set;


public class Project implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private int projectid;
	private String project_name;
	private String date_created;
	private int id;
	private User user;
	 private Set<Attributes> attributes;
	public Project() {

	}

	public Project(int projectid, String project_name, String date_created,int id,User user) {
		super();
		this.projectid = projectid;
		this.project_name = project_name;
		this.date_created = date_created;
		this.id=id;
		this.user=user;
		
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

	public String getdate_created() {
		return date_created;
	}

	public void setdate_created(String date_created) {
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