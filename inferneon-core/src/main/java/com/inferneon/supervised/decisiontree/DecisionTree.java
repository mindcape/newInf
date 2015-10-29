package com.inferneon.supervised.decisiontree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.inferneon.core.utils.MathUtils;
import com.inferneon.supervised.FrequencyCounts;

public class DecisionTree extends DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge>{

	private DecisionTreeNode decisionTreeRootNode; 
	
	public DecisionTree(Class<? extends DecisionTreeEdge> edgeClass) {
		super(edgeClass);
	}
	
	public Set<DecisionTreeNode> getChildNodes(DecisionTreeNode node) {
		Set<DecisionTreeEdge> outgoingEdges = outgoingEdgesOf(node);
		Iterator<DecisionTreeEdge> iterator = outgoingEdges.iterator();
		Set<DecisionTreeNode> childNodes = new HashSet<>();
		while(iterator.hasNext()){
			DecisionTreeEdge edge = iterator.next();
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
		Stack<Integer> depthStack = new Stack<>();
		depthStack.push(0);
		
		emitTree(decisionTreeRootNode, depthStack);
	}
	
	private void emitTree(DecisionTreeNode node, Stack<Integer> depthStack) {
		if(node == null){
			node = decisionTreeRootNode;
		}		

		Set<DecisionTreeEdge> outgoingEdges = outgoingEdgesOf(node);
		if(outgoingEdges.size() == 0){
			return;
		}

		System.out.println(node + ":");

		Iterator<DecisionTreeEdge> edgesIterator = outgoingEdges.iterator();		
		List<DecisionTreeNode> children = new ArrayList<>();		
		while(edgesIterator.hasNext()){
			DecisionTreeEdge edge = edgesIterator.next();
			DecisionTreeNode target = (DecisionTreeNode)edge.getTarget();

			String distDescription = "";
			if(target.isLeaf()){
				FrequencyCounts frequencyCounts = target.getFrequencyCounts();
				distDescription += "(" + frequencyCounts.getDistrbutionDesc() + ")";
			}

			String childDesc = "	(" + edge + ") -> " + target  +  " " + distDescription;
			System.out.println(childDesc.trim());
			children.add(target);
		}

		for(DecisionTreeNode child : children){
			emitTree(child, depthStack);
		}		
	}
}