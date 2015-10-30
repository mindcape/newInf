package com.inferneon.supervised.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.inferneon.core.InstanceComparator;
import com.inferneon.core.utils.MathUtils;
import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.decisiontree.DecisionTreeNode.Type;
import com.inferneon.supervised.linearregression.LinearModel;

public class DecisionTree extends DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge>{

	private DecisionTreeNode decisionTreeRootNode; 
	
	public DecisionTree(Class<? extends DecisionTreeEdge> edgeClass) {
		super(edgeClass);
	}
	
	public List<DecisionTreeNode> getChildNodes(DecisionTreeNode node) {
		Set<DecisionTreeEdge> outgoingEdges = outgoingEdgesOf(node);
		List<DecisionTreeEdge> edgesList = new ArrayList<>(outgoingEdges);
		Collections.sort(edgesList, new DecisionTreeEdgeComparator());
		
		List<DecisionTreeNode> childNodes = new ArrayList<>();		
		for(DecisionTreeEdge edge : edgesList){
			childNodes.add((DecisionTreeNode)edge.getTarget());
		}

		return childNodes;
	}

	public DecisionTreeNode getParentOfNode(DecisionTreeNode node){		
		DecisionTreeEdge incomingEdge = incomingEdgeOfNode(node);
		if(incomingEdge == null){
			return null;
		}
		return (DecisionTreeNode)incomingEdge.getSource();		
	}

	public DecisionTreeEdge incomingEdgeOfNode(DecisionTreeNode node){
		Set<DecisionTreeEdge> incomingEdges = incomingEdgesOf(node);
		if(incomingEdges == null || incomingEdges.size() == 0){
			return null;
		}

		DecisionTreeEdge incomingEdge = (DecisionTreeEdge)incomingEdges.iterator().next();		
		return incomingEdge;
	}	
	
	public DecisionTreeNode getDecisionTreeRootNode() {
		return decisionTreeRootNode;
	}

	public void setDecisionTreeRootNode(DecisionTreeNode decisionTreeRootNode) {
		this.decisionTreeRootNode = decisionTreeRootNode;
	}	
	
	public void emitTree(){
		StringBuffer treeDesc = new StringBuffer();
		emitTree(decisionTreeRootNode, 0, treeDesc);
		System.out.println(treeDesc.toString());
	}
	
	private void emitTree(DecisionTreeNode node, int level, StringBuffer treeDesc){
		if(!node.isLeaf()){
			List<DecisionTreeNode> childNodes = getChildNodes(node);	
			for(DecisionTreeNode target : childNodes){
				treeDesc.append(System.getProperty("line.separator"));
				for (int j = 0; j < level; j++) {
					treeDesc.append("|   ");
				}
				
				treeDesc.append(node.toString());
				DecisionTreeEdge edge = this.getEdge(node, target);
				treeDesc.append(" " + edge);
				if (target.isLeaf()) {
					treeDesc.append(": ");
					
				} else {
					emitTree(target, level + 1, treeDesc);
				}
			}
		}
		else{
			treeDesc.append(": ");
			FrequencyCounts frequencyCounts = node.getFrequencyCounts();
			treeDesc.append(node + " (" + frequencyCounts.getDistrbutionDesc() + ")" + System.getProperty("line.separator"));
		}
	}
	
	private String emitLeafNodeDesc(DecisionTreeNode node){
		String desc = "";
		if(node.isLeaf()){
			return desc;
		}
		
		if(node.getType() == Type.VALUE){
			FrequencyCounts frequencyCounts = node.getFrequencyCounts();
			desc = node + " (" + frequencyCounts.getDistrbutionDesc() + ")";
			return desc;
		}		
		
		// Must be of type linear model
		LinearModel linearModel = node.getLinearModel();
		return linearModel.toString();
	}
}
