package com.ipsg.inferneon.app.model;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * The Project JPA entity
 *
 */
@Entity
@Table(name = "PROJECT")
public class Project extends AbstractEntity {

    @Column(name = "PROJECT_NAME" , unique = true, nullable = false, length = 30)
	private String projectName;
    
	@ManyToOne
    private User user;
	
    @Column(name = "CREATED_TS")
    private Timestamp createdTS;
    
    @Column(name = "UPDATED_TS")
    private Timestamp updatedTS;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project",cascade = { javax.persistence.CascadeType.ALL })
    private Set<Attribute> attributes = new HashSet<Attribute>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
    private Set<File> files = new HashSet<>();

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
		this.attributes = attributes;
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

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
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
				+ updatedTS + ", attributes=" + attributes + ", files=" + files + "]";
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
}
