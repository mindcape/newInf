package com.ipsg.inferneon.app.dto;

/**
 *
 * JSON-serializable DTO containing user data
 *
 */
public class UserInfoDTO {

    private String userName;
    private Long maxNoOfProjectsPerDay;
    private Long todaysNoOfProjects;

    public UserInfoDTO(String userName, Long maxNoOfProjectsPerDay, Long todaysNoOfProjects) {
        this.userName = userName;
        this.maxNoOfProjectsPerDay = maxNoOfProjectsPerDay;
        this.todaysNoOfProjects = todaysNoOfProjects;
    }

    public Long getMaxNoOfProjectsPerDay() {
        return maxNoOfProjectsPerDay;
    }

    public void setMaxNoOfProjectsPerDay(Long maxNoOfProjectsPerDay) {
        this.maxNoOfProjectsPerDay = maxNoOfProjectsPerDay;
    }

    public Long getTodaysNoOfProjects() {
        return todaysNoOfProjects;
    }

    public void setTodaysNoOfProjects(Long todaysNoOfProjects) {
        this.todaysNoOfProjects = todaysNoOfProjects;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
