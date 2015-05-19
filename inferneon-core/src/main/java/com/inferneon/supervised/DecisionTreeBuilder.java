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
		GAIN_RATIO,
		GINI
	}

	public enum Method {
		ID3,
		C45
	}

	private Criterion criteria;
	private Method method;

	private DecisionTree decisionTree;
	private DecisionTreeNode decisionTreeRootNode; 
	private List<Attribute> attributes;

	public DecisionTreeBuilder(){
		method = Method.ID3;
		criteria = Criterion.INFORMATION_GAIN;
		decisionTree = new DecisionTree(DecisionTreeEdge.class);
	}

	public DecisionTreeBuilder(Method method){
		this.method = method;
		criteria = Criterion.INFORMATION_GAIN;
		decisionTree = new DecisionTree(DecisionTreeEdge.class);
	}

	public DecisionTreeBuilder(Method method, Criterion criteria){
		this.method = method;
		this.criteria = criteria;
		decisionTree = new DecisionTree(DecisionTreeEdge.class);
	}

	@Override
	public void train(Instances instances) {
		this.attributes = instances.getAttributes();
		try {
			if(method == Method.ID3){
				ID3 id3 = new ID3(criteria);
				id3.train(instances);
				decisionTree = id3.getDecisionTree();
				decisionTreeRootNode = decisionTree.getDecisionTreeRootNode();
			}
			else if(method == Method.C45){
				C45 c45 = new C45(criteria);
				c45.train(instances);
				decisionTree = c45.getDecisionTree();
				decisionTreeRootNode = decisionTree.getDecisionTreeRootNode();
			}
		}
		catch (CycleFoundException e) {
			e.printStackTrace();
		} catch (InvalidDataException e) {
			e.printStackTrace();
		}

		decisionTree.emitTree(null);		
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
}
