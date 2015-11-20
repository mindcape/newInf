package com.ipsg.inferneon.app.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.ipsg.inferneon.app.model.Algorithm;

@Repository
public class AlgorithmRepository {
	
	
	//private static final Logger LOGGER = Logger.getLogger(AlgorithmRepository.class);
	
	@PersistenceContext
	EntityManager em;
	
	public Algorithm loadAlgorithmWithFormFields(String algorithmName) {
		Algorithm algorithm = em.createNamedQuery(Algorithm.LoadAlgorithFormFieldsByName, Algorithm.class)
				.setParameter("algorithmname", algorithmName).getSingleResult();

		return algorithm;
	}

}
