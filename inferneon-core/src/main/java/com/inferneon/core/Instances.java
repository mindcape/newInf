package com.inferneon.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.inferneon.core.Value.ValueType;
import com.inferneon.core.exceptions.InvalidDataException;
import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;
import com.inferneon.core.matrices.IMatrix;
import com.inferneon.core.matrices.Matrix;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.functions.MultilayerNeuralNetwork;
import com.inferneon.supervised.functions.NeuralConnnection;
import com.inferneon.supervised.functions.NeuralNode;
import com.inferneon.supervised.functions.NeuralNode.TYPE;

public class Instances extends IInstances {

	// Register this class with the factory
	static
	{
		InstancesFactory factory = InstancesFactory.getInstance();
		factory.registerProduct("STAND_ALONE", new Instances(Context.STAND_ALONE));
	}

	private List<Attribute> attributes;
	private List<Instance> instances;
	private Instance currentInstance;
	private int classIndex;
	private int attributeIndex;   // Attribute index on which these instances are sorted on.
	private Value thresholdValueOfSplitsInOrderedList;

	public Instances(Context context){
		super(context);
		instances = new ArrayList<>();
	}

	public Instances(Context context, List<Instance> instances, List<Attribute> attributes, int classIndex){
		super(context);
		this.instances = instances;
		this.attributes = attributes;
		this.classIndex = classIndex;
	}

	public List<Instance> getInstances() {
		return instances;
	}

	@Override
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

	@Override
	public IInstances removeAttribute(Attribute attribute) {
		Instances newInstances = new Instances(context);

		// Create a new values list without a value for the given attribute		
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
			newInstances.addInstance(newInst);			
		}

		// Create a new attribute list without the list of attributes
		List<Attribute> newAttributes = new ArrayList<>();
		for(Attribute attr : attributes){
			if(attr != attribute){
				newAttributes.add(attr);
			}
		}

		newInstances.setAttributes(newAttributes);

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

	public Double sumOfWeights(long startIndex, long endIndex) {
		double sum = 0L;

		for(long i = startIndex; i < endIndex; i++){
			Instance inst = instances.get((int)i);
			sum += inst.getWeight();
		}

		return sum;
	}

