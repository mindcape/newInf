package com.inferneon.supervised;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.ValueComparator;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.DecisionTreeBuilder.Criterion;

public class C45 {
	private Criterion criteria;

	private DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> decisionTree;
	private DecisionTreeNode decisionTreeRootNode; 
	private List<DecisionTreeNode> leafNodes;
	private Instances allInstances;

	private static final Double NUM_STANDARD_DEVIATIONS = 0.69;

	// Minimum number of instances in a node
	private int minNumInstancesInNode;

	// Minimum number of nodes that should have minimum number of instances
	private int minNumSplitsWithMinNumInstances;

	public C45(Criterion criteria){
		this.criteria = criteria;
		decisionTree = new DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge>(DecisionTreeEdge.class);
		leafNodes = new ArrayList<>();
		minNumInstancesInNode = 2;
		minNumSplitsWithMinNumInstances = 2;
	}

	public void train(Instances instances) throws CycleFoundException{		
		this.allInstances = instances;		
		train(null, null, instances);
		pruneDecisionTree();
	}

	private void train(DecisionTreeNode parentDecisionTreeNode, DecisionTreeEdge decisionTreeEdge, Instances instances) throws CycleFoundException{

		// Determine the distribution of these instances
		FrequencyCounts frequencyCounts = DataLoader.getFrequencyCounts(instances);

		// Get the best attribute (the one with the most info gain)
		BestAttributeSearchResult bestAttributeSearchResult = searchForBestAttributeToSplitOn(instances, frequencyCounts);
		if(bestAttributeSearchResult == null){
			// Could not find a suitable attribute to split on. Create leaf and return			
			Map<Value, Double> targetClassCounts = frequencyCounts.getTargetCounts(instances);			
			Value mostFrequentlyOccuringTargetValue  = mostFrequentlyOccuringTargetValue(targetClassCounts);			
			DecisionTreeNode leafNode = createLeavesForAttribute(parentDecisionTreeNode, decisionTreeEdge, mostFrequentlyOccuringTargetValue,
					instances.sumOfWeights(), targetClassCounts);
			leafNodes.add(leafNode);

			return;
		}		
		Attribute attribute = bestAttributeSearchResult.getAttribute();

		// If all the instances belong to the same target class, entropy will be zero. Create a leaf attribute and return
		Map<Value, Double> targetClassCounts = frequencyCounts.getTargetCountsForAttribute(attribute);
		if(targetClassCounts.size() == 1){
			// All instances belong to the same class, entropy will be zero
			Value targetValue = targetClassCounts.keySet().iterator().next();
			DecisionTreeNode leafNode = createLeavesForAttribute(parentDecisionTreeNode, decisionTreeEdge, targetValue,
					instances.sumOfWeights(), targetClassCounts);
			leafNodes.add(leafNode);
			return;
		}

		// Add this attribute to the tree and and edge from the parent node to this attribute 
		DecisionTreeNode decisionTreeNode = new DecisionTreeNode(attribute, instances.sumOfWeights(), targetClassCounts);
		addAttributeToTree(parentDecisionTreeNode, decisionTreeNode, decisionTreeEdge);

		// Split the instances based on values of this attribute
		Map<DecisionTreeEdge, Instances>  instancesSplitForAttribute = split(bestAttributeSearchResult, instances, frequencyCounts);

		// For each of the splits, recursively call this function.
		if(instancesSplitForAttribute.size() <= 1){
			return;
		}
		Set<Entry<DecisionTreeEdge, Instances>> splits = instancesSplitForAttribute.entrySet();

		Iterator<Entry<DecisionTreeEdge, Instances>> iterator = splits.iterator();
		while(iterator.hasNext()){
			Entry<DecisionTreeEdge, Instances> entry = iterator.next();
			DecisionTreeEdge dtEdge = entry.getKey();
//			if(!dtEdge.toString().equals("Overcast")){
//				continue;
//			}			
//			else{
//				System.out.println("WAIT HERE");
//			}

			if(dtEdge.toString().equals("false")){
				System.out.println("WAIT HERE");
			}
			Instances splitInstances = entry.getValue();

			if(dtEdge.toString().equals("Sunny") || dtEdge.toString().equals("Overcast") || dtEdge.toString().equals("Rainy")){
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

	private boolean checkMinCriteriaBasedOnTargetValueCounts(FrequencyCounts frequencyCounts, Instances instances) {
		Double total = instances.sumOfWeights();
		if(Double.compare(total, minNumInstancesInNode * minNumSplitsWithMinNumInstances) < 0){
			return false;
		}
		
		Double numMaxClass = frequencyCounts.getNumInstancesInMaxClass(instances);
		if(Double.compare(numMaxClass, total) == 0){
			return false;
		}
		
		return true;
		
	}

	/**
	 * Returns target class value with the highest number
	 * @param instances
	 * @return
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
			decisionTreeRootNode = decisionTreeNode;
		}

		if(decisionTreeEdge != null){
			System.out.println("Adding edge " + decisionTreeEdge + " between attributes " + parent + " and " + decisionTreeNode);

			decisionTreeEdge.setSource(parent); decisionTreeEdge.setTarget(decisionTreeNode);
			decisionTree.addDagEdge(parent, decisionTreeNode, decisionTreeEdge);
		}		
	}

	private Long getInstancesSize(Instances instances, FrequencyCounts frequencyCounts){
		Long instancesSize = instances.size();
		instancesSize -= frequencyCounts.getTotalInstancesWithMissingValues();		

		return instancesSize;
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

		double instancesSize = sumofWeights - instancesWithMissingValue.size();
		
		Iterator<Entry<DecisionTreeEdge, Instances>> iterator = instancesSplitForAttribute.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<DecisionTreeEdge, Instances> entry = iterator.next();
			Instances split = entry.getValue();
			for(Instance instanceWithMissingValue : instancesWithMissingValue){
				Double ratio = (double) split.size() / (double) instancesSize;
				Instance newInstance = new Instance(instanceWithMissingValue.getValues());
				newInstance.setWeight(ratio);
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

		Instances instances1 = instances.getSubset(attribute, bestAttributeSearchResult.getSplitBeforeThreshold());
		Instances instances2 = instances.getSubset(attribute, bestAttributeSearchResult.getSplitAfterThreshold());


		result.put(firstEdge, instances1);
		result.put(secondEdge, instances2);

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

		while(iterator.hasNext()){
			Instance instance = iterator.next();
			if(instancesWithMissingValue != null && instancesWithMissingValue.contains(instance)){
				continue;
			}
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

		// Adjust against missing values in these instances.
		adjustForMissingValues(attribute, valueAndInstancesHavingValue, frequencyCounts,  instances.sumOfWeights());

		if(attribute.getName().equals("Windy")){
			System.out.println("WAIT HERE");
		}
		
		return valueAndInstancesHavingValue;
	}

	private DecisionTreeNode createLeavesForAttribute(DecisionTreeNode decisionTreeNode, DecisionTreeEdge edge,
			Value targetValue, Double numInstances, Map<Value, Double> targetClassCounts) throws CycleFoundException {
		DecisionTreeNode leafNode = new DecisionTreeNode(targetValue, numInstances, targetClassCounts);
		decisionTree.addVertex(leafNode);
		edge.setSource(decisionTreeNode); edge.setTarget(leafNode);
		System.out.println("Adding edge " + edge + " between attributes " + decisionTreeNode + " and leaf node " + leafNode);		
		decisionTree.addDagEdge(decisionTreeNode, leafNode, edge);		
		return leafNode;
	}

	private BestAttributeSearchResult searchForBestAttributeToSplitOn(Instances instances,  FrequencyCounts frequencyCounts) {
		
		if(!checkMinCriteriaBasedOnTargetValueCounts(frequencyCounts, instances)){
			return null;
		}
		
		Double maxInfoGain = 0.0;
		Attribute attrWithMaxInfoGain = null;

		BestAttributeSearchResult bestAttributeSearchResultWithMaxInfoGain = null;

		int attributeCount = 0;
		List<Attribute> attributes = instances.getAttributes();
		for(Attribute attribute : attributes){

			if(attribute.getName().equals("Windy")){
				System.out.println("WAIT HERE");
			}
			
			if(attributeCount == instances.getClassIndex()){
				attributeCount++;
				continue;
			}

			if(! checkMinCriteria(instances, frequencyCounts, attribute)){
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
				bestAttributeSearchResult = new BestAttributeSearchResult(attribute);
			}
			else{
				bestAttributeSearchResult = getInfoGainForContinuousValuedAttribute(attribute, instances, 
						targetClassCount, entropyOfTrainingSample, frequencyCounts);
				if(bestAttributeSearchResult == null){
					// Did not find anything to split further on. 
					return null;
				}
				infoGainForAttribute = bestAttributeSearchResult.getInfoGain();
			}

			System.out.println("Info gain for attr: " + attribute.getName() + " = " + infoGainForAttribute);

			if(Double.compare(infoGainForAttribute, maxInfoGain) > 0){
				maxInfoGain = infoGainForAttribute;
				attrWithMaxInfoGain = attribute;
				bestAttributeSearchResultWithMaxInfoGain = bestAttributeSearchResult;
			}			
			attributeCount++;
		}

		if(attrWithMaxInfoGain != null){
			System.out.println("**************************************************** Max = " + attrWithMaxInfoGain.getName());
		}

		return bestAttributeSearchResultWithMaxInfoGain;
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

		Set<Value> valuesOfAttribute = targetClassCount.keySet();
		List<Value> valueList = new ArrayList<Value>(valuesOfAttribute);
		Collections.sort(valueList, new ValueComparator());

		Double maxInfoGain = 0.0;
		BestAttributeSearchResult bestAttributeSearchResultWithMaxInfoGain = null;
		Value thresholdValue = null;

		int splitPoint = 0;

		int minSplit = getMinSplit(valueList.size(), instances.numClasses());

		while(splitPoint < valueList.size() -1){

			List<Value> splitBeforeThreshold = valueList.subList(0, splitPoint + 1);
			thresholdValue = splitBeforeThreshold.get(splitBeforeThreshold.size() -1);			
			int maxIndexWithSameValue = 1;
			Value nextValue = valueList.get(splitPoint + maxIndexWithSameValue);
			while(Value.valuesAreIdentical(thresholdValue, nextValue)){
				maxIndexWithSameValue++;				
				if(splitPoint + maxIndexWithSameValue >=  valueList.size()){
					break;
				}
				
				nextValue = valueList.get(splitPoint + maxIndexWithSameValue);
			}

			if(maxIndexWithSameValue > 1){
				splitPoint += maxIndexWithSameValue -1;
				continue;
			}

			List<Value> splitAfterThreshold = valueList.subList(splitPoint + 1, valueList.size());	

			if (!(splitBeforeThreshold.size() >=  minSplit && splitAfterThreshold.size() >=  minSplit)) {
				splitPoint++;
				continue;
			}

			Double instancesWithKnownValuesSize = getSizeOfInstancesWithKnownValuesForAttribute(attribute, instances, frequencyCounts);
			Double weightedEntropy = getWeightedEntropyForValuesSubset(attribute, splitBeforeThreshold, frequencyCounts,
					instancesWithKnownValuesSize,  targetClassCount);			
			weightedEntropy += getWeightedEntropyForValuesSubset(attribute, splitAfterThreshold, frequencyCounts,
					instancesWithKnownValuesSize,  targetClassCount);
			Double knownValueInstancesRatio =  ((double)instancesWithKnownValuesSize) /((double) instances.sumOfWeights());			
			Double infoGainForSplit = knownValueInstancesRatio * (entropyOfTrainingSample - weightedEntropy);

			BestAttributeSearchResult attributeSearchResultWithMaxInfoGain = new BestAttributeSearchResult(attribute, infoGainForSplit, 
					splitPoint,  thresholdValue, splitBeforeThreshold, splitAfterThreshold);

			if(Double.compare(infoGainForSplit, maxInfoGain) > 0){

				maxInfoGain = infoGainForSplit;
				bestAttributeSearchResultWithMaxInfoGain = attributeSearchResultWithMaxInfoGain;
			}			
			splitPoint++;
		}

		if(bestAttributeSearchResultWithMaxInfoGain != null){
			splitPoint = bestAttributeSearchResultWithMaxInfoGain.getSplittingPoint();
			double thresholdVal = 0.0;
			if(splitPoint + 1 < valueList.size()){
				thresholdVal = (valueList.get(splitPoint).getNumericValueAsDouble()
						+ valueList.get(splitPoint +1).getNumericValueAsDouble()) / 2.0;
			}
			else{
				thresholdVal = valueList.get(splitPoint).getNumericValueAsDouble();
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

		//		
		//		
		//		Map<Attribute, List<Instance>> attributeAndInstancesWithMissingValues = frequencyCounts.getAttributeAndMissingValueInstances();
		//		List<Instance> instancesWithMissingValues = attributeAndInstancesWithMissingValues.get(attribute);
		//		if(instancesWithMissingValues != null){
		//			instancesWithKnownValuesSize = instances.sumOfWeights() - instancesWithMissingValues.size();
		//		}

		return instancesWithKnownValuesSize;
	}

	private Double getWeightedEntropyForValuesSubset(Attribute attribute, List<Value> split, 
			FrequencyCounts frequencyCounts, Double instancesSize,  
			Map<Value, Map<Value, Double>> targetClassCount){

		double totalOccurencesOfValue = 0.0;
		Map<Value, Double> cummulativeCounts = new HashMap<Value, Double>();
		for(Value val : split){
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


	private void pruneDecisionTree() {

		List<DecisionTreeNode> processedParentNodes = new ArrayList<>();

		for(DecisionTreeNode leafNode : leafNodes){
			DecisionTreeNode parent = (DecisionTreeNode)decisionTree.incomingEdgesOf(leafNode).iterator().next().getSource();
			if(processedParentNodes.contains(parent)){
				continue;
			}

			List<DecisionTreeNode> leafSiblings = getLeafSiblingsOfLeafNode(leafNode, parent);			
			leafSiblings.add(leafNode);			
			processedParentNodes.add(parent);			
		}		
	}

	private List<DecisionTreeNode> getLeafSiblingsOfLeafNode(DecisionTreeNode leafNode, DecisionTreeNode parent){

		Set<DecisionTreeEdge> outGoingEdgesOfParent = decisionTree.outgoingEdgesOf(parent);
		Iterator<DecisionTreeEdge> iterator = outGoingEdgesOfParent.iterator();
		List<DecisionTreeNode> siblings = new ArrayList<>();
		while(iterator.hasNext()){
			DecisionTreeNode child = (DecisionTreeNode)iterator.next().getTarget();
			if(child == leafNode){
				continue;
			}

			siblings.add(child);

		}		
		return siblings;
	}

	private Double errorRateOfLeafChildren(DecisionTreeNode node, List<DecisionTreeNode> childLeaves){		
		Double errorRate = 0.0;

		Double totalInstances = node.getNumInstances();

		for(DecisionTreeNode childNode : childLeaves){
			Double numInstances = childNode.getNumInstances();

			Double observedError = observedErrorAtNode(node);	
			Double errorRateAtNode = errorRateAtNode(observedError, numInstances);

			errorRate += ((double) numInstances / (double)totalInstances) * errorRateAtNode;
		}

		return errorRate;		
	}

	private Double observedErrorAtNode(DecisionTreeNode node){
		Map<Value, Double> targetClassCounts = node.getTargetClassCounts();
		Double numInstances = node.getNumInstances();

		Double maxCount = 0.0;
		Iterator<Entry<Value, Double>> iterator = targetClassCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Double count = iterator.next().getValue();
			if(Double.compare(count, maxCount) > 0){
				maxCount = count;
			}
		}		
		return 1.0 - ((double)maxCount / (double)numInstances);			
	}

	private Double errorRateAtNode(Double observedError, Double totalSamples){
		Double errorRate = observedError;
		errorRate += Math.pow(NUM_STANDARD_DEVIATIONS, 2.0) / (2.0 * totalSamples);
		errorRate += NUM_STANDARD_DEVIATIONS * (Math.sqrt(
				(observedError / totalSamples) 
				- (Math.pow(observedError, 2.0) / totalSamples)
				+ (Math.pow(NUM_STANDARD_DEVIATIONS, 2.0) / (4.0 * Math.pow(totalSamples, 2.0)))));

		errorRate /= 1.0 + (Math.pow(NUM_STANDARD_DEVIATIONS, 2.0) / totalSamples);		
		return errorRate;
	}

	public DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> getDecisionTree(){
		return decisionTree;
	}

	public DecisionTreeNode getDecisionTreeRootNode() {
		return decisionTreeRootNode;
	}
}
