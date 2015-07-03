package com.inferneon.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstancesFactory {
	
	private static InstancesFactory FACTORY_INSTANCE;
	
	private Map<String, IInstances> registeredInstanceTypes;
	
	private InstancesFactory(){
		registeredInstanceTypes = new HashMap<String, IInstances>();
	}
	
	public static InstancesFactory getInstance(){
		if(FACTORY_INSTANCE == null){
			FACTORY_INSTANCE = new InstancesFactory();
		}
		
		return FACTORY_INSTANCE;
	}
	
	public void registerProduct(String instancesID, IInstances instances)    {
		registeredInstanceTypes.put(instancesID, instances);
	}
	
	/**
	 * Used to create instances when attributes and the class index are known. The sourceURI points to a
	 * location of the CSV file.
	 * @param instancesID
	 * @param attributes
	 * @param classIndex
	 * @param sourceURI
	 * @return
	 * @throws Exception
	 */
	public IInstances createInstances(String instancesID, List<Attribute> attributes, int classIndex, String csvSourceURI) throws Exception{
		return registeredInstanceTypes.get(instancesID).createInstances(attributes, classIndex, csvSourceURI);
	}	
}