	@Override
	public FrequencyCounts getFrequencyCounts(){

		FrequencyCounts frequencyCounts = new FrequencyCounts();

		List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCountList = new ArrayList<>(); 

		Map<Attribute, Map<Value, Double>> attributeAndTargetClassCounts = new HashMap<>();

		Map<Attribute, Long> attributeAndNonMissingValueCount = new HashMap<>();

		Map<Attribute, Map<Value, Double>> attributeValueCounts = new HashMap<>();

		Map<Attribute, IInstances> attributeAndMissingValueInstances = new HashMap<>();

		Map<Attribute, Double> attributeAndMissingValueInstancesSumOfWts = new HashMap<>();

		Map<Attribute, Integer> attributeAndMissingInstanceSizes = new HashMap<Attribute, Integer>();

		Set<Instance> instancesWithMissingvalues = new HashSet<>();

		Map<Value, Double> totalTargetCounts = new HashMap<>();

		Double sumOfWeights = 0.0;

		int numAttributes = attributes.size();
		for(int attributeIndex = 0; attributeIndex < numAttributes -1; attributeIndex++){

			Attribute currentAttribute = attributes.get(attributeIndex);
			Map<Value, Map<Value, Double>>  valueAndTargetClassCount = new HashMap<>();

			for(Instance instance: instances){
				Value value = instance.getValue(attributeIndex);
				Value targetClassValue = instance.getValue(attributes.size() -1);

				if(attributeIndex == 0){
					Double instanceWeight = instance.getWeight();
					sumOfWeights += instanceWeight;
					Double targetValueCount = totalTargetCounts.get(targetClassValue);
					if(targetValueCount == null){
						totalTargetCounts.put(targetClassValue, instanceWeight);
					}
					else{
						totalTargetCounts.put(targetClassValue, targetValueCount + instanceWeight);
					}
				}

				if(value.getType() == ValueType.MISSING){					
					IInstances missingValueInstances = attributeAndMissingValueInstances.get(currentAttribute);
					Double currentSumOfWtsOfMissingValuInstancesForAttr = attributeAndMissingValueInstancesSumOfWts.get(currentAttribute);
					if(currentSumOfWtsOfMissingValuInstancesForAttr == null){
						currentSumOfWtsOfMissingValuInstancesForAttr = 0.0;
					}
					if(missingValueInstances == null){
						missingValueInstances = new Instances(context, new ArrayList<Instance>(), attributes, classIndex);
					}

					Integer currentNumMissingForAttr = attributeAndMissingInstanceSizes.get(currentAttribute);
					if(currentNumMissingForAttr == null){
						currentNumMissingForAttr = 0;
					}

					missingValueInstances.addInstance(instance);
					attributeAndMissingValueInstances.put(currentAttribute, missingValueInstances);
					attributeAndMissingValueInstancesSumOfWts.put(currentAttribute, currentSumOfWtsOfMissingValuInstancesForAttr 
							+ instance.getWeight());
					attributeAndMissingInstanceSizes.put(currentAttribute, currentNumMissingForAttr + 1);

					instancesWithMissingvalues.add(instance);
					continue;
				}

				Map<Value, Double> targetClassCount = valueAndTargetClassCount.get(value);
				if(targetClassCount == null){
					targetClassCount = new HashMap<>();
					targetClassCount.put(targetClassValue, instance.getWeight());
					valueAndTargetClassCount.put(value, targetClassCount);
				}
				else{
					Double currentCountForTarget = targetClassCount.get(targetClassValue);
					if(currentCountForTarget == null){
						targetClassCount.put(targetClassValue, instance.getWeight());
					}
					else{
						targetClassCount.put(targetClassValue, currentCountForTarget + instance.getWeight());
					}				
				}

				Map<Value, Double> targetClassCountsForAttribute = attributeAndTargetClassCounts.get(currentAttribute);
				if(targetClassCountsForAttribute == null){
					targetClassCountsForAttribute = new HashMap<>();
					targetClassCountsForAttribute.put(targetClassValue, instance.getWeight());
					attributeAndTargetClassCounts.put(currentAttribute, targetClassCountsForAttribute);

				}
				else{
					Double currentTargetCountForAttribute = targetClassCountsForAttribute.get(targetClassValue);
					if(currentTargetCountForAttribute == null){
						targetClassCountsForAttribute.put(targetClassValue, instance.getWeight());
					}
					else{
						targetClassCountsForAttribute.put(targetClassValue, currentTargetCountForAttribute + instance.getWeight());
					}
				}	
				Long nonMissingValueCountForAttribute = attributeAndNonMissingValueCount.get(currentAttribute); 
				if(nonMissingValueCountForAttribute == null){
					attributeAndNonMissingValueCount.put(currentAttribute, new Long(1));
				}
				else{
					attributeAndNonMissingValueCount.put(currentAttribute, nonMissingValueCountForAttribute + 1);
				}

				// Update the attribute's value count
				Map<Value, Double> attrValCount = attributeValueCounts.get(currentAttribute);
				if(attrValCount == null){
					attrValCount = new HashMap<>();
					attrValCount.put(value, instance.getWeight());
				}
				else{
					Double currentCountForAttrVal = attrValCount.get(value);
					if(currentCountForAttrVal == null){
						attrValCount.put(value, instance.getWeight());
					}
					else{
						attrValCount.put(value, currentCountForAttrVal + instance.getWeight());
					}	
				}
				attributeValueCounts.put(currentAttribute, attrValCount);
			}

			valueAndTargetClassCountList.add(valueAndTargetClassCount);
		}

		Value maxTargetValue = DataLoader.getMaxTargetValue(totalTargetCounts);
		Double maxTargetValueCount = 0.0;
		if(maxTargetValue != null){
			maxTargetValueCount = totalTargetCounts.get(maxTargetValue);
		}

		frequencyCounts.setTotalTargetCounts(totalTargetCounts);
		frequencyCounts.setSumOfWeights(sumOfWeights);
		frequencyCounts.setMaxTargetValue(maxTargetValue);
		frequencyCounts.setMaxTargetValueCount(maxTargetValueCount);
		frequencyCounts.setAttributeAndTargetClassCounts(attributeAndTargetClassCounts);
		frequencyCounts.setAttributeAndNonMissingValueCount(attributeAndNonMissingValueCount);
		frequencyCounts.setValueAndTargetClassCount(valueAndTargetClassCountList);
		frequencyCounts.setNumInstances((long)instances.size());
		frequencyCounts.setAttributeValueCounts(attributeValueCounts);
		frequencyCounts.setAttributeAndMissingValueInstances(attributeAndMissingValueInstances);
		frequencyCounts.setAttributeAndMissingValueInstanceSumOfWeights(attributeAndMissingValueInstancesSumOfWts);
		frequencyCounts.setAttributeAndMissingValueInstanceSizes(attributeAndMissingInstanceSizes);
		frequencyCounts.setTotalInstancesWithMissingValues(getSumOfWeights(instancesWithMissingvalues));

		return frequencyCounts;
	}

	@Override
	public IInstances createInstances(List<Attribute> attributes, int classIndex, String sourceURI) throws IOException, InvalidDataException{		
		if(sourceURI == null){
			IInstances instances =  new Instances(Context.STAND_ALONE, new ArrayList<Instance>(), attributes, classIndex);
			return instances;
		}

		InputStream inputStream = new FileInputStream(sourceURI);
		return loadData(attributes, inputStream, false);
	}

	@Override
	public Map<Value, IInstances> splitOnAttribute(Attribute attribute) {

		Map<Value, IInstances> valueAndInstancesHavingValue = new HashMap<Value, IInstances>();
		Iterator<Instance> iterator = instances.iterator();		

		while(iterator.hasNext()){
			Instance instance = iterator.next();
			Value value = instance.getValue(attributeIndex(attribute));

			if(value.getType() == ValueType.MISSING){
				continue;
			}

			IInstances instancesHavingValue = valueAndInstancesHavingValue.get(value);
			if(instancesHavingValue == null){
				instancesHavingValue = new Instances(context);
				instancesHavingValue.setAttributes(attributes);
			}
			instancesHavingValue.addInstance(instance);
			valueAndInstancesHavingValue.put(value,  instancesHavingValue);			
		}

		return valueAndInstancesHavingValue;
	}

	public Double getSumOfWeights(Set<Instance> instances){
		double sum = 0L;
		for(Instance inst: instances){
			sum += inst.getWeight();
		}

		return sum;
	}

	@Override
	public Double getMaxValueLesserThanOrEqualTo(double thresholdValue, Attribute attribute){
		Double result = 0.0;

		int attributeIndex = attributeIndex(attribute);
		for(Instance inst: instances){

			Value value = inst.getValue(attributeIndex);
			if(value.getType() == ValueType.MISSING){
				continue;
			}

			double valInData = value.getNumericValueAsDouble();
			if(Double.compare(thresholdValue, valInData) < 0){
				continue;
			}

			if(Double.compare(valInData, result) >= 0){
				result = valInData;
			}
		}

		return result;
	}

	@Override
	public void sort(Attribute attribute) {
		Collections.sort(instances, new InstanceComparator(attribute, attributes));
		this.attributeIndex  = attributes.indexOf(attribute);		
	}

	@Override
	public IInstances getSubList(long fromIndex, long toIndex) {
		// In the stand-alone mode, integer should suffice on the instances collection
		List<Instance> insts = new ArrayList<>(instances.subList((int)fromIndex, (int)toIndex));

		Instance thresholdInstance = instances.get((int)toIndex -1);
		thresholdValueOfSplitsInOrderedList = thresholdInstance.getValue(attributeIndex);

		return new Instances(context, insts,  attributes, classIndex);		
	}

	@Override
	public Value valueOfAttributeAtInstance(long index, int attributeIndex) {
		return instances.get((int)index).getValue(attributeIndex);	
	}

	@Override
	public void appendAll(IInstances other, double weightFactor){
		Instances otherInsts = (Instances) other;
		List<Instance> otherInstList = otherInsts.getInstances();
		for(Instance instance : otherInstList){
			Double weight = weightFactor * instance.getWeight();
			Instance newInstance = new Instance(instance.getValues());
			newInstance.setWeight(weight);
			addInstance(newInstance);
		}
	}

	private IInstances loadData(List<Attribute> attributes, InputStream inputStream, boolean removeFirstRow) throws IOException, InvalidDataException{

		CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));

		Instances instances = new Instances(Context.STAND_ALONE);

		List<String[]> rowsList = null;

		rowsList = csvReader.readAll();
		if(removeFirstRow){
			rowsList.remove(0);
		}

		int count = 0;
		for(String[] row : rowsList) {

			if(row.length != attributes.size()){
				csvReader.close();
				throw new InvalidDataException("Invalid data in row number: " + count);
			}

			int attrCount = 0;
			List<Value> valuesForInstance = new ArrayList<>();
			for(Attribute attribute : attributes){
				Value val = null;
				String strValue = row[attrCount++].trim();
				if(strValue.equals("?")){
					val = new Value();
					valuesForInstance.add(val);
					continue;
				}

				Attribute.Type attributeType = attribute.getType();
				if(attributeType == Attribute.Type.NOMINAL){
					val = attribute.getNominalValueForName(strValue);
					if(val == null){
						csvReader.close();
						throw new InvalidDataException("Unknown nominal value for attribute " + attribute.getName() + " with value " + strValue + " at row " + count);
					}
				}
				else if(attributeType == Attribute.Type.NUMERIC){
					try{
						Long number = Long.valueOf(strValue);
						val = new Value(number);
					}
					catch(NumberFormatException nfe1){
						try{
							Double realNumber = Double.valueOf(strValue);
							val = new Value(realNumber);
						}
						catch(NumberFormatException nfe2){		
							csvReader.close();
							throw new InvalidDataException("Invalid value for numeric attribute " + attribute.getName() + " with value " + strValue + " at row " + count);
						}
					}

				}
				valuesForInstance.add(val);
			}

			Instance instance = new Instance(valuesForInstance);
			instances.addInstance(instance);

			count++;			
		}

		csvReader.close();

		instances.setAttributes(attributes);

		return instances;		
	}

	@Override
	public String getContextId() {
		return "STAND_ALONE";
	}

	@Override
	public Map<Value, Double> getTargetClassCounts() {
		return getFrequencyCounts().getTotalTargetCounts();
	}

	@Override
	public long getNumOccurrencesOfValueInOrderedList(Value value) {
		Long numOccurrences = 0L;
		for(Instance inst : instances){
			Value valueOfAttrInInst = inst.getValue(attributeIndex);
			if(value.equals(valueOfAttrInInst)){
				numOccurrences++;
			}
		}

		return numOccurrences;
	}

	@Override
	public Value getThresholdValueOfSplitsInOrderedList() {
		return thresholdValueOfSplitsInOrderedList;
	}

	@Override
	public IMatrix matrix(long startRowIndex, long startColumnIndex, long endRowIndex, long endColumnIndex)
			throws MatrixElementIndexOutOfBounds{
		int numRows = (int) (endRowIndex - startRowIndex +1);
		int numColumns = (int) (endColumnIndex - startColumnIndex +1);
		double data[][] = new double[numRows][numColumns];
		for(int i = (int)startRowIndex; i < (int) endRowIndex; i++){
			Instance inst = instances.get(i);
			double vals[] = new double[numColumns];
			for(int j = 0; j < numColumns; j++){
				Value value = inst.getValue(i);
				vals[j] = value.getNumericValueAsDouble();
			}

			data[i] = vals;
		}

		Matrix matrix = new Matrix(data);
		return matrix;
	}

	@Override
	public IMatrix[] matrixAndClassVector(boolean regularize){
		int numRows = instances.size();
		int totalAttributes = attributes.size();

		int numColumnsOfMatrix = totalAttributes -1;

		double data[][] = new double[numRows][numColumnsOfMatrix];		
		Matrix valuesMat = new Matrix(data);

		double classVectorData[][] = new double[numRows][1];
		Matrix classVector = new Matrix(classVectorData);

		for(int i = 0; i < numRows; i++){
			Instance inst = instances.get(i);
			double vals[] = new double[numColumnsOfMatrix];
			double targetValues[] = new double[1];

			for(int j = 0; j < totalAttributes; j++){
				Value value = inst.getValue(j);

				if(j == classIndex){
					if(value.getType() == Value.ValueType.MISSING){
						targetValues[0] = Double.NaN;
					}
					else{
						targetValues[0] = value.getNumericValueAsDouble();
					}
				}
				else{
					vals[j] = value.getNumericValueAsDouble();
				}
			}
			classVectorData[i] = targetValues;
			data[i] = vals;
		}

		IMatrix matrixAndClassVector[] = new IMatrix[2];
		if(regularize){
			valuesMat = (Matrix)valuesMat.normalize(true, true);
			classVector = (Matrix) classVector.normalize(true, false);
		}

		matrixAndClassVector[0] = valuesMat;
		matrixAndClassVector[1] = classVector;

		return matrixAndClassVector;
	}

	@Override
	public double trainNeuralNetwork(MultilayerNeuralNetwork network) {

		for(int ins=0; ins<instances.size(); ins++){
			currentInstance = instances.get(ins);
			for(int a = 0; a < attributes.size(); a++){
				List<NeuralNode> inputNodes = network.getInputNodes();
				for(int inp = 0; inp < inputNodes.size(); inp++) {
					inputNodes.get(inp).setOutput(currentInstance.getValue(a).getNumber());
				}
				List<List<NeuralNode>> hiddenLayers = network.getHiddenLayers();
				for(int i = 0; i < hiddenLayers.size(); i++) {
					List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
					for( int j = 0; j < hiddenNodes.size(); j++){
						double totalOutput = 0d;
						NeuralNode hiddenNode = hiddenNodes.get(j);
						Set<NeuralConnnection> inputConnections = network.incomingEdgesOf(hiddenNode);
						Iterator<NeuralConnnection> connectionIterator = inputConnections.iterator();
						for (int c = 0; connectionIterator.hasNext(); c++){
							NeuralConnnection connection = connectionIterator.next();
							double weights[] = hiddenNode.getWeights();
							double value = weights[0];
							NeuralNode sourceNode = (NeuralNode) connection.getSource();
							totalOutput += value*sourceNode.getOutput()*weights[0+1];
						}
						hiddenNode.setOutput(totalOutput);
					}
				}
				List<NeuralNode> outputNodes = network.getOutputNodes();
				for(int op = 0; op < outputNodes.size(); op++) {
					NeuralNode outputNode = outputNodes.get(op);
					Set<NeuralConnnection> inutConnections = network.incomingEdgesOf(outputNode);
					Iterator<NeuralConnnection> connectionIterator = inutConnections.iterator();
					double totalOutput = 0d;
					for (int c = 0; connectionIterator.hasNext(); c++){
						NeuralConnnection connection = connectionIterator.next();
						double weight = connection.getWeight();
						NeuralNode sourceNode = (NeuralNode) connection.getSource();
						double weights[] = sourceNode.getWeights();
						double value = weights[0];

						totalOutput += value*sourceNode.getOutput()*weights[0+1];
						//	totalOutput += weight*sourceNode.getOutput();
					}
					outputNode.setOutput(totalOutput);
				}
				for(int inp = 0; inp < inputNodes.size(); inp++) {
					NeuralNode inpuNode = inputNodes.get(inp);
					System.out.println("output "+inp+": "+inpuNode.getOutput());
				}
				for(int i = 0; i < hiddenLayers.size(); i++) {
					List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
					for( int j = 0; j < hiddenNodes.size(); j++){

						NeuralNode hiddenNode = hiddenNodes.get(j);
						System.out.println("hiddenNode "+j+": "+hiddenNode.getOutput());
					}
				}
				for(int op = 0; op < outputNodes.size(); op++) {
					NeuralNode outputNode = outputNodes.get(op);
					System.out.println("output "+op+": "+outputNode.getOutput());
				}

			}
		}
		return 0;
	}
}
