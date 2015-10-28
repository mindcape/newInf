package com.ipsg.inferneon.app.model;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Date;

/**
 *
 * The Project JPA entity
 *
 */
@Entity
@Table(name = "PROJECTS")
public class Project extends AbstractEntity {

    @ManyToOne
    private User user;

    private Date date;
    private Time time;
    private String description;
    private Long noOfProjects;

    public Project() {

    }

    public Project(User user, Date date, Time time, String description, Long noOfProjects) {
        this.user = user;
        this.date = date;
        this.time = time;
        this.description = description;
        this.noOfProjects = noOfProjects;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
