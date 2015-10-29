package com.inferneon.supervised.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.inferneon.supervised.FrequencyCounts;
import com.inferneon.supervised.decisiontree.DecisionTreeEdge;
import com.inferneon.supervised.decisiontree.DecisionTreeNode;

public class DescriptiveTree extends DirectedAcyclicGraph<DescriptiveTreeNode, DescriptiveTreeEdge>{

	private DescriptiveTreeNode rootNode; 

	public DescriptiveTree() {
		super(DescriptiveTreeEdge.class);
	}
	
	public DescriptiveTree(DescriptiveTreeNode rootNode) {
		super(DescriptiveTreeEdge.class);
		this.rootNode = rootNode;
	}
	
	public DescriptiveTreeNode getRootNode(){
		return rootNode;
	}
	
	public void setRootNode(DescriptiveTreeNode node){
		this.rootNode = node;
	}
	
	public void emitTree(){
		emitTree(rootNode);
	}
	
	private void emitTree(DescriptiveTreeNode node) {
		if(node == null){
			node = rootNode;
		}		

		Set<DescriptiveTreeEdge> outgoingEdges = outgoingEdgesOf(node);
		if(outgoingEdges.size() == 0){
			return;
		}

		System.out.println(node + ":");

		Iterator<DescriptiveTreeEdge> edgesIterator = outgoingEdges.iterator();		
		List<DescriptiveTreeNode> children = new ArrayList<>();		
		while(edgesIterator.hasNext()){
			DescriptiveTreeEdge edge = edgesIterator.next();
			DescriptiveTreeNode target = getEdgeTarget(edge);

			String distDescription = "";
			if(target.isLeaf()){
				distDescription += "(" + target.getLeafDistribution() + ")";
			}

			String childDesc = "	(" + edge + ") -> " + target  +  " " + distDescription;
			System.out.println(childDesc.trim());
			children.add(target);
		}

		for(DescriptiveTreeNode child : children){
			emitTree(child);
		}		
	}
}
