package com.inferneon.supervised;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.exceptions.InvalidDataException;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.DecisionTreeBuilder.Criterion;

public class ID3 {
	
	private Criterion criteria;
	
	private DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> decisionTree;
	private DecisionTreeNode decisionTreeRootNode; 

	public ID3(Criterion criteria){
		this.criteria = criteria;
		decisionTree = new DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge>(DecisionTreeEdge.class);
	}
	
	public void train(Instances instances) throws CycleFoundException, InvalidDataException{
		train(null, null, instances);
	}
	
	private void train(DecisionTreeNode parentDecisionTreeNode, DecisionTreeEdge decisionTreeEdge, Instances instances)
			throws CycleFoundException, InvalidDataException{
		FrequencyCounts frequencyCounts = DataLoader.getFrequencyCounts(instances);
		Map<Value, Double> targetClassCounts = frequencyCounts.getTargetCounts();		
		if(targetClassCounts.size() == 1){
			// All instances belong to the same class, entropy will be zero
			Value targetValue = targetClassCounts.keySet().iterator().next();
			createLeavesForAttribute(parentDecisionTreeNode, decisionTreeEdge, targetValue, instances.sumOfWeights(), targetClassCounts);
			return;
		}
		
		List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCountList = frequencyCounts.getValueAndTargetClassCount();

		Double entropyOfTrainingSample = computeEntropy(instances.sumOfWeights(), targetClassCounts);
		Attribute attribute = searchForBestAttributeToSplitOn(instances, valueAndTargetClassCountList, entropyOfTrainingSample, frequencyCounts);

		DecisionTreeNode decisionTreeNode = new DecisionTreeNode(attribute, instances.sumOfWeights(), targetClassCounts);
		decisionTree.addVertex(decisionTreeNode);

		if(parentDecisionTreeNode == null){
			decisionTreeRootNode = decisionTreeNode;
			parentDecisionTreeNode = decisionTreeNode;
		}

		if(decisionTreeEdge != null){
			System.out.println("Adding edge " + decisionTreeEdge + " between attributes " + parentDecisionTreeNode + " and " + decisionTreeNode);
			
			decisionTreeEdge.setSource(parentDecisionTreeNode); decisionTreeEdge.setTarget(decisionTreeNode);
			decisionTree.addDagEdge(parentDecisionTreeNode, decisionTreeNode, decisionTreeEdge);
		}

		Map<DecisionTreeEdge, Instances>  instancesSplitForAttribute = splitOn(attribute, instances);

		if(instancesSplitForAttribute.size() > 1){

			Set<Entry<DecisionTreeEdge, Instances>> splits = instancesSplitForAttribute.entrySet();
			Iterator<Entry<DecisionTreeEdge, Instances>> iterator = splits.iterator();
			while(iterator.hasNext()){
				Entry<DecisionTreeEdge, Instances> entry = iterator.next();
				DecisionTreeEdge dtEdge = entry.getKey();
				Instances splitInstances = entry.getValue();			
				Instances newInstances = splitInstances.removeAtribute(attribute);			
				train(decisionTreeNode, dtEdge, newInstances);
			}
		}
	}
	
	private Map<DecisionTreeEdge, Instances> splitOn(Attribute attribute, Instances instances){

		Map<DecisionTreeEdge, Instances> valueAndInstancesHavingValue = new HashMap<DecisionTreeEdge, Instances>();
		List<Attribute> attributes = instances.getAttributes();
		Iterator<Instance> iterator = instances.iterator();		
		Map<Value, DecisionTreeEdge> valueAndDecisionTreeEdge = new HashMap<>();
				
		while(iterator.hasNext()){
			Instance instance = iterator.next();
			Value value = instance.attributeValue(attribute);
			
			DecisionTreeEdge decisionTreeEdge = valueAndDecisionTreeEdge.get(value);
			if(decisionTreeEdge == null){
				decisionTreeEdge = new DecisionTreeEdge(value);
				valueAndDecisionTreeEdge.put(value, decisionTreeEdge);
			}
			
			Instances instancesHavingValue = valueAndInstancesHavingValue.get(decisionTreeEdge);
			if(instancesHavingValue == null){
				instancesHavingValue = new Instances();
				instancesHavingValue.setAttributes(attributes);
			}
			instancesHavingValue.addInstance(instance);
			
			valueAndInstancesHavingValue.put(decisionTreeEdge,  instancesHavingValue);			
		}

		return valueAndInstancesHavingValue;
	}

