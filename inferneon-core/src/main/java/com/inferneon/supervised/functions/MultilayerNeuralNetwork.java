package com.inferneon.supervised.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.inferneon.supervised.functions.NeuralNode.TYPE;

public class MultilayerNeuralNetwork extends DirectedAcyclicGraph<NeuralNode, NeuralConnnection>{
	/**
	 * 
	 */
	private List<NeuralNode> rootNodes;
	private List<List<NeuralNode>> hiddenLayers;
	private List<NeuralNode> hiddenNodes;
	private List<NeuralNode> outputNodes;
	public MultilayerNeuralNetwork(Class<? extends NeuralConnnection> neuralConnection) {
		super(neuralConnection);
	}
	public void setInputNodes(List<NeuralNode> inputNodes) {
		this.rootNodes = inputNodes;
		
	}
	public List<NeuralNode> getInputNodes() {
		return rootNodes;
		
	}
	
	public List<NeuralNode> getHiddenNodes() {
		return hiddenNodes;
	}
	public void setHiddenNodes(List<NeuralNode> hiddenNodes) {
		this.hiddenNodes = hiddenNodes;
	}
	public List<NeuralNode> getOutputNodes() {
		return outputNodes;
	}
	public void setOutputNodes(List<NeuralNode> outputNodes) {
		this.outputNodes = outputNodes;
	}
	public List<List<NeuralNode>> getHiddenLayers() {
		return hiddenLayers;
	}
	public void setHiddenLayers(List<List<NeuralNode>> hiddenLayers) {
		this.hiddenLayers = hiddenLayers;
	}
	
	public void emitTree() {
		for(int i=0; i < rootNodes.size(); i++) {
			emitTree(rootNodes.get(i));
		}

	}
	private void emitTree(NeuralNode neuralNode) {		

		Set<NeuralConnnection> outgoingConnections = outgoingEdgesOf(neuralNode);
		if(outgoingConnections.size() == 0){
			return;
		}

		System.out.println(neuralNode.getName() + ":");

		Iterator<NeuralConnnection> connectionsIterator = outgoingConnections.iterator();		
		List<NeuralNode> children = new ArrayList<>();		
		while(connectionsIterator.hasNext()){
			NeuralConnnection connection = connectionsIterator.next();
			NeuralNode target = (NeuralNode)connection.getTarget();

			String distDescription = "";
			if(target.getType() == TYPE.OUTPUT){
				distDescription += "(Output)";
			}

			String childDesc = "	(" + connection.getWeight() + ") -> " + target.getName()  +  " " + distDescription;
			System.out.println(childDesc.trim());
			children.add(target);
		}

		for(NeuralNode child : children){
			emitTree(child);
		}		
		
	}

}
