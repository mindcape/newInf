package com.ipsg.inferneon.app.dao;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.ipsg.inferneon.app.model.Activity;
import com.ipsg.inferneon.app.model.Project;
import com.ipsg.inferneon.app.model.User;

/**
 *
 * Repository class for the Project entity
 *
 */
@Repository
public class ProjectRepository {

    private static final Logger LOGGER = Logger.getLogger(ProjectRepository.class);

    @PersistenceContext
    EntityManager em;

    /**
     *
     * counts the matching projects, given the bellow criteria
     *
     * @param username - the currently logged in username
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @return -  a list of matching projects, or an empty collection if no match found
     */
    public Long countProjectsByUser(String username) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // query for counting the total results
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Project> countRoot = cq.from(Project.class);
        cq.select((cb.count(countRoot)));
        cq.where(getCommonWhereCondition(cb, username, countRoot));
        Long resultsCount = em.createQuery(cq).getSingleResult();

        LOGGER.info("Found " + resultsCount + " results.");

        return resultsCount;
    }

    /**
     *
     * finds a list of projects, given the bellow criteria
     *
     * @param username - the currently logged in username
     * @param fromDate - search from this date, including
     * @param toDate - search until this date, including
     * @param fromTime - search from this time, including
     * @param toTime - search to this time, including
     * @return -  a list of matching projects, or an empty collection if no match found
     */
    public List<Project> findProjectsByUser(String username, int pageNumber) {
       CriteriaBuilder cb = em.getCriteriaBuilder();

        // the actual search query that returns one page of results
        CriteriaQuery<Project> searchQuery = cb.createQuery(Project.class);
        Root<Project> searchRoot = searchQuery.from(Project.class);
        searchQuery.select(searchRoot);
        searchQuery.where(getCommonWhereCondition(cb, username, searchRoot));
        searchQuery.orderBy(cb.desc(searchRoot.get("id")));
        TypedQuery<Project> filterQuery = em.createQuery(searchQuery);
        return filterQuery.getResultList();
    }

    /**
     * Delete a project, given its identifier
     *
     * @param deletedProjectId - the id of the project to be deleted
     */
    public void delete(Long deletedProjectId) {
        Project delete = em.find(Project.class, deletedProjectId);
        em.remove(delete);
    }

    /**
     *
     * finds a project given its id
     *
     */
    public Project findProjectById(String username, Long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Project> searchQuery = cb.createQuery(Project.class);
        Root<Project> project = searchQuery.from(Project.class);
        searchQuery.select(project);
        //Joins to project
        List<Predicate> predicates = new ArrayList<>();
        Join<Project, User> user = project.join("user");
        predicates.add(cb.equal(user.<String>get("username"), username));
        predicates.add(cb.equal(project.<Long>get("id"), projectId));
        searchQuery.where(predicates.toArray(new Predicate[]{}));
        Project result =  em.createQuery(searchQuery).getSingleResult();
        return result;
    }
    
    
    /**
    *
    * finds a project given its id
    *
    */
    @SuppressWarnings("unused")
    public Project loadProjectDetailsById(String username, Long projectId) throws NoResultException{
       CriteriaBuilder cb = em.getCriteriaBuilder();
       CriteriaQuery<Project> searchQuery = cb.createQuery(Project.class);
       Root<Project> project = searchQuery.from(Project.class);
       searchQuery.select(project);
       //Joins to project
       List<Predicate> predicates = new ArrayList<>();
       Join<Project, User> user = project.join("user");
       Join<Project, Activity> file = project.join("activities",JoinType.LEFT);
       predicates.add(cb.equal(user.<String>get("username"), username));
       predicates.add(cb.equal(project.<Long>get("id"), projectId));
       searchQuery.where(predicates.toArray(new Predicate[]{}));
       Project result =  em.createQuery(searchQuery).getSingleResult();
       if(result != null) {
    	   result.setActivities(result.getActivities());
       }
       return result;
   }
    

    /**
     *
     * save changes made to a project, or create the project if its a new project.
     *
     */
    public Project save(Project project) {
    	project.setCreatedTS(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    	return em.merge(project);
    }


    private Predicate[] getCommonWhereCondition(CriteriaBuilder cb, String username, Root<Project> searchRoot) {

        List<Predicate> predicates = new ArrayList<>();
        Join<Project, User> user = searchRoot.join("user");
        predicates.add(cb.equal(user.<String>get("username"), username));
        return predicates.toArray(new Predicate[]{});
    }

	/**
	 * Verifies whether Project Name is available for this user. 
	 * Project Name should be unique by user.
	 * @param username
	 * @param projectName
	 * @return True or False
	 */
	public boolean isProjectNameAvailable(String username, String projectName) {
		 List<Project> projects = em.createNamedQuery(Project.FindProjectByNameAndUserName, Project.class)
	                .setParameter("username", username)
	                .setParameter("projectname", projectName)
	                .getResultList();

	        return projects.isEmpty();
	}

	/**
	 * Saves a project activity.
	 * @param activity
	 */
	public void saveProjectActivity(Activity activity) {
		em.persist(activity);		
	}

}
