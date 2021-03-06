package com.ipsg.inferneon.app.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

	public Algorithm loadAlgorithmById(Long algorithmId) {
		Algorithm algorithm = em.createNamedQuery(Algorithm.LoadAlgorithmById, Algorithm.class)
				.setParameter("algorithmId", algorithmId).getSingleResult();
		return algorithm;
	}
	
	
	public List<Algorithm> loadAllAlgorithms() {
		List<Algorithm> algorithmList  = em.createNamedQuery(Algorithm.LoadAll, Algorithm.class).getResultList();
				
		return algorithmList;
	}
	

}
