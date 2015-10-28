package com.ipsg.inferneon.app.model;


import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * The User JPA entity.
 *
 */
@Entity
@Table(name = "USERS")
@NamedQueries({
        @NamedQuery(
                name = User.FIND_BY_USERNAME,
                query = "select u from User u where username = :username"
        ),
        @NamedQuery(
                name = User.COUNT_TODAYS_NO_OF_PROJECTS,
                query = "select sum(m.noOfProjects) from Project m where m.user.username = :username and m.date = CURRENT_DATE"
        )
})
public class User extends AbstractEntity {

    public static final String FIND_BY_USERNAME = "user.findByUserName";
    public static final String COUNT_TODAYS_NO_OF_PROJECTS = "user.todaysNoOfProjects";

    private String username;
    private String passwordDigest;
    private String email;
    private Long maxNoOfProjectsPerDay;


    public User() {

    }

    public User(String username, String passwordDigest, String email, Long maxNoOfProjectsPerDay) {
        this.username = username;
        this.passwordDigest = passwordDigest;
        this.email = email;
        this.maxNoOfProjectsPerDay = maxNoOfProjectsPerDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordDigest() {
        return passwordDigest;
    }

    public void setPasswordDigest(String passwordDigest) {
        this.passwordDigest = passwordDigest;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getMaxNoOfProjectsPerDay() {
        return maxNoOfProjectsPerDay;
    }

    public void setMaxNoOfProjectsPerDay(Long maxNoOfProjectsPerDay) {
        this.maxNoOfProjectsPerDay = maxNoOfProjectsPerDay;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", maxNoOfProjectsPerDay=" + maxNoOfProjectsPerDay +
                '}';
    }
}
