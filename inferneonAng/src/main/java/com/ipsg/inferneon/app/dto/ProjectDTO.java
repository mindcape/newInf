package com.ipsg.inferneon.app.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ipsg.inferneon.app.dto.serialization.CustomTimeDeserializer;
import com.ipsg.inferneon.app.dto.serialization.CustomTimeSerializer;
import com.ipsg.inferneon.app.model.Project;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * JSON serializable DTO containing Project data
 *
 */
public class ProjectDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "CET")
    private Date date;

    @JsonSerialize(using = CustomTimeSerializer.class)
    @JsonDeserialize(using = CustomTimeDeserializer.class)
    private Time time;

    private String description;
    private Long noOfProjects;

    public ProjectDTO() {
    }

    public ProjectDTO(Long id, Date date, Time time, String description, Long noOfProjects) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.description = description;
        this.noOfProjects = noOfProjects;
    }

    public static ProjectDTO mapFromProjectEntity(Project project) {
        return new ProjectDTO(project.getId(), project.getDate(), project.getTime(),
                project.getDescription(), project.getNoOfProjects());
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getNoOfProjects() {
        return noOfProjects;
    }

    public void setNoOfProjects(Long noOfProjects) {
        this.noOfProjects = noOfProjects;
    }

}
