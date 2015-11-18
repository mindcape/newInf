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
                name = User.USER_PROJECTS,
                query = "select count(m.projectName) from Project m where m.user.username = :username"
        )
})
public class User extends AbstractEntity {

    public static final String FIND_BY_USERNAME = "user.findByUserName";
    public static final String USER_PROJECTS = "user.userProjects";

    private String username;
    private String passwordDigest;
    private String email;
    private Long maxNoOfProjectsPerPage;


    public User() {

    }

    public User(String username, String passwordDigest, String email, Long maxNoOfProjectsPerPage) {
        this.username = username;
        this.passwordDigest = passwordDigest;
        this.email = email;
        this.maxNoOfProjectsPerPage = maxNoOfProjectsPerPage;
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

    public Long getMaxNoOfProjectsPerPage() {
		return maxNoOfProjectsPerPage;
	}

	public void setMaxNoOfProjectsPerPage(Long maxNoOfProjectsPerPage) {
		this.maxNoOfProjectsPerPage = maxNoOfProjectsPerPage;
	}

	@Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", maxNoOfProjectsPerPage=" + maxNoOfProjectsPerPage +
                '}';
    }
}
