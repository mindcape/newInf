package com.ipsg.inferneon.app.services;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipsg.inferneon.app.dao.ProjectRepository;
import com.ipsg.inferneon.app.dao.UserRepository;
import com.ipsg.inferneon.app.dto.ProjectDTO;
import com.ipsg.inferneon.app.model.Project;
import com.ipsg.inferneon.app.model.SearchResult;
import com.ipsg.inferneon.app.model.User;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.ipsg.inferneon.app.services.ValidationUtils.assertNotBlank;
import static org.springframework.util.Assert.notNull;

/**
 *
 * Business service for Project-related operations.
 *
 */
@Service
public class ProjectService {

    private static final Logger LOGGER = Logger.getLogger(ProjectService.class);

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    /**
     *
     * searches projects by date/time
     *
     * @param username - the currently logged in user
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @param pageNumber - the page number (each page has 10 entries)
     * @return - the found results
     */
    @Transactional(readOnly = true)
    public SearchResult<Project> findProjects(String username, Date fromDate, Date toDate, Time fromTime, Time toTime, int pageNumber) {

        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Both the from and to date are needed.");
        }

        if (fromDate.after(toDate)) {
            throw new IllegalArgumentException("From date cannot be after to date.");
        }

        if (fromDate.equals(toDate) && fromTime != null && toTime != null && fromTime.after(toTime)) {
            throw new IllegalArgumentException("On searches on the same day, from time cannot be after to time.");
        }

        Long resultsCount = projectRepository.countProjectsByDateTime(username, fromDate, toDate, fromTime, toTime);

        List<Project> projects = projectRepository.findProjectsByDateTime(username, fromDate, toDate, fromTime, toTime, pageNumber);

        return new SearchResult<>(resultsCount, projects);
    }

    /**
     *
     * deletes a list of projects, given their Ids
     *
     * @param deletedProjectIds - the list of projects to delete
     */
    @Transactional
    public void deleteProjects(List<Long> deletedProjectIds) {
        notNull(deletedProjectIds, "deletedProjectsId is mandatory");
        deletedProjectIds.stream().forEach((deletedProjectId) -> projectRepository.delete(deletedProjectId));
    }

    /**
     *
     * saves a project (new or not) into the database.
     *
     * @param username - - the currently logged in user
     * @param id - the database ud of the project
     * @param date - the date the project took place
     * @param time - the time the project took place
     * @param description - the description of the project
     * @param noOfProjects - the noOfProjects of the project
     * @return - the new version of the project
     */

    @Transactional
    public Project saveProject(String username, Long id, Date date, Time time, String description, Long noOfProjects) {

        assertNotBlank(username, "username cannot be blank");
        notNull(date, "date is mandatory");
        notNull(time, "time is mandatory");
        notNull(description, "description is mandatory");
        notNull(noOfProjects, "noOfProjects is mandatory");

        Project project = null;

        if (id != null) {
            project = projectRepository.findProjectById(id);

            project.setDate(date);
            project.setTime(time);
            project.setDescription(description);
            project.setNoOfProjects(noOfProjects);
        } else {
            User user = userRepository.findUserByUsername(username);

            if (user != null) {
                project = projectRepository.save(new Project(user, date, time, description, noOfProjects));
                LOGGER.warn("A project was attempted to be saved for a non-existing user: " + username);
            }
        }

        return project;
    }

    /**
     *
     * saves a list of projects (new or not) into the database
     *
     * @param username - the currently logged in user
     * @param projects - the list of projects to be saved
     * @return - the new versions of the saved projects
     */
    @Transactional
    public List<Project> saveProjects(String username, List<ProjectDTO> projects) {
        return projects.stream()
                .map((project) -> saveProject(
                        username,
                        project.getId(),
                        project.getDate(),
                        project.getTime(),
                        project.getDescription(),
                        project.getNoOfProjects()))
                .collect(Collectors.toList());
    }
}
