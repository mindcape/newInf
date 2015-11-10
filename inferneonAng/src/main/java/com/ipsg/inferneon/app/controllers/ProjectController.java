package com.ipsg.inferneon.app.controllers;


import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ipsg.inferneon.app.dto.ProjectDTO;
import com.ipsg.inferneon.app.model.Attribute;
import com.ipsg.inferneon.app.model.Project;
import com.ipsg.inferneon.app.services.ProjectService;

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
     * @param pageNumber - the page number (each page has 10 entries)
     * @return - @see ProjectsDTO with the current page, total pages and the list of projects
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<ProjectDTO> loadProjectsByUser(
            Principal principal,           
            @RequestParam(value = "pageNumber") Integer pageNumber) {


        List<Project> result = projectService.findProjects(principal.getName(),1);
        return result.stream()
                .map(ProjectDTO::mapFromProjectEntity)
                .collect(Collectors.toList());
      /*  int resultsCount = result.size();
        int totalPages = resultsCount / 10;

        if (resultsCount % 10 > 0) {
            totalPages++;
        }

        return new ProjectsDTO(pageNumber, totalPages, ProjectDTO.mapFromProjectsEntities(result));*/
    }
    
    
    /**
     * 
     * search Project for the current user by Project Id
     * 
     * 
     * 
     * @param principal
     * @param projectId
     * @return ProjectDTO
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/loadProjectById", method = RequestMethod.GET)
    public ProjectDTO findProjectById( Principal principal,           
            @RequestParam(value = "projectId") Long projectId) {
    	
    	Project result = projectService.findProjectById(principal.getName(),projectId);
    	 return ProjectDTO.mapFromProjectEntity(result);
            	
            }

   /* *//**
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
    public List<ProjectDTO> saveProject(Principal principal, @RequestBody ProjectDTO project) {
    	
    	Set<Attribute> newAtts = new HashSet<Attribute>();
    	for(Attribute att:project.getAttributes()){
    		newAtts.add(new Attribute(att.getAttName(),att.getAttType(),att.getAttValidValues(),att.getAttOrder()));   
    		System.out.println(att.toString());
    	}

        projectService.saveProject(principal.getName(), project.getId(), project.getProjectName(), newAtts);

        List<Project> allProjects = projectService.findProjects(principal.getName(), 1);
        return allProjects.stream()
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
