package com.ipsg.inferneon.app.controllers;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.ipsg.inferneon.app.dto.ProjectDTO;
import com.ipsg.inferneon.app.dto.ProjectsDTO;
import com.ipsg.inferneon.app.model.Project;
import com.ipsg.inferneon.app.model.SearchResult;
import com.ipsg.inferneon.app.services.ProjectService;

import java.security.Principal;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *  REST service for projects - allows to update, create and search for projects for the currently logged in user.
 *
 */
@Controller
@RequestMapping("project")
public class ProjectController {

    Logger LOGGER = Logger.getLogger(ProjectController.class);

    private static final long DAY_IN_MS = 1000 * 60 * 60 * 24;


    @Autowired
    private ProjectService projectService;

    /**
     * search Projects for the current user by date and time ranges.
     *
     *
     * @param principal  - the current logged in user
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @param pageNumber - the page number (each page has 10 entries)
     * @return - @see ProjectsDTO with the current page, total pages and the list of projects
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public ProjectsDTO searchProjectsByDate(
            Principal principal,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd") Date toDate,
            @RequestParam(value = "fromTime", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date fromTime,
            @RequestParam(value = "toTime", required = false) @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date toTime,
            @RequestParam(value = "pageNumber") Integer pageNumber) {

        if (fromDate == null && toDate == null) {
            fromDate = new Date(System.currentTimeMillis() - (3 * DAY_IN_MS));
            toDate = new Date();
        }

        SearchResult<Project> result = projectService.findProjects(
                principal.getName(),
                fromDate,
                toDate,
                fromTime != null ? new Time(fromTime.getTime()) : null,
                toTime != null ? new Time(toTime.getTime()) : null,
                pageNumber);

        Long resultsCount = result.getResultsCount();
        Long totalPages = resultsCount / 10;

        if (resultsCount % 10 > 0) {
            totalPages++;
        }

        return new ProjectsDTO(pageNumber, totalPages, ProjectDTO.mapFromProjectsEntities(result.getResult()));
    }

    /**
     *
     * saves a list of projects - they be either new or existing
     *
     * @param principal - the current logged in user
     * @param projects - the list of projects to save
     * @return - an updated version of the saved projects
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST)
    public List<ProjectDTO> saveProjects(Principal principal, @RequestBody List<ProjectDTO> projects) {

        List<Project> savedProjects = projectService.saveProjects(principal.getName(), projects);

        return savedProjects.stream()
                .map(ProjectDTO::mapFromProjectEntity)
                .collect(Collectors.toList());
    }

    /**
     *
     * deletes a list of projects
     *
     * @param deletedProjectIds - the ids of the projects to be deleted
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteProjects(@RequestBody List<Long> deletedProjectIds) {
        projectService.deleteProjects(deletedProjectIds);
    }

    /**
     *
     * error handler for backend errors - a 400 status code will be sent back, and the body
     * of the message contains the exception text.
     *
     * @param exc - the exception caught
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception exc) {
        LOGGER.error(exc.getMessage(), exc);
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
