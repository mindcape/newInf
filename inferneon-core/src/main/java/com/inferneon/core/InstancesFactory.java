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
	
	public IInstances createInstances(String instancesID, List<Attribute> attributes, int classIndex, String sourceURI) throws Exception{
		return registeredInstanceTypes.get(instancesID).createInstances(attributes, classIndex, sourceURI);
	}
	

	public IInstances createInstances(InputStream inputStream, String instancesID, List<Attribute> attributes, int classIndex) throws Exception{
		return registeredInstanceTypes.get(instancesID).createInstances(inputStream, attributes, classIndex);
	}
	
}
