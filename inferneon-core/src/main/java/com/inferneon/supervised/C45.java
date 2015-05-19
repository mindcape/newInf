package com.inferneon.supervised;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import com.inferneon.core.Attribute;
import com.inferneon.core.Attribute.Type;
import com.inferneon.core.Instance;
import com.inferneon.core.InstanceComparator;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.Value.ValueType;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.core.utils.StatisticsUtils;
import com.inferneon.supervised.DecisionTreeBuilder.Criterion;

public class C45 {
	private Criterion criteria;

	private DecisionTree decisionTree;
	private Instances allInstances;

	// Minimum number of instances in a node
	private int minNumInstancesInNode;

	// Minimum number of nodes that should have minimum number of instances
	private int minNumSplitsWithMinNumInstances;

	// Collapse the sub-tree if the collapsing does not increase the error in training
	private boolean collapseTree = false;

	// Frequency counts of all instances (computed once)
	private FrequencyCounts frequencyCountsOfAllInstances;

	//Confidence level 
	private float confidenceLevel = 0.25f;

	public C45(Criterion criteria){
		this.criteria = criteria;
		decisionTree = new DecisionTree(DecisionTreeEdge.class);
		minNumInstancesInNode = 2;
		minNumSplitsWithMinNumInstances = 2;
	}

	public void train(Instances instances) throws CycleFoundException{		
		this.allInstances = instances;	
		frequencyCountsOfAllInstances = DataLoader.getFrequencyCounts(allInstances);
		train(null, null, instances);
		collapseTree();

		System.out.println("######################## STARTED PRUNING DECISION TREE NOW WITH ########################");

		pruneDecisionTree(decisionTree.getDecisionTreeRootNode());
	}

	private void train(DecisionTreeNode parentDecisionTreeNode, DecisionTreeEdge decisionTreeEdge, Instances instances) throws CycleFoundException{

		// Determine the distribution of these instances. If the instances are the original one that is input
		// the code, use the one thats already computed
		FrequencyCounts frequencyCounts = null;
		if(instances == allInstances){
			frequencyCounts = frequencyCountsOfAllInstances;
		}
		else{
			frequencyCounts = DataLoader.getFrequencyCounts(instances);
		}

		//		System.out.println("Parent node sum of wts: " + frequencyCounts.getSumOfWeights()
		//				 + ", num Errors = " + frequencyCounts.getErrorOnDistribution());
		//		
		// Get the best attribute (the one with the most info gain)
		BestAttributeSearchResult bestAttributeSearchResult = searchForBestAttributeToSplitOn(instances, frequencyCounts);
		if(bestAttributeSearchResult == null){
			// Could not find a suitable attribute to split on. Create leaf and return			
			Value mostFrequentlyOccuringTargetValue  = mostFrequentlyOccuringTargetValue(frequencyCounts.getTotalTargetCounts());			
			createLeavesForAttribute(parentDecisionTreeNode, decisionTreeEdge, 
					mostFrequentlyOccuringTargetValue, frequencyCounts);

			return;
		}		

		// Found a suitable attribute to split on
		Attribute attribute = bestAttributeSearchResult.getAttribute();

		// If all the instances belong to the same target class, entropy will be zero. Create a leaf attribute and return
		Map<Value, Double> targetClassCounts = frequencyCounts.getTargetCountsForAttribute(attribute);
		if(targetClassCounts.size() == 1){
			// All instances belong to the same class, entropy will be zero
			Value targetValue = targetClassCounts.keySet().iterator().next();
			createLeavesForAttribute(parentDecisionTreeNode, decisionTreeEdge, targetValue, frequencyCounts);
			return;
		}

		// Add this attribute to the tree and and edge from the parent node to this attribute 
		DecisionTreeNode decisionTreeNode = new DecisionTreeNode(frequencyCounts, attribute);
		addAttributeToTree(parentDecisionTreeNode, decisionTreeNode, decisionTreeEdge);

		// Split the instances based on values of this attribute
		Map<DecisionTreeEdge, Instances>  instancesSplitForAttribute = split(bestAttributeSearchResult, instances, frequencyCounts);

		// Train each of the splits recursively
		if(instancesSplitForAttribute.size() <= 1){
			return;
		}
		Set<Entry<DecisionTreeEdge, Instances>> splits = instancesSplitForAttribute.entrySet();

		Iterator<Entry<DecisionTreeEdge, Instances>> iterator = splits.iterator();
		while(iterator.hasNext()){
			Entry<DecisionTreeEdge, Instances> entry = iterator.next();
			DecisionTreeEdge dtEdge = entry.getKey();			
			Instances splitInstances = entry.getValue();

			if(dtEdge.toString().equals("Sunny") || dtEdge.toString().equals("Overcast") || dtEdge.toString().equals("Rainy")){
				//System.out.println("WAIT HERE");
				//System.out.println("===================Instances for " + dtEdge + ": ");
				//System.out.println(splitInstances);
			}

			Instances newInstances = splitInstances;
			if(attribute.getType() == Attribute.Type.NOMINAL && frequencyCounts.getTotalInstancesWithMissingValues() == 0){
				newInstances = splitInstances.removeAtribute(attribute);
			}

			System.out.println("Training on subset based on attribute = " + attribute.getName() + " with value = " + dtEdge);

			train(decisionTreeNode, dtEdge, newInstances);
		}
	}

