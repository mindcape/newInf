package com.inferneon.supervised;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.Value;
import com.inferneon.supervised.DecisionTreeBuilder.Criterion;

public class CARTM5Extension extends Supervised {

	private static final Logger LOG = LoggerFactory.getLogger(CARTM5Extension.class);
	private static final long MIN_NUM_INSTANCES = 4;
	private static final double MIN_STD_DEV_FRACTION = 0.05;
	private IInstances allInstances;
	private DecisionTree decisionTree;
	private Attribute classAttribute;

	private boolean pruneTree;
	private int impurityOrder = 5;

	private double standardDeviationOfEntireSample;

	public CARTM5Extension(){
		decisionTree = new DecisionTree(DecisionTreeEdge.class);
	}
	
	@Override
	public void train(IInstances instances) throws Exception {
		List<Attribute> attributes = instances.getAttributes();
		int classIndex = instances.getClassIndex();
		classAttribute = attributes.get(classIndex);
		standardDeviationOfEntireSample = instances.standardDeviation(classAttribute, 0, instances.size());

		instances = instances.convertNominalToSyntheticBinaryAttributes();		
		this.allInstances = instances;	
		long before = new Date().getTime();
		train(null, null, instances);

		LOG.debug("TREE DETERMINED AFTER TRAINING: ");
		decisionTree.emitTree();

		if(pruneTree){
			pruneDecisionTree(decisionTree.getDecisionTreeRootNode());
			System.out.println("TREE AFTER PRUNING: ");
		}

		long after = new Date().getTime();

		double timeElapsedDbl = ((double)(after - before)) / 1000.0;
		LOG.debug("Decision tree computed in: " + (long)timeElapsedDbl + " seconds");		
	}

