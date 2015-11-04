package com.ipsg.inferneon.app.dto;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipsg.inferneon.app.model.Attribute;
import com.ipsg.inferneon.app.model.Project;

/**
 *
 * JSON serializable DTO containing Project data
 *
 */
public class ProjectDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "CET")
    private Timestamp createTS;
    
    private String projectName;
    private Set<Attribute> attributes = new HashSet<Attribute>();

    public ProjectDTO() {
    }

    public ProjectDTO(Long id, Timestamp createTS, String projectName) {
        this.id = id;
        this.createTS = createTS;
        this.projectName = projectName;
    }

    public static ProjectDTO mapFromProjectEntity(Project project) {
        return new ProjectDTO(project.getId(), project.getCreatedTS(),project.getProjectName());
    }

    public static List<ProjectDTO> mapFromProjectsEntities(List<Project> projects) {
        return projects.stream().map((project) -> mapFromProjectEntity(project)).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Timestamp getCreateTS() {
		return createTS;
	}

	public void setCreateTS(Timestamp createTS) {
		this.createTS = createTS;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

    



}
