package com.inferneon.supervised.decisiontree;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.IInstances.Context;
import com.inferneon.core.Value;
import com.inferneon.core.exceptions.InvalidDataException;
import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.decisiontree.DecisionTreeBuilder.Criterion;

public class ID3 {

	// Context in which this program is running. Default is stand alone mode.
	private Context context = Context.STAND_ALONE;
	
	private Criterion criteria;

	private DecisionTree decisionTree;

	public ID3(Criterion criteria){
		this.criteria = criteria;
		decisionTree = new DecisionTree(DecisionTreeEdge.class);
	}

	public void train(IInstances instances) throws CycleFoundException, InvalidDataException{
		train(null, null, instances);
	}

	private void train(DecisionTreeNode parentDecisionTreeNode, DecisionTreeEdge decisionTreeEdge, IInstances instances)
			throws CycleFoundException, InvalidDataException{
		FrequencyCounts frequencyCounts = instances.getFrequencyCounts();
		Map<Value, Double> targetClassCounts = frequencyCounts.getTotalTargetCounts();
		if(targetClassCounts.size() == 1){
			// All instances belong to the same class, entropy will be zero
			Value targetValue = targetClassCounts.keySet().iterator().next();
			createLeavesForAttribute(parentDecisionTreeNode, decisionTreeEdge, targetValue, frequencyCounts, targetClassCounts);
			return;
		}

		List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCountList = frequencyCounts.getValueAndTargetClassCount();

		Double entropyOfTrainingSample = computeEntropy(instances.sumOfWeights(), targetClassCounts);
		Attribute attribute = searchForBestAttributeToSplitOn(instances, valueAndTargetClassCountList, entropyOfTrainingSample,
							frequencyCounts);

		DecisionTreeNode decisionTreeNode = new DecisionTreeNode(frequencyCounts, attribute);
		decisionTree.addVertex(decisionTreeNode);

		if(parentDecisionTreeNode == null){
			decisionTree.setDecisionTreeRootNode(decisionTreeNode);
			parentDecisionTreeNode = decisionTreeNode;
		}

		if(decisionTreeEdge != null){
			System.out.println("Adding edge " + decisionTreeEdge + " between attributes " + parentDecisionTreeNode + " and " + decisionTreeNode);

			decisionTreeEdge.setSource(parentDecisionTreeNode); decisionTreeEdge.setTarget(decisionTreeNode);
			decisionTree.addDagEdge(parentDecisionTreeNode, decisionTreeNode, decisionTreeEdge);
		}

		Map<Value, IInstances>  instancesSplitForAttribute = instances.splitOnAttribute(attribute);

		if(instancesSplitForAttribute.size() > 1){

			Set<Entry<Value, IInstances>> splits = instancesSplitForAttribute.entrySet();
			Iterator<Entry<Value, IInstances>> iterator = splits.iterator();
			while(iterator.hasNext()){
				Entry<Value, IInstances> entry = iterator.next();
				DecisionTreeEdge dtEdge = new DecisionTreeEdge(entry.getKey());
				IInstances splitInstances = entry.getValue();			
				IInstances newInstances = splitInstances.removeAttribute(attribute);			
				train(decisionTreeNode, dtEdge, newInstances);
			}
		}
	}
	
	private void createLeavesForAttribute(DecisionTreeNode decisionTreeNode, DecisionTreeEdge edge,
			Value targetValue, FrequencyCounts frequencyCounts, Map<Value, Double> targetClassCounts) throws CycleFoundException {
		DecisionTreeNode leafNode = new DecisionTreeNode(frequencyCounts, targetValue);
		decisionTree.addVertex(leafNode);
		edge.setSource(decisionTreeNode); edge.setTarget(leafNode);
		System.out.println("Adding edge " + edge + " between attributes " + decisionTreeNode + " and " + leafNode);		
		decisionTree.addDagEdge(decisionTreeNode, leafNode, edge);		
	}

	private Attribute searchForBestAttributeToSplitOn(IInstances instances,
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

	private Double getInfoGain(Attribute attribute, IInstances instances, Map<Value, Map<Value, Double>> targetClassCount,
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

	public DecisionTree getDecisionTree(){
		return decisionTree;
	}

	public void setCollapseTree(boolean collapseTree) {
		
	}

	public void setPruneTree(boolean pruneTree) {
		
	}
}