	private void train(DecisionTreeNode parentDecisionTreeNode, DecisionTreeEdge decisionTreeEdge, IInstances instances) 
			throws Exception{

		LOG.debug(parentDecisionTreeNode == null && decisionTreeEdge == null ? "Training start at root" 
				: "Training on node decisionTreeNode " + parentDecisionTreeNode + " on outgoing edge " + decisionTreeEdge);
		
		if(decisionTreeEdge != null && "<= 116.5".equals(decisionTreeEdge.toString())){
			System.out.println("WAIT HERE");
		}

		BestAttributeSearchResult bestAttributeSearchResult = searchForBestAttributeToSplitOn(instances);
		if(bestAttributeSearchResult == null){
			// Could not find a suitable attribute to split on. Create leaf and return			
			//createLeavesForAttribute(parentDecisionTreeNode, decisionTreeEdge,  mostFrequentlyOccuringTargetValue, frequencyCounts);
			//LOG.debug("No suitable attribute to split on, created leaf " + mostFrequentlyOccuringTargetValue);

			return;
		}

		// Found a suitable attribute to split on
		Attribute attribute = bestAttributeSearchResult.getAttribute();
		Impurity impurity = bestAttributeSearchResult.getImpurity();

		// Add this attribute to the tree and and edge from the parent node to this attribute 
		DecisionTreeNode decisionTreeNode = new DecisionTreeNode(impurity, attribute);
		addAttributeToTree(parentDecisionTreeNode, decisionTreeNode, decisionTreeEdge);

		// Split the instances based on left and right values of this attribute
		Value threshold = bestAttributeSearchResult.getThreshold();		
		PredicateEdge leftPredicateEdge = new PredicateEdge(attribute, threshold, true);
		DecisionTreeEdge leftEdge = new DecisionTreeEdge(leftPredicateEdge);				
		PredicateEdge rightPredicateEdge = new PredicateEdge(attribute, threshold, false);
		DecisionTreeEdge rightEdge = new DecisionTreeEdge(rightPredicateEdge);

		// Train each of the splits recursively
		train(decisionTreeNode, leftEdge, bestAttributeSearchResult.getSplitBeforeThreshold());
		train(decisionTreeNode, rightEdge, bestAttributeSearchResult.getSplitAfterThreshold());
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

	private BestAttributeSearchResult searchForBestAttributeToSplitOn(IInstances instances) {
		if(!checkMinCriteriaBased(instances)){
			return null;
		}

		double maxImpurityValue = Double.MIN_VALUE;
		Impurity maxImpurity = null;

		BestAttributeSearchResult result = null;

		List<Attribute> attributes = instances.getAttributes();

		int count = 0;
		for(Attribute attribute : attributes){
			if(count == instances.getClassIndex()){
				count++;
				continue;
			}
			instances.sort(attribute);

			Impurity impurityForAttribute = getMaxImpurityForAttribute(instances, attribute, impurityOrder);
			System.out.println(impurityForAttribute);
						
			if(impurityForAttribute != null && Double.compare(impurityForAttribute.getImpurityValue(), maxImpurityValue) > 0){
				double impurityValForAttr = impurityForAttribute.getImpurityValue();
				maxImpurityValue = impurityValForAttr;	
				maxImpurity = impurityForAttribute;
				double splitValue = impurityForAttribute.getSplitValue();
				maxImpurity.setSplitValue(splitValue);
			}
			count++;
		}

		if(maxImpurity != null){
			long splitPoint = (long)maxImpurity.getNumOnLeftGroup() -1;
			double splitNum = maxImpurity.getSplitValue();
			Value splitValue = new Value(splitNum);
			IInstances splitBeforeThreshold = instances.getSubList(0, splitPoint + 1);
			IInstances splitAfterThreshold = instances.getSubList(splitPoint + 1, instances.size());
			Attribute attribute = maxImpurity.getAttribute();
			result = new BestAttributeSearchResult(attribute, maxImpurity, splitPoint,  splitValue, 
					splitBeforeThreshold, splitAfterThreshold);
		}

		return result;
	}

	private boolean checkMinCriteriaBased(IInstances instances) {
		long numInsts = instances.size();
		if (numInsts < MIN_NUM_INSTANCES){
			return false;
		}
		
		double standardDev = instances.standardDeviation(classAttribute, 0, numInsts);
		double minStandardDev = standardDeviationOfEntireSample * MIN_STD_DEV_FRACTION;		
		if (Double.compare(standardDev, minStandardDev) < 0){
			return false;
		} 
		
		return true;
	}

	public Impurity getMaxImpurityForAttribute(IInstances instances, Attribute attribute, int impurityOrder) {
		long numInstances = instances.size();
		if(numInstances < Impurity.MIN_NUM_INSTANCES){
			// TODO Implement this later
			return null;
		}

		long offset = (numInstances < 5) ? 1 : numInstances / 5;
		Impurity impurity = instances.initializeImpurity(attribute, offset, impurityOrder);

		long startIndex = offset;
		long endIndex = numInstances - offset -1;		
		impurity = instances.updateImpurity(startIndex, endIndex, impurity);

		return impurity;
	}

	private void createLeavesForAttribute(DecisionTreeNode parentNode, DecisionTreeEdge edge, Value targetValue, Impurity impurity) throws CycleFoundException {
//		DecisionTreeNode leafNode = new DecisionTreeNode(impurity, targetValue);
//		decisionTree.addVertex(leafNode);
//		edge.setSource(parentNode); edge.setTarget(leafNode);
//		System.out.println("Adding edge " + edge + " between attributes " + parentNode + " and leaf node " + leafNode);	
//
//		decisionTree.addDagEdge(parentNode, leafNode, edge);		
	}


	private void pruneDecisionTree(DecisionTreeNode decisionTreeRootNode) {
		// TODO Auto-generated method stub
		// Needs to be pruned to ensure that we use Minimum description length or Occam's razor : simplicity 

	}

	@Override
	public Value classify(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	public DecisionTree getDecisionTree() {
		return decisionTree;
	}
}
