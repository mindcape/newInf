package com.ipsg.inferneon.app.services;


import static com.ipsg.inferneon.app.services.ValidationUtils.assertNotBlank;
import static org.springframework.util.Assert.notNull;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipsg.inferneon.app.dao.AlgorithmRepository;
import com.ipsg.inferneon.app.dao.FileRepository;
import com.ipsg.inferneon.app.dao.ProjectRepository;
import com.ipsg.inferneon.app.dao.UserRepository;
import com.ipsg.inferneon.app.dto.FormInput;
import com.ipsg.inferneon.app.model.Activity;
import com.ipsg.inferneon.app.model.ActivityStatus;
import com.ipsg.inferneon.app.model.ActivityType;
import com.ipsg.inferneon.app.model.Algorithm;
import com.ipsg.inferneon.app.model.AlgorithmData;
import com.ipsg.inferneon.app.model.Attribute;
import com.ipsg.inferneon.app.model.BigFile;
import com.ipsg.inferneon.app.model.FormField;
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
    AlgorithmRepository algorithmRepository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    FileRepository fileRepository;

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
     * Find project by id. Loads only project object.
     * @param userName
     * @param projectId
     * @return Project.
     */
    public Project findProjectById(String userName, Long projectId) {
    	return projectRepository.findProjectById(userName,projectId);
    }
    
    
    /**
     * Loads project details including activities, file details and algorithm data details.
     * 
     * @param userName
     * @param projectId
     * @return Project object.
     */
    public Project loadProjectDetailsById(String userName, Long projectId) {
    	return projectRepository.loadProjectDetailsById(userName,projectId);
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
    public Project saveProject(String username, Long id, String projectName, Set<Attribute> attributes) {

        assertNotBlank(username, "username cannot be blank");
        notNull(projectName, "project name is mandatory");
        
        if (!projectRepository.isProjectNameAvailable(username, projectName)) {
            throw new IllegalArgumentException("Project Name is not available.");
        }

        Project project = null;
        if (id != null) {
          project =  findProjectById(username, id);//This needs change....
        } else {
        	project = new Project();
        }
        project.setProjectName(projectName);
        for(Attribute att: attributes) {
    		att.setProject(project);
    	} 
        project.setAttributes(attributes);
         
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
        	project.setUser(user);
            project = projectRepository.save(project);
            LOGGER.info("A project was attempted to be saved for user: " + username);
        }
        
        return project;
    }

    @Transactional
	public void addFiles(String username, List<BigFile> files, Long projectId) {
		Project project = findProjectById(username, projectId);
		Activity act = new Activity(ActivityType.FileUpload,ActivityStatus.STARTED,
    			new Timestamp(Calendar.getInstance().getTimeInMillis()),project);
		for(BigFile file: files) {
			file.setActivity(act);;
		}
		act.setFiles(files);
    	project.getActivities().add(act);
    	projectRepository.save(project);		
	}
    
    @Transactional
    public Algorithm getAlgorithmByName(String algorithmName) {
    	return algorithmRepository.loadAlgorithmWithFormFields(algorithmName);
    }
    
    @Transactional
    public Algorithm getAlgorithmById(Long algorithmId) {
    	return algorithmRepository.loadAlgorithmById(algorithmId);
    }
    
    @Transactional
    public List<Algorithm> getAllAlgorithm() {
    	return algorithmRepository.loadAllAlgorithms();
    }
    
    
    

    @Transactional
	public void saveAndRunAnalysis(FormInput formData, String userName) {
		Algorithm algorithm = getAlgorithmById(formData.getAlgorithmId());
		Project project = findProjectById(userName,formData.getProjectId());
		Activity activity = new Activity(ActivityType.RunAlgorithm, ActivityStatus.INITIATED, 
				new Timestamp(Calendar.getInstance().getTimeInMillis()), project);
		activity.setProject(project);
		for(FormField field: formData.getFormFields()) {
			AlgorithmData algData = new AlgorithmData(activity,algorithm,field.getName(),field.getSelectedValue());
			activity.getAlgorithmData().add(algData);			
		}
		
		projectRepository.saveProjectActivity(activity);
	}

    public BigFile findFileById(String projectName, Long fileId) {
    	return fileRepository.findFileById(projectName,fileId);
    }  
}
