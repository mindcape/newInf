package com.ipsg.inferneon.app.services;


import static com.ipsg.inferneon.app.services.ValidationUtils.assertNotBlank;
import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipsg.inferneon.app.dao.ProjectRepository;
import com.ipsg.inferneon.app.dao.UserRepository;
import com.ipsg.inferneon.app.model.Attribute;
import com.ipsg.inferneon.app.model.Project;
import com.ipsg.inferneon.app.model.User;

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
    public List<Project> findProjects(String username, int pageNumber) {
//        Long resultsCount = projectRepository.countProjectsByUser(username);
        List<Project> projects = projectRepository.findProjectsByUser(username,  pageNumber);

        return projects;
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
     * @param projectName - name of the project
     * @return - the new version of the project
     */

    @Transactional
    public Project saveProject(String username, Long id, String projectName, Set<Attribute> attributes) {
        assertNotBlank(username, "username cannot be blank");
        notNull(projectName, "project name is mandatory");
        Project project = null;

        if (id != null) {
            project = new Project();
            project.setProjectName(projectName);
        } else {
            User user = userRepository.findUserByUsername(username);

            if (user != null) {
                project = projectRepository.save(new Project(projectName, user,attributes));
                LOGGER.info("A project was attempted to be saved for user: " + username);
            }
        }
        return project;
    }

    
}