	private BestAttributeSearchResult searchForBestAttributeToSplitOn(Instances instances,  FrequencyCounts frequencyCounts) {

		if(!checkMinCriteriaBasedOnTargetValueCounts(frequencyCounts, instances)){
			return null;
		}

		BestAttributeSearchResult bestAttributeSearchResultWithMaxInfoGain = null;
		Attribute attrWithMaxInfoGain = null;
		int attributeCount = 0;
		Double maxInfoGain = 0.0;
		List<Attribute> attributes = instances.getAttributes();
		List<BestAttributeSearchResult> searchResults = new ArrayList<>(attributes.size());
		int numValidAttributesForSplitting = 0;
		double averageInfoGain = 0.0;
		for(Attribute attribute : attributes){
			if(attributeCount == instances.getClassIndex()){
				searchResults.add(null);
				attributeCount++;
				continue;
			}

			if(! checkMinCriteria(instances, frequencyCounts, attribute)){
				searchResults.add(null);
				attributeCount++;
				continue;
			}

			// Compute entropy of the whole sample
			double instancesSize = instances.sumOfWeights();
			double size = instancesSize - frequencyCounts.getNumMissingInstancesForAttribute(attribute);
			Map<Value, Double> targetCounts = frequencyCounts.getTargetCountsForAttribute(attribute);
			Double entropyOfTrainingSample = computeEntropy(size, targetCounts);

			BestAttributeSearchResult bestAttributeSearchResult = null;

			List<Map<Value, Map<Value, Double>>>  valueAndTargetClassCountList = frequencyCounts.getValueAndTargetClassCount();			
			Map<Value, Map<Value, Double>> targetClassCount = valueAndTargetClassCountList.get(attributeCount);	
			Double infoGainForAttribute = null;
			if(attribute.getType() == Attribute.Type.NOMINAL){
				infoGainForAttribute = getInfoGain(attribute, instances, targetClassCount, entropyOfTrainingSample, frequencyCounts);
				bestAttributeSearchResult = new BestAttributeSearchResult(attribute, infoGainForAttribute);
			}
			else{
				bestAttributeSearchResult = getInfoGainForContinuousValuedAttribute(attribute, instances, 
						targetClassCount, entropyOfTrainingSample, frequencyCounts);
				if(bestAttributeSearchResult == null){
					// Cannot use this attribute to split on 
					searchResults.add(null);
					attributeCount++;
					continue;
				}
				infoGainForAttribute = bestAttributeSearchResult.getInfoGain();
			}
			averageInfoGain += infoGainForAttribute;
			numValidAttributesForSplitting++;

			searchResults.add(bestAttributeSearchResult);

			//System.out.println("Info gain for attr: " + attribute.getName() + " = " + infoGainForAttribute);
			if(Double.compare(infoGainForAttribute, maxInfoGain) > 0){
				maxInfoGain = infoGainForAttribute;
				attrWithMaxInfoGain = attribute;
				bestAttributeSearchResultWithMaxInfoGain = bestAttributeSearchResult;
			}			

			attributeCount++;
		}

		averageInfoGain /= numValidAttributesForSplitting;

		BestAttributeSearchResult result = getBestAttributeToSplitOn(frequencyCounts, searchResults, 
				bestAttributeSearchResultWithMaxInfoGain, averageInfoGain);		

		if(attrWithMaxInfoGain != null){
			System.out.println("**************************************************** Max info gain = " + result.getAttribute().getName());
		}

		return result;
	}

	private Double computeGainRatio(FrequencyCounts frequencyCounts, BestAttributeSearchResult searchResult) {
		Attribute attribute = searchResult.getAttribute();
		Double infoGainForAttribute = searchResult.getInfoGain();

		double numInstances = frequencyCounts.getSumOfWeights();
		double unknownWeights = frequencyCounts.getNumMissingInstancesForAttribute(attribute);

		Map<Value, Double> valueCountsForAttribute = frequencyCounts.getAttributeValueCounts().get(attribute);

		Double splitInfo = 0.0;

		if(attribute.getType() == Type.NOMINAL){
			Iterator<Entry<Value, Double>> iterator = valueCountsForAttribute.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Value, Double> entry = iterator.next();
				Double count = entry.getValue();			
				Double ratio = (double) count / (double) numInstances;			
				splitInfo -= ratio * (Math.log(ratio) / Math.log(2));
			}
		}
		else{
			Instances splitBeforeThreshold = searchResult.getSplitBeforeThreshold();
			Instances splitAfterThreshold = searchResult.getSplitAfterThreshold();

			Double ratioOfInstsBeforeThreshold = splitBeforeThreshold.sumOfWeights() / (double) numInstances;
			Double ratioOfInstsAfterThreshold = splitAfterThreshold.sumOfWeights() / (double) numInstances;

			splitInfo -= ratioOfInstsBeforeThreshold * (Math.log(ratioOfInstsBeforeThreshold) / Math.log(2));
			splitInfo -= ratioOfInstsAfterThreshold * (Math.log(ratioOfInstsAfterThreshold) / Math.log(2));
		}