	private void createLeavesForAttribute(DecisionTreeNode decisionTreeNode, DecisionTreeEdge edge,
			Value targetValue, Double instanceSize, Map<Value, Double> targetClassCounts) throws CycleFoundException {
		DecisionTreeNode leafNode = new DecisionTreeNode(targetValue, instanceSize, targetClassCounts);
		decisionTree.addVertex(leafNode);
		edge.setSource(decisionTreeNode); edge.setTarget(leafNode);
		System.out.println("Adding edge " + edge + " between attributes " + decisionTreeNode + " and " + leafNode);		
		decisionTree.addDagEdge(decisionTreeNode, leafNode, edge);		
	}

	private Attribute searchForBestAttributeToSplitOn(Instances instances,
			List<Map<Value, Map<Value, Double>>> valueAndTargetClassCountList,
			Double entropyOfTrainingSample, FrequencyCounts frequencyCounts) {

		Double maxInfoGain = 0.0;
		Attribute attrWithMaxInfoGain = null;

		int attributeCount = 0;
		List<Attribute> attributes = instances.getAttributes();
		for(Attribute attribute : attributes){

			if(attributeCount == instances.getClassIndex()){
				continue;
			}

			Map<Value, Map<Value, Double>> targetClassCount = valueAndTargetClassCountList.get(attributeCount);

			Double infoGainForAttribute = getInfoGain(attribute, instances, targetClassCount, entropyOfTrainingSample, frequencyCounts);

			System.out.println("Info gain for attr: " + attribute.getName() + " = " + infoGainForAttribute);

			if(Double.compare(infoGainForAttribute, maxInfoGain) > 0){
				maxInfoGain = infoGainForAttribute;
				attrWithMaxInfoGain = attribute;
			}			
			attributeCount++;
		}

		System.out.println("**************************************************** Max = " + attrWithMaxInfoGain.getName());

		return attrWithMaxInfoGain;
	}

	private Double getInfoGain(Attribute attribute, Instances instances, Map<Value, Map<Value, Double>> targetClassCount,
			Double entropyOfTrainingSample, FrequencyCounts frequencyCounts) {
		Double weightedEntropy = 0.0;

		Iterator<Entry<Value, Map<Value, Double>>> iterator = targetClassCount.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Map<Value, Double>> entry = iterator.next();
			Value val = entry.getKey();

			Double totalOccurencesOfValue = (double)frequencyCounts.getAttributeValueCounts().get(attribute).get(val);

			Map<Value, Double> counts = entry.getValue();	

			Double entropyForVal = computeEntropy(totalOccurencesOfValue, counts);
			Double weightRatio = ((double)totalOccurencesOfValue) /((double) instances.size());
			weightedEntropy += weightRatio * entropyForVal;

		}

		return entropyOfTrainingSample - weightedEntropy;
	}

	private Double computeEntropy(Double numInstances, Map<Value, Double> targetClassCounts) {

		Double entropy = 0.0;

		Iterator<Entry<Value, Double>> iterator = targetClassCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Double count = entry.getValue();			
			Double ratio = (double) count / (double) numInstances;			
			entropy -= ratio * (Math.log(ratio) / Math.log(2));
		}

		return entropy;
	}
	
	public DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> getDecisionTree(){
		return decisionTree;
	}
	
	
	public DecisionTreeNode getDecisionTreeRootNode() {
		return decisionTreeRootNode;
	}
}
