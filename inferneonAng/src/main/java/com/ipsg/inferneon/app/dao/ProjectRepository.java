package com.ipsg.inferneon.app.dao;


import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.ipsg.inferneon.app.model.Project;
import com.ipsg.inferneon.app.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public Long countProjectsByDateTime(String username, Date fromDate, Date toDate, Time fromTime, Time toTime) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // query for counting the total results
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Project> countRoot = cq.from(Project.class);
        cq.select((cb.count(countRoot)));
        cq.where(getCommonWhereCondition(cb, username, countRoot, fromDate, toDate, fromTime, toTime));
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
    public List<Project> findProjectsByDateTime(String username, Date fromDate, Date toDate,
                                          Time fromTime, Time toTime, int pageNumber) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // the actual search query that returns one page of results
        CriteriaQuery<Project> searchQuery = cb.createQuery(Project.class);
        Root<Project> searchRoot = searchQuery.from(Project.class);
        searchQuery.select(searchRoot);
        searchQuery.where(getCommonWhereCondition(cb, username, searchRoot, fromDate, toDate, fromTime, toTime));

        List<Order> orderList = new ArrayList();
        orderList.add(cb.desc(searchRoot.get("date")));
        orderList.add(cb.asc(searchRoot.get("time")));
        searchQuery.orderBy(orderList);

        TypedQuery<Project> filterQuery = em.createQuery(searchQuery)
                .setFirstResult((pageNumber - 1) * 10)
                .setMaxResults(10);

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
    public Project findProjectById(Long id) {
        return em.find(Project.class, id);
    }

    /**
     *
     * save changes made to a project, or create the project if its a new project.
     *
     */
    public Project save(Project project) {
        return em.merge(project);
    }


    private Predicate[] getCommonWhereCondition(CriteriaBuilder cb, String username, Root<Project> searchRoot, Date fromDate, Date toDate,
                                                Time fromTime, Time toTime) {

        List<Predicate> predicates = new ArrayList<>();
        Join<Project, User> user = searchRoot.join("user");

        predicates.add(cb.equal(user.<String>get("username"), username));
        predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("date"), fromDate));

        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("date"), toDate));
        }

        if (fromTime != null) {
            predicates.add(cb.greaterThanOrEqualTo(searchRoot.<Date>get("time"), fromTime));
        }

        if (toTime != null) {
            predicates.add(cb.lessThanOrEqualTo(searchRoot.<Date>get("time"), toTime));
        }

        return predicates.toArray(new Predicate[]{});
    }

}
