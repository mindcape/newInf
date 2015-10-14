package com.inferneon.model;

import java.util.Date;

public class Activities {
	private int Activities_id;
	private String Activities_name;
	private Date Run_date;
	private int projectid;
	
	
	public Activities() {
		super();
	}
	public Activities(int activities_id, String activities_name, Date run_date,int projectid) {
		super();
		Activities_id = activities_id;
		Activities_name = activities_name;
		Run_date = run_date;
		this.projectid=projectid;
	}
	public int getActivities_id() {
		return Activities_id;
	}
	public void setActivities_id(int activities_id) {
		Activities_id = activities_id;
	}
	public String getActivities_name() {
		return Activities_name;
	}
	public void setActivities_name(String activities_name) {
		Activities_name = activities_name;
	}
	public Date getRun_date() {
		return Run_date;
	}
	public void setRun_date(Date run_date) {
		Run_date = run_date;
	}
	public int getProjectid() {
		return projectid;
	}
	public void setProjectid(int projectid) {
		this.projectid = projectid;
	}
	
	
	
}