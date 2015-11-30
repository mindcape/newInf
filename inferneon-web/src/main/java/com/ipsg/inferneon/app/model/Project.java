package com.ipsg.inferneon.app.model;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 *
 * The Project JPA entity
 *
 */
@Entity
@Table(name = "PROJECT")
@NamedQueries({
    @NamedQuery(
            name = Project.FindProjectByNameAndUserName,
            query = "select pr from Project pr where pr.projectName = :projectname and pr.user.username = :username"
    )
})
public class Project extends AbstractEntity {

	public static final String FindProjectByNameAndUserName = "project.findProjectByNameAndUserName";
	//public static final String PROJECT_FILES = "project.projectFiles";
    @Column(name = "PROJECT_NAME" , nullable = false, length = 30)
	private String projectName;
    
	@ManyToOne
    private User user;
	
    @Column(name = "CREATED_TS")
    private Timestamp createdTS;
    
    @Column(name = "UPDATED_TS")
    private Timestamp updatedTS;
    
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,fetch = FetchType.EAGER, orphanRemoval=true)
    @JsonManagedReference
    private Set<Attribute> attributes = new HashSet<>();
    

    

    @OneToMany(mappedBy = "project", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Activity> activities = new HashSet<>();

 

    public Project() {

    }

    public Project(String projectName, User user, Set<Attribute> attributes) {
        this.projectName = projectName;
        this.user = user;
        this.attributes = attributes;
    }
    
    public Set<Attribute> getAttributes() {
		return attributes;
	}


	public void setAttributes(Set<Attribute> attributes) {
		this.attributes.clear();
		this.attributes.addAll(attributes);
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	
	
	public Timestamp getCreatedTS() {
		return createdTS;
	}

	public void setCreatedTS(Timestamp createdTS) {
		this.createdTS = createdTS;
	}

	public Timestamp getUpdatedTS() {
		return updatedTS;
	}

	public void setUpdatedTS(Timestamp updatedTS) {
		this.updatedTS = updatedTS;
	}



	public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	@Override
	public String toString() {
		return "Project [projectName=" + projectName + ", user=" + user + ", createdTS=" + createdTS + ", updatedTS="
				+ updatedTS + ", attributes=" + attributes + "]";
	}
    
    @Override
    public boolean equals(Object o) {
    	Project newProj = (Project)o;
    	return this.projectName.equals(newProj.getProjectName());    	
    }
    
    
    @Override
    public int hashCode() {
    	return projectName.hashCode();
    }

	public Set<Activity> getActivities() {
		return activities;
	}

	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
	}    
    
    
    
}
