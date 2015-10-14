package com.inferneon.dao;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.inferneon.model.*;
public interface UserDataAccessObject {
	public void NewRegistration(User user);
	   public int login(User user);
	   public void newProject(Project project);
	   public List<Project> getProjects(int userId);
	   public void newAttribute(Attributes attributes);
	   public void newAttributeName(attributeNames attributenames);
	   public void newNominalValues(attributeNominalValues attributeNominalValuess);
	   public List<Activities> getActivities(int projectid);
		public void newActivities(Activities activities);
		public List<User> getUsername(int userId);

}
