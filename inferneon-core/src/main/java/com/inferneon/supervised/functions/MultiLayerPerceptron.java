package com.inferneon.supervised.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Value;
import com.inferneon.supervised.functions.NeuralNode.TYPE;

public class MultiLayerPerceptron {

	private IInstances instances;
	private List<Attribute> attributes;
	/** The number of attributes. */
	private int m_numAttributes = 0;
	private Random random;
	private MultilayerNeuralNetwork network;

	private String hiddenLayerDesc;
	public MultiLayerPerceptron(String hiddenLayerDesc){
		this.hiddenLayerDesc = hiddenLayerDesc;
	}

	public void learn(IInstances instances){
		this.instances = instances;
		attributes = instances.getAttributes();
		// Create the network
		createNetwork();
		
		// Train the network
		trainNetwork();
		
	}

	private void createNetwork(){

		network = new MultilayerNeuralNetwork(NeuralConnnection.class);

		// Create the input nodes
		List<NeuralNode> inputNodes = new ArrayList<>();
		int classIndex = instances.getClassIndex();
		for(int i =0; i < attributes.size(); i++){
			if(i == classIndex){
				continue;
			}
			NeuralNode inputNode = new NeuralNode(attributes.get(i).getName(), TYPE.INPUT, random);
			inputNode.setOutput(Double.NaN);
			network.addVertex(inputNode);
			inputNodes.add(inputNode);
		}
		// Create the output nodes
		Attribute target = attributes.get(classIndex);
		List<Value> targetValues;
		List<NeuralNode> outputNodes = new ArrayList<>();
		if(target.getType() == Attribute.Type.NOMINAL){
			targetValues = target.getAllValues();
			for(int i =0; i < targetValues.size(); i++){
				NeuralNode outputNode = new NeuralNode(targetValues.get(i).toString(), TYPE.OUTPUT, random);
				outputNode.setOutput(Double.NaN);
				network.addVertex(outputNode);
				outputNodes.add(outputNode);
			}
		}
		else {
			NeuralNode outputNode = new NeuralNode(target.getName(), TYPE.OUTPUT, random);
			outputNode.setOutput(Double.NaN);
			network.addVertex(outputNode);
			outputNodes.add(outputNode);
		}
		// Create the hidden nodes
		String[] layers = hiddenLayerDesc.split(",");		
		int numLayers = layers.length;
		List<NeuralNode> prevHiddenNodes = new ArrayList<>();
		List<List<NeuralNode>> hiddenLayers = new ArrayList<>(); 
		for(int i = 0; i < numLayers; i++){
			String layer = layers[i];
			int numNodesInLayer = Integer.parseInt(layer.trim());
			List<NeuralNode> currHiddenNodes = new ArrayList<>();
			for(int j = 0; j < numNodesInLayer; j++){
				NeuralNode hiddenNode = new NeuralNode("hidden_"+(i+1)+"_"+(j+1), TYPE.HIDDEN, random);
				network.addVertex(hiddenNode);
				if(i == 0){
					// First hidden layer
					for(int k = 0; k < inputNodes.size(); k++) {
					//	double weight = Math.random();
						NeuralConnnection connection = new NeuralConnnection();
						try {
							connection.setSource(inputNodes.get(k));
							connection.setTarget(hiddenNode);
							network.addDagEdge(inputNodes.get(k), hiddenNode, connection);
						} catch (CycleFoundException e) {
							e.printStackTrace();
						}
					}
				}
				else if(i <= (numLayers-1)){
					// Some inner hidden layer
					for(int l = 0; l < prevHiddenNodes.size(); l++) {
					//	double weight = Math.random();
						NeuralConnnection connection = new NeuralConnnection();
						try {
							connection.setSource(prevHiddenNodes.get(l));
							connection.setTarget(hiddenNode);
							network.addDagEdge(prevHiddenNodes.get(l), hiddenNode, connection);
						} catch (CycleFoundException e) {
							e.printStackTrace();
						}
					}
					if(i == (numLayers-1)) {
						// Last layer
						for(int k = 0; k < outputNodes.size(); k++) {
						//	double weight = Math.random();
							NeuralConnnection connection = new NeuralConnnection();
							try {
								connection.setSource(hiddenNode);
								connection.setTarget(outputNodes.get(k));
								network.addDagEdge(hiddenNode, outputNodes.get(k), connection);
							} catch (CycleFoundException e) {
								e.printStackTrace();
							}
						}
					}
					
				}
				hiddenNode.setOutput(Double.NaN);
				currHiddenNodes.add(hiddenNode);
			}
			hiddenLayers.add(currHiddenNodes);
			prevHiddenNodes = currHiddenNodes;
			
		}
		network.setInputNodes(inputNodes);
		network.setHiddenLayers(hiddenLayers);
		network.setOutputNodes(outputNodes);
	}

	public MultilayerNeuralNetwork getNetwork() {
		
		return network;		
	}
	
	private void trainNetwork() {
		instances.trainNeuralNetwork(network);
		
	}
}
