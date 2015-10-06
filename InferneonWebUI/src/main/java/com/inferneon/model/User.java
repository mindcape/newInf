package com.inferneon.model;

import java.util.HashSet;
import java.util.Set;

public class User {
	private static final long serialVersionUID = 1L;

	private int id;
	private String password;
	private String Firstname;
	private String Lastname;
	private String Email;
	private Set<Project> projects = new HashSet<Project>(0);
	public User (){
		
	}

	public int getid() {
		return id;
	}

	public void setid(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return Firstname;
	}

	public void setFirstname(String Firstname) {
		this.Firstname = Firstname;
	}

	public String getLastname() {
		return Lastname;
	}

	public void setLastname(String Lastname) {
		this.Lastname = Lastname;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	}