		if(Double.compare(unknownWeights, 0.0) > 0){
			Double ratioOfUnknownInsts = unknownWeights / (double) numInstances;
			splitInfo -= ratioOfUnknownInsts * (Math.log(ratioOfUnknownInsts) / Math.log(2));
		}

		if(Double.compare(splitInfo, 0.0) <= 0){
			return 0.0;
		}

		return infoGainForAttribute / splitInfo;		
	}

	private BestAttributeSearchResult getBestAttributeToSplitOn(
			FrequencyCounts frequencyCounts,
			List<BestAttributeSearchResult> searchResults, 
			BestAttributeSearchResult bestAttributeSearchResultWithMaxInfoGain, double averageInfoGain) {

		if(criteria == Criterion.INFORMATION_GAIN){
			return bestAttributeSearchResultWithMaxInfoGain;			
		}
		else if(criteria == Criterion.GAIN_RATIO){
			Double maxGainRatio = 0.0;
			BestAttributeSearchResult bestAttributeSearchResultWithMaxGainRatio = null;
			for(BestAttributeSearchResult result : searchResults){
				if(result == null){
					continue;
				}

				double infoGainForAttribute = result.getInfoGain();
				double gainRatio = computeGainRatio(frequencyCounts, result);

				System.out.println("Gain ratio for attribute " + result.getAttribute().getName() + " = " + gainRatio);
				bestAttributeSearchResultWithMaxInfoGain.setGainRatio(gainRatio);

				if(Double.compare(infoGainForAttribute, averageInfoGain) >= 0
						&& Double.compare(gainRatio, maxGainRatio) >= 0){
					bestAttributeSearchResultWithMaxGainRatio = result;
					maxGainRatio = gainRatio;
				}			
			}

			return bestAttributeSearchResultWithMaxGainRatio;
		}

		// TODO Implement other criteria (Gini impurity, etc.)
		return null;

	}

	private boolean checkMinCriteriaBasedOnTargetValueCounts(FrequencyCounts frequencyCounts, Instances instances) {
		Double total = instances.sumOfWeights();
		if(Double.compare(total, minNumInstancesInNode * minNumSplitsWithMinNumInstances) < 0){
			return false;
		}

		Double numMaxClass = frequencyCounts.getMaxTargetValueCount();
		if(Double.compare(numMaxClass, total) == 0){
			return false;
		}
		return true;		
	}

	/**
	 * Returns target class value with the highest number. If there are no instances, arbitrarily assign the
	 * class value with the max occurrences.
	 */
	private Value mostFrequentlyOccuringTargetValue(Map<Value, Double> targetClassCounts) {

		Value mostFrequentlyOccuringTargetValue = null;
		Double max = 0.0;

		Set<Entry<Value, Double>> entries = targetClassCounts.entrySet();
		Iterator<Entry<Value, Double>> iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Value value = entry.getKey();
			Double numInstances = entry.getValue();
			if(numInstances > max){
				max = numInstances;
				mostFrequentlyOccuringTargetValue = value;
			}
		}

		if(mostFrequentlyOccuringTargetValue == null){
			mostFrequentlyOccuringTargetValue = frequencyCountsOfAllInstances.getMaxTargetValue();
		}

		return mostFrequentlyOccuringTargetValue;
	}

	public boolean checkMinCriteria(Instances instances, FrequencyCounts frequencyCounts, Attribute attribute) {

		if(attribute.getType() != Attribute.Type.NOMINAL){			
			return true;
		}

		Map<Attribute, Map<Value, Double>> attributeValueCounts = frequencyCounts.getAttributeValueCounts();		
		Map<Value, Double> valueCountsForAttribute = attributeValueCounts.get(attribute);

		int count = 0;

		Set<Entry<Value, Double>> entries = valueCountsForAttribute.entrySet();
		Iterator<Entry<Value, Double>> iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Double numInstances = entry.getValue();

			if(Double.compare(numInstances, minNumInstancesInNode) >= 0){
				count++;
			}			
		}

		if(count >= minNumSplitsWithMinNumInstances){
			return true;
		}

		return false;
	}

	private Map<DecisionTreeEdge, Instances> split(BestAttributeSearchResult bestAttributeSearchResult , Instances instances, FrequencyCounts frequencyCounts) {
		Map<DecisionTreeEdge, Instances>  instancesSplitForAttribute = null;

		Attribute attribute = bestAttributeSearchResult.getAttribute();

		// Treat nominal and continuous valued attributes differently
		if(attribute.getType() == Attribute.Type.NOMINAL){
			instancesSplitForAttribute = splitOn(attribute, instances, frequencyCounts);
		}
		else{
			instancesSplitForAttribute = getSplitForContinuousValuedAttributeBasedOnThreshold(bestAttributeSearchResult, instances, frequencyCounts);
		}

		return instancesSplitForAttribute;
	}

	private void addAttributeToTree(DecisionTreeNode parent, DecisionTreeNode decisionTreeNode, DecisionTreeEdge decisionTreeEdge)
			throws CycleFoundException {

		decisionTree.addVertex(decisionTreeNode);
		if(parent == null){
			decisionTree.setDecisionTreeRootNode(decisionTreeNode);
		}

		if(decisionTreeEdge != null){
			System.out.println("Adding edge " + decisionTreeEdge + " between attributes " + parent + " and " + decisionTreeNode);

			decisionTreeEdge.setSource(parent); decisionTreeEdge.setTarget(decisionTreeNode);
			decisionTree.addDagEdge(parent, decisionTreeNode, decisionTreeEdge);
		}		
	}

	private void adjustForMissingValues(Attribute attribute, Map<DecisionTreeEdge, Instances> instancesSplitForAttribute, 
			FrequencyCounts frequencyCounts, double sumofWeights) {
		List<Instance> instancesWithMissingValue = frequencyCounts.getAttributeAndMissingValueInstances().get(attribute);
		if(instancesWithMissingValue == null || instancesWithMissingValue.size() == 0){
			// No missing values for this attribute, nothing to do
			return;
		}

		// There are some instances with missing values for this attribute. Add each of those instances to each of the 
		// splits with appropriate weights

		double instancesSize = sumofWeights - DataLoader.getSumOfWeights(instancesWithMissingValue);

		Iterator<Entry<DecisionTreeEdge, Instances>> iterator = instancesSplitForAttribute.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<DecisionTreeEdge, Instances> entry = iterator.next();
			Instances split = entry.getValue();

			Double ratio = (double) split.sumOfWeights() / instancesSize;
			for(Instance instanceWithMissingValue : instancesWithMissingValue){				
				Double weight = ratio * instanceWithMissingValue.getWeight();
				Instance newInstance = new Instance(instanceWithMissingValue.getValues());
				newInstance.setWeight(weight);
				split.addInstance(newInstance);

			}						
		}		
	}

	public Map<DecisionTreeEdge, Instances> getSplitForContinuousValuedAttributeBasedOnThreshold(
			BestAttributeSearchResult bestAttributeSearchResult, Instances instances, FrequencyCounts frequencyCounts){
		Map<DecisionTreeEdge, Instances> result = new HashMap<DecisionTreeEdge, Instances>();

		Attribute attribute = bestAttributeSearchResult.getAttribute();
		Value threshold = bestAttributeSearchResult.getThreshold();		
		PredicateEdge firstPredicateEdge = new PredicateEdge(attribute, threshold, true);
		DecisionTreeEdge firstEdge = new DecisionTreeEdge(firstPredicateEdge);		
		PredicateEdge secondPredicateEdge = new PredicateEdge(attribute, threshold, false);
		DecisionTreeEdge secondEdge = new DecisionTreeEdge(secondPredicateEdge);

		result.put(firstEdge, bestAttributeSearchResult.getSplitBeforeThreshold());
		result.put(secondEdge, bestAttributeSearchResult.getSplitAfterThreshold());

		// Adjust against missing values in these instances.
		adjustForMissingValues(attribute, result, frequencyCounts, instances.sumOfWeights());

		return result;
	}

	private Map<DecisionTreeEdge, Instances> splitOn(Attribute attribute, Instances instances, FrequencyCounts frequencyCounts){

		Map<DecisionTreeEdge, Instances> valueAndInstancesHavingValue = new HashMap<DecisionTreeEdge, Instances>();
		List<Attribute> attributes = instances.getAttributes();
		Iterator<Instance> iterator = instances.iterator();		
		Map<Value, DecisionTreeEdge> valueAndDecisionTreeEdge = new HashMap<>();
		List<Instance> instancesWithMissingValue = frequencyCounts.getAttributeAndMissingValueInstances().get(attribute);

		Set<Value> valuesInSplit = new HashSet<>();

		while(iterator.hasNext()){
			Instance instance = iterator.next();
			if(instancesWithMissingValue != null && instancesWithMissingValue.contains(instance)){
				continue;
			}
			Value value = instance.attributeValue(attribute);
			valuesInSplit.add(value);

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

		// Adjust against missing values in these instances.
		adjustForMissingValues(attribute, valueAndInstancesHavingValue, frequencyCounts,  instances.sumOfWeights());

		// If there is any value that has no instances, add that as a split with an empty set of instances
		List<Value> allValuesOfAttribute = attribute.getAllValues();
		for(Value value : allValuesOfAttribute){			
			if(!valuesInSplit.contains(value)){
				Instances emptyInstances = new Instances();
				emptyInstances.setAttributes(instances.getAttributes());
				valueAndInstancesHavingValue.put(new DecisionTreeEdge(value), emptyInstances);
			}
		}

		return valueAndInstancesHavingValue;
	}

	private void createLeavesForAttribute(DecisionTreeNode parentNode, DecisionTreeEdge edge,
			Value targetValue, FrequencyCounts frequencyCounts) throws CycleFoundException {
		DecisionTreeNode leafNode = new DecisionTreeNode(frequencyCounts, targetValue);
		decisionTree.addVertex(leafNode);
		edge.setSource(parentNode); edge.setTarget(leafNode);
		System.out.println("Adding edge " + edge + " between attributes " + parentNode + " and leaf node " + leafNode);		
		decisionTree.addDagEdge(parentNode, leafNode, edge);		
	}


	private Double getInfoGain(Attribute attribute, Instances instances, Map<Value, Map<Value, Double>> targetClassCount,
			Double entropyOfTrainingSample, FrequencyCounts frequencyCounts) {
		Double weightedEntropy = 0.0;

		Double instancesWithKnownValuesSize = getSizeOfInstancesWithKnownValuesForAttribute(attribute, instances, frequencyCounts);

		Iterator<Entry<Value, Map<Value, Double>>> iterator = targetClassCount.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Map<Value, Double>> entry = iterator.next();
			Value val = entry.getKey();

			Double totalOccurencesOfValue = (double)frequencyCounts.getAttributeValueCounts().get(attribute).get(val);

			Map<Value, Double> counts = entry.getValue();	

			Double entropyForVal = computeEntropy(totalOccurencesOfValue, counts);
			Double weightRatio = ((double)totalOccurencesOfValue) /((double) instancesWithKnownValuesSize);
			weightedEntropy += weightRatio * entropyForVal;
		}

		Double knownValueInstancesRatio =  ((double)instancesWithKnownValuesSize) /((double) instances.sumOfWeights());

		return knownValueInstancesRatio * (entropyOfTrainingSample - weightedEntropy);
	}

	private BestAttributeSearchResult getInfoGainForContinuousValuedAttribute(Attribute attribute, Instances instances, Map<Value, 
			Map<Value, Double>> targetClassCount, Double entropyOfTrainingSample, FrequencyCounts frequencyCounts){

		List<Instance> instancesList = instances.getInstances();
		List<Attribute> attributes = instances.getAttributes();
		int attributeIndex = attributes.indexOf(attribute);

		//List<Value> valueList = new ArrayList<Value>(valuesOfAttribute);
		Collections.sort(instancesList, new InstanceComparator(attribute, instances.getAttributes()));

		int firstInstanceWithMissingValueForAttribute = 0;
		for(Instance inst : instancesList){
			Value val = inst.getValue(attributeIndex);
			if(val.getType() == ValueType.MISSING){
				break;
			}
			firstInstanceWithMissingValueForAttribute++;
		}

		Double maxInfoGain = 0.0;
		BestAttributeSearchResult bestAttributeSearchResultWithMaxInfoGain = null;
		Value thresholdValue = null;

		int splitPoint = 0;

		int minSplit = getMinSplit(instancesList.size(), instances.numClasses());

		while(splitPoint < firstInstanceWithMissingValueForAttribute -1){			
			List<Instance> splitBeforeThreshold = new ArrayList<>(instancesList.subList(0, splitPoint + 1));
			thresholdValue = splitBeforeThreshold.get(splitBeforeThreshold.size() -1).getValue(attributeIndex);			
			int maxIndexWithSameValue = 1;
			Value nextValue = instancesList.get(splitPoint + maxIndexWithSameValue).getValue(attributeIndex);

			while(Value.valuesAreIdentical(thresholdValue, nextValue)){
				maxIndexWithSameValue++;				
				if(splitPoint + maxIndexWithSameValue >=  instancesList.size()){
					break;
				}

				nextValue = instancesList.get(splitPoint + maxIndexWithSameValue).getValue(attributeIndex);
			}

			if(maxIndexWithSameValue > 1){
				splitPoint += maxIndexWithSameValue -1;
				continue;
			}

			//List<Instance> splitAfterThreshold = instancesList.subList(splitPoint + 1, instancesList.size());	
			List<Instance> splitAfterThreshold = new ArrayList<>(instancesList.subList(splitPoint + 1, firstInstanceWithMissingValueForAttribute));

			Double sumOfWeightsBeforeThreshold = instances.sumOfWeights(0, splitPoint + 1);
			Double sumOfWeightsAfterThreshold = instances.sumOfWeights(splitPoint + 1, firstInstanceWithMissingValueForAttribute);

			if (!(sumOfWeightsBeforeThreshold >=  minSplit && sumOfWeightsAfterThreshold >=  minSplit)) {
				splitPoint++;
				continue;
			}

			Double instancesWithKnownValuesSize = getSizeOfInstancesWithKnownValuesForAttribute(attribute, instances, frequencyCounts);
			Double weightedEntropy = getWeightedEntropyForValuesSubset(attribute, attributeIndex, splitBeforeThreshold, frequencyCounts,
					instancesWithKnownValuesSize,  targetClassCount);			
			weightedEntropy += getWeightedEntropyForValuesSubset(attribute, attributeIndex, splitAfterThreshold, frequencyCounts,
					instancesWithKnownValuesSize,  targetClassCount);
			Double knownValueInstancesRatio =  ((double)instancesWithKnownValuesSize) /((double) instances.sumOfWeights());			
			Double infoGainForSplit = knownValueInstancesRatio * (entropyOfTrainingSample - weightedEntropy);

			Instances instsBeforeThreshold = new Instances(splitBeforeThreshold, instances.getAttributes(), instances.getClassIndex());			
			Instances instsAfterThreshold = new Instances(splitAfterThreshold, instances.getAttributes(), instances.getClassIndex());

			BestAttributeSearchResult attributeSearchResultWithMaxInfoGain = new BestAttributeSearchResult(attribute, infoGainForSplit, 
					splitPoint,  thresholdValue, instsBeforeThreshold, instsAfterThreshold);

			if(Double.compare(infoGainForSplit, maxInfoGain) > 0){

				maxInfoGain = infoGainForSplit;
				bestAttributeSearchResultWithMaxInfoGain = attributeSearchResultWithMaxInfoGain;
			}			
			splitPoint++;
		}

		if(bestAttributeSearchResultWithMaxInfoGain != null){
			splitPoint = bestAttributeSearchResultWithMaxInfoGain.getSplittingPoint();
			double thresholdVal = 0.0;
			Value val = instancesList.get(splitPoint).getValue(attributeIndex);
			if(splitPoint + 1 < instancesList.size()){
				Value valNext = instancesList.get(splitPoint + 1).getValue(attributeIndex);
				thresholdVal = (val.getNumericValueAsDouble()
						+ valNext.getNumericValueAsDouble()) / 2.0;
			}
			else{
				thresholdVal = val.getNumericValueAsDouble();
			}

			// Apply C4.5 correction
			thresholdVal =  DataLoader.getMaxValueLesserThanOrEqualTo(thresholdVal, attribute, allInstances);
			thresholdValue = new Value(thresholdVal);
			bestAttributeSearchResultWithMaxInfoGain.setThreshold(thresholdValue);
		}
		return bestAttributeSearchResultWithMaxInfoGain;
	}

	private int getMinSplit(int instancesSize, int numClasses){
		int minSplit = (int) 0.1 * (instancesSize) / (numClasses);

		if (Double.compare(minSplit, minNumInstancesInNode) < 0) {
			minSplit = minNumInstancesInNode;
		} 
		else if (Double.compare(minSplit, 25) > 0) {
			minSplit = 25;
		}

		return minSplit;
	}

	private Double getSizeOfInstancesWithKnownValuesForAttribute(Attribute attribute, Instances instances,
			FrequencyCounts frequencyCounts) {

		Double instancesSize = instances.sumOfWeights();
		Double instsWithMissingValues = frequencyCounts.getNumMissingInstancesForAttribute(attribute);
		Double instancesWithKnownValuesSize = instancesSize - instsWithMissingValues;

		return instancesWithKnownValuesSize;
	}

	private Double getWeightedEntropyForValuesSubset(Attribute attribute, int attributeIndex, List<Instance> split, 
			FrequencyCounts frequencyCounts, Double instancesSize,  
			Map<Value, Map<Value, Double>> targetClassCount){

		double totalOccurencesOfValue = 0.0;

		Map<Value, Double> cummulativeCounts = new HashMap<Value, Double>();
		for(Instance inst : split){
			Value val = inst.getValue(attributeIndex);
			totalOccurencesOfValue += 
					frequencyCounts.getAttributeValueCounts().get(attribute).get(val);
			Map<Value, Double> counts = targetClassCount.get(val);	
			if(cummulativeCounts.size() == 0){
				cummulativeCounts.putAll(counts);
			}
			else{
				mergeCounts(cummulativeCounts, counts);
			}
		}

		Double entropyForVals = computeEntropy(totalOccurencesOfValue, cummulativeCounts);
		Double weightRatio = ((double)totalOccurencesOfValue) /((double) instancesSize);

		return weightRatio * entropyForVals;
	}

	private void mergeCounts(Map<Value, Double> cummulativeCounts,
			Map<Value, Double> newCounts) {
		Set<Entry<Value, Double>> entries = newCounts.entrySet();
		Iterator<Entry<Value, Double>> iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Value value = entry.getKey();
			Double newCount = entry.getValue();

			Double existingCount = cummulativeCounts.get(value);
			if(existingCount != null){			
				cummulativeCounts.put(value, existingCount + newCount);
			}
			else{
				cummulativeCounts.put(value, newCount);
			}
		}		
	}

	private Double computeEntropy(Double size, Map<Value, Double> targetClassCounts) {

		Double entropy = 0.0;

		Iterator<Entry<Value, Double>> iterator = targetClassCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Double count = entry.getValue();			
			Double ratio = (double) count / (double) size;			
			entropy -= ratio * (Math.log(ratio) / Math.log(2));
		}

		return entropy;
	}

	private void collapseTree() throws CycleFoundException {
		if(!collapseTree){
			return;
		}

		// Need to collapse the tree
		List<DecisionTreeNode> nodesToBeRemoved = new ArrayList<>();
		collapseTree(decisionTree.getDecisionTreeRootNode(), nodesToBeRemoved);

		for(DecisionTreeNode node : nodesToBeRemoved){
			DecisionTreeNode parentNode = decisionTree.getParentOfNode(node);
			if(parentNode == null){
				// Node to be removed has no parent; this must be the root node, nothing to do
				break;
			}

			makeLeaf(node, parentNode);						
		}		
	}

	private void makeLeaf(DecisionTreeNode node, DecisionTreeNode parentNode) throws CycleFoundException{
		FrequencyCounts frequencyCountsOfNode = node.getFrequencyCounts();
		DecisionTreeEdge currentEdge = decisionTree.incomingEdgeOfNode(node);
		Value targetValue = frequencyCountsOfNode.getMaxTargetValue();			
		decisionTree.removeVertex(node);

		createLeavesForAttribute(parentNode, currentEdge, targetValue, frequencyCountsOfNode);
	}

	private void collapseTree(DecisionTreeNode decisionTreeNode, List<DecisionTreeNode> nodesToBeRemoved) {

		FrequencyCounts frequencyCounts = decisionTreeNode.getFrequencyCounts();

		Double errorOnDistribution = frequencyCounts.getErrorOnDistribution();
		Double trainingErrorOnTree = getTrainingErrorOnTree(decisionTreeNode);

		if(Double.compare(trainingErrorOnTree, errorOnDistribution) >= 0){
			nodesToBeRemoved.add(decisionTreeNode);
		}
		else{
			Set<DecisionTreeNode> childNodes = decisionTree.getChildNodes(decisionTreeNode);
			Iterator<DecisionTreeNode> iterator = childNodes.iterator();
			while(iterator.hasNext()){
				DecisionTreeNode childNode = iterator.next();
				if(!childNode.isLeaf()){
					collapseTree(childNode, nodesToBeRemoved);
				}
			}
		}
	}

	private Double getTrainingErrorOnTree(DecisionTreeNode node){
		if(node.isLeaf()){
			FrequencyCounts frequencyCounts = node.getFrequencyCounts();
			Map<Value, Double> targetClassCounts = frequencyCounts.getTotalTargetCounts();

			//Map<Value, Double> targetClassCounts = node.getTargetClassCounts();
			Double numCorrect = targetClassCounts.get(node.getValue());
			Double numIncorrect = node.getNumInstances() - numCorrect;
			return numIncorrect;
		}

		double errors = 0.0;
		Set<DecisionTreeNode> childNodes = decisionTree.getChildNodes(node);
		Iterator<DecisionTreeNode> iterator = childNodes.iterator();
		while(iterator.hasNext()){
			DecisionTreeNode childNode = iterator.next();
			errors += getTrainingErrorOnTree(childNode);
		}

		return errors;
	}

	private void pruneDecisionTree(DecisionTreeNode node) throws CycleFoundException {
		System.out.println("CURRENT TREE: ");
		decisionTree.emitTree(null);

		if(! node.isLeaf()){
			Set<DecisionTreeNode> childNodes = decisionTree.getChildNodes(node);
			FrequencyCounts frequencyCounts = node.getFrequencyCounts();			
			double estimatedErrorAtNode = getEstimatedError(node);
			double totalInstancesAtNode = frequencyCounts.getSumOfWeights();			
			double estimatedErrorOfSubTree = getEstimatedError(childNodes, totalInstancesAtNode);
			
			if(Double.compare(estimatedErrorOfSubTree, estimatedErrorAtNode) >= 0){
				DecisionTreeNode parentNode = decisionTree.getParentOfNode(node);
				if(parentNode == null){
					// Must be the root node
					return;
				}
				makeLeaf(node, parentNode);			
			}
						
			Iterator<DecisionTreeNode> iterator = childNodes.iterator();
			while(iterator.hasNext()){
				DecisionTreeNode childNode = iterator.next();
				pruneDecisionTree(childNode);					
			}	
		}
		
		//		Map<DecisionTreeNode, Set<DecisionTreeNode>> parentAndLeafChildNodes = new HashMap<>();
		//		populateParentAndLeafNodes(parentAndLeafChildNodes, decisionTree.getDecisionTreeRootNode());
		//
		//		Set<DecisionTreeNode> parentNodesToFold = identifyParentNodesToFold(parentAndLeafChildNodes);
		//		if(parentNodesToFold != null && parentNodesToFold.size() > 0){			
		//			foldParents(parentNodesToFold);
		//			pruneDecisionTree();
		//		}
	}

	/**
	 * Returns the estimated error the set of nodes that is passed
	 * @param parentNode
	 * @return
	 */
	private double getEstimatedError(Set<DecisionTreeNode> childNodes, double totalInstancesAtParentNode) {
		double estimatedError = 0.0;
		Iterator<DecisionTreeNode> iterator = childNodes.iterator();

		while(iterator.hasNext()){
			DecisionTreeNode node = iterator.next();
			double totalInstances = node.getFrequencyCounts().getSumOfWeights(); 
			if(Double.compare(totalInstances, 0.0) == 0){
				continue;
			}

			double estimatedErrorAtNode = getEstimatedError(node);
			estimatedError += estimatedErrorAtNode;
		}

		return estimatedError;
	}

	/**
	 * Returns the estimated error if this node is a leaf node
	 * @param node
	 * @return
	 */
	private double getEstimatedError(DecisionTreeNode node) {

		FrequencyCounts frequencyCounts = node.getFrequencyCounts();

		double totalInstances = frequencyCounts.getSumOfWeights();		
		double errorOnDistribution = frequencyCounts.getErrorOnDistribution();		
		double extraError = extraError(totalInstances, errorOnDistribution); 		
		double totalError = errorOnDistribution + extraError;

		return totalError;
	}

	/**
	 * Computes estimated extra error for given total number of instances
	 * and error using normal approximation to binomial distribution
	 * (and continuity correction).
	 *
	 */
	public double extraError(double totalInstances, double errorOnDistribution){

		// Ignore stupid values for CF
		if (confidenceLevel > 0.5) {
			System.err.println("WARNING: confidence value for pruning " +
					" too high. Error estimate not modified.");
			return 0;
		}

		// Check for extreme cases at the low end because the
		// normal approximation won't work
		if (errorOnDistribution < 1) {

			// Base case (i.e. e == 0) from documenta Geigy Scientific
			// Tables, 6th edition, page 185
			double base = totalInstances * (1 - Math.pow(confidenceLevel, 1 / totalInstances)); 
			if (errorOnDistribution == 0) {
				return base; 
			}

			// Use linear interpolation between 0 and 1 like C4.5 does
			return base + errorOnDistribution * (extraError(totalInstances, 1) - base);
		}

		// Use linear interpolation at the high end (i.e. between N - 0.5
		// and N) because of the continuity correction
		if (errorOnDistribution + 0.5 >= totalInstances) {

			// Make sure that we never return anything smaller than zero
			return Math.max(totalInstances - errorOnDistribution, 0);
		}

		// Get z-score corresponding to CF
		double numStandardDeviations = StatisticsUtils.normalInverse(1 - confidenceLevel);

		// Compute upper limit of confidence interval
		double observedError = (errorOnDistribution + 0.5) / totalInstances;
		if(Double.compare(totalInstances, 4.666666666666667) == 0 
				&& Double.compare(errorOnDistribution, 1.666666666666667) == 0 ){
			System.out.println("WAIT HERE");
		}

		double numeratorTerm1 = observedError;		
		double numeratorTerm2 =  Math.pow(numStandardDeviations, 2.0) / (2.0 * totalInstances);		
		double numeratorTerm3 = (observedError / totalInstances) 
				- (Math.pow(observedError, 2.0) / totalInstances)
				+ (Math.pow(numStandardDeviations, 2.0) / (4.0 * Math.pow(totalInstances, 2.0)));
		numeratorTerm3 = numStandardDeviations * Math.sqrt(numeratorTerm3);		
		double numerator = numeratorTerm1 + numeratorTerm2 + numeratorTerm3;		
		double denominator = Math.pow(numStandardDeviations, 2.0) / totalInstances;
		denominator += 1.0;

		double errorRate = numerator / denominator;
		double extraError = (errorRate * totalInstances) - errorOnDistribution;
		return extraError;
	}

	public DecisionTree getDecisionTree(){
		return decisionTree;
	}
}
