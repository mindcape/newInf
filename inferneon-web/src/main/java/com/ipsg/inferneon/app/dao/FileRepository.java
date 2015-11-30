package com.ipsg.inferneon.app.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.ipsg.inferneon.app.model.BigFile;
import com.ipsg.inferneon.app.model.Project;
@Repository
public class FileRepository {
//	 private static final Logger LOGGER = Logger.getLogger(FileRepository.class);

	@PersistenceContext
	EntityManager em;

	public List<BigFile> findFilesByProjectName(String projectName, Long pageNumber) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		// the actual search query that returns one page of results
		CriteriaQuery<BigFile> searchQuery = cb.createQuery(BigFile.class);
		Root<BigFile> searchRoot = searchQuery.from(BigFile.class);
		searchQuery.select(searchRoot);
		searchQuery.where(getCommonWhereCondition(cb, pageNumber, searchRoot));
		TypedQuery<BigFile> filterQuery = em.createQuery(searchQuery);
		return filterQuery.getResultList();
	}

	public BigFile findFileById(String projectName, Long id) {
		BigFile find = em.find(BigFile.class, id);
		return find;
	}

	private Predicate[] getCommonWhereCondition(CriteriaBuilder cb, Long projectName, Root<BigFile> searchRoot) {
		List<Predicate> predicates = new ArrayList<>();
		Join<BigFile, Project> project = searchRoot.join("project");
		predicates.add(cb.equal(project.<String> get("projectName"), projectName));
		return predicates.toArray(new Predicate[] {});
	}
	
	
}