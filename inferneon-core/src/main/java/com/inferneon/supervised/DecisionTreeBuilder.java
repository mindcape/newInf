package com.inferneon.supervised;

import java.util.ArrayList;
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
import com.inferneon.core.exceptions.InvalidDataException;

public class DecisionTreeBuilder extends Supervised{

	public enum Criterion {
		INFORMATION_GAIN,
		GINI
	}

	public enum Method {
		ID3,
		C45
	}

	private Criterion criteria;
	private Method method;

	private DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> decisionTree;
	private DecisionTreeNode decisionTreeRootNode; 
	private List<Attribute> attributes;

	public DecisionTreeBuilder(){
		method = Method.ID3;
		criteria = Criterion.INFORMATION_GAIN;
		decisionTree = new DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge>(DecisionTreeEdge.class);
	}

	public DecisionTreeBuilder(Method method){
		this.method = method;
		criteria = Criterion.INFORMATION_GAIN;
		decisionTree = new DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge>(DecisionTreeEdge.class);
	}

	public DecisionTreeBuilder(Method method, Criterion criteria){
		this.method = method;
		this.criteria = criteria;
		decisionTree = new DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge>(DecisionTreeEdge.class);
	}

	@Override
	public void train(Instances instances) {
		this.attributes = instances.getAttributes();
		try {
			if(method == Method.ID3){
				ID3 id3 = new ID3(criteria);
				id3.train(instances);
				decisionTree = id3.getDecisionTree();
				decisionTreeRootNode = id3.getDecisionTreeRootNode();
			}
			else if(method == Method.C45){
				C45 c45 = new C45(criteria);
				c45.train(instances);
				decisionTree = c45.getDecisionTree();
				decisionTreeRootNode = c45.getDecisionTreeRootNode();
			}
		}
		catch (CycleFoundException e) {
			e.printStackTrace();
		} catch (InvalidDataException e) {
			e.printStackTrace();
		}

		emitTree(null);		
	}

	public DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> getDecisionTree(){
		return decisionTree;
	}
	
	public DecisionTreeNode getRootNode(){
		return decisionTreeRootNode;
	}

	@Override
	public Value classify(Instance instance) {
		DecisionTreeNode node = decisionTreeRootNode;

		while(decisionTree.outgoingEdgesOf(node) != null && decisionTree.outgoingEdgesOf(node).size() > 0){			
			Attribute attribute = node.getAttribute();
			Value value = null;
			if(attribute.getType() == Attribute.Type.NOMINAL){
				value = instance.attributeValue(attribute);
			}
			else{
				value = instance.getValue(attributes.indexOf(attribute));
			}

			node = nextAttribute(node, value);
		}

		return node.getValue();
	}

	private DecisionTreeNode nextAttribute(DecisionTreeNode node, Value value){

		Set<DecisionTreeEdge> outgoingEdges = decisionTree.outgoingEdgesOf(node);
		Iterator<DecisionTreeEdge> iterator = outgoingEdges.iterator();
		while(iterator.hasNext()){
			DecisionTreeEdge dte = iterator.next();
			if(dte.getType() == DecisionTreeEdge.Type.VALUE){
				if(dte.getValue().equals(value)){
					return decisionTree.getEdgeTarget(dte);
				}			
			}
			else{
				PredicateEdge predicateEdge = dte.getPredicateEdge();
				if(predicateEdge.test(value)){
					return decisionTree.getEdgeTarget(dte);
				}
			}
		}

		return null;
	}

	private void emitTree(DecisionTreeNode node) {
		if(node == null){
			node = decisionTreeRootNode;
		}		

		Set<DecisionTreeEdge> outgoingEdges = decisionTree.outgoingEdgesOf(node);
		if(outgoingEdges.size() == 0){
			return;
		}

		System.out.println(node + ":");

		Iterator<DecisionTreeEdge> edgesIterator = outgoingEdges.iterator();		
		List<DecisionTreeNode> children = new ArrayList<>();		
		while(edgesIterator.hasNext()){
			DecisionTreeEdge edge = edgesIterator.next();
			DecisionTreeNode target = (DecisionTreeNode)edge.getTarget();

			Map<Value, Double> targetClassCounts = target.getTargetClassCounts();
			Iterator<Entry<Value, Double>> iterator = targetClassCounts.entrySet().iterator();
			int numEntries = targetClassCounts.size();
			int count = 0;

			String distDescription = "";
			if(decisionTree.outgoingEdgesOf(target) != null && decisionTree.outgoingEdgesOf(target).size() == 0){
				while(iterator.hasNext()){
					Entry<Value, Double> entry = iterator.next();
					Value value = entry.getKey(); 
					Double total = entry.getValue();
					distDescription += "(" + value.getName() + ": " + total + (count < numEntries -1 ? ", " : "");
					count++;
				}
				
				distDescription += ")";
				
			}

			System.out.println("	(" + edge + ") -> " + target  + distDescription);
			children.add(target);
		}

		for(DecisionTreeNode child : children){
			emitTree(child);
		}		
	}	
}
