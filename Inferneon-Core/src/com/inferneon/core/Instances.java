package com.inferneon.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.inferneon.core.Value.ValueType;

public class Instances {

	private List<Attribute> attributes;
	private List<Instance> instances;
	private int classIndex;

	public Instances(){
		instances = new ArrayList<>();
	}

	public List<Instance> getInstances() {
		return instances;
	}

	public Instance getInstance(int index){
		return instances.get(index);
	}
	
	public void setInstances(List<Instance> instances) {
		this.instances = instances;
	}

	public List<Attribute> getAttributes(){
		return attributes;
	}

	public long size(){
		return instances.size();
	}

	public void addInstance(Instance instance) {
		instances.add(instance);
	}

	public void setAttributes(List<Attribute> attributes){
		this.attributes = attributes;
		classIndex = attributes.size() -1;   // Default
	}

	public int getClassIndex() {
		return classIndex;
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	public int numClasses(){
		Attribute classAttribute = attributes.get(classIndex);
		return classAttribute.getNumValues();
	}
	
	public List<Instance> getInstances(Attribute attribute, Value attributeValue){

		List<Instance> instancesWithAttributeValue = new ArrayList<>();

		for(Instance instance : this.instances){
			Value value = instance.attributeValue(attribute);
			if(value == attributeValue){
				instancesWithAttributeValue.add(instance);
			}
		}

		return instancesWithAttributeValue;
	}

	public Instances getSubset(Attribute attribute, List<Value> values) {		
		Instances instances = new Instances();
		instances.setAttributes(attributes);
		outer: for(Instance instance : this.instances){
			Value value = instance.getValue(attributes.indexOf(attribute));

			for(Value referenceValue : values){
				if(Value.valuesAreIdentical(value, referenceValue)){
					instances.addInstance(instance);
					continue outer;
				}
			}
		}
		return instances;
	}
	
	public Instances removeAtribute(Attribute attribute) {
		Instances newInstances = new Instances();

		// Create a new values list without a value for the given attribute		
		List<Instance> newInstancesList = new ArrayList<>();
		for(Instance inst : instances){
			Value valueOfAttribute = inst.attributeValue(attribute);

			List<Value> newValues = new ArrayList<>();

			List<Value> valuesInInstance = inst.getValues();
			for(Value valInInstance : valuesInInstance){
				if(valInInstance != valueOfAttribute){
					newValues.add(valInInstance);
				}
			}

			Instance newInst = new Instance(newValues);
			newInstancesList.add(newInst);			
		}

		// Create a new attribute list without the list of attributes
		List<Attribute> newAttributes = new ArrayList<>();
		for(Attribute attr : attributes){
			if(attr != attribute){
				newAttributes.add(attr);
			}
		}

		newInstances.setAttributes(newAttributes);
		newInstances.setInstances(newInstancesList);
		return newInstances;
	}
	
	public Long getNumInstancesWithKnownValues(Attribute attribute){
		
		long knownValues = 0;
		for(Instance instance : this.instances){
			Value value = instance.getValue(attributes.indexOf(attribute));

			if(value.getType() != ValueType.MISSING){
				knownValues++;
			}
		}
		return knownValues;
		
	}
	
	public Iterator<Instance> iterator(){
		return instances.iterator();
	}
	
	public int attributeIndex(Attribute attribute){
		return attributes.indexOf(attribute);
	}
	
	@Override
	public String toString(){
		String description = attributes.size() + " attributes, " + instances.size() + " instances" 
				+ System.getProperty("line.separator");
		
		for(Attribute attribute : attributes){
			description += "@attribute " + attribute + System.getProperty("line.separator");
		}
		
		description += System.getProperty("line.separator");
		
		description += "@data" + System.getProperty("line.separator");
		for(Instance instance : instances){
			description += instance.toString() + System.getProperty("line.separator");
		}
		return description.trim();
	}

	public Double sumOfWeights() {
		double sum = 0L;
		for(Instance inst: instances){
			sum += inst.getWeight();
		}
		
		return sum;
	}
}
