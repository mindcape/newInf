package com.inferneon.supervised.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Value;
import com.inferneon.supervised.functions.NeuralNode.TYPE;

public class StochasticMultilayerPerceptron {

	private IInstances instance;
	private List<Attribute> attributes;
	/** The number of attributes. */
	private int m_numAttributes = 0;
	private Random random;
	private MultilayerNeuralNetwork network;
	private double learningRate;

	private String hiddenLayerDesc;
	public StochasticMultilayerPerceptron(String hiddenLayerDesc, double learningRate){
		this.hiddenLayerDesc = hiddenLayerDesc;
		this.learningRate = learningRate;
	}

	public void learn(IInstances instance){
		this.instance = instance;
		attributes = instance.getAttributes();
		// Create the network
		createNetwork();
		
		// Train the network
		trainNetwork();
		
	}

	private void createNetwork(){

		network = new MultilayerNeuralNetwork(NeuralConnnection.class);

		// Create the input nodes
		List<NeuralNode> inputNodes = new ArrayList<>();
		int classIndex = instance.getClassIndex();
		for(int i =0; i < attributes.size(); i++){
			if(i == classIndex){
				continue;
			}
			NeuralNode inputNode = new NeuralNode(attributes.get(i).getName(), TYPE.INPUT, random);
			inputNode.setOutput(Double.NaN);
			inputNode.setAttr(attributes.get(i));
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
				outputNode.setAttr(target);
				network.addVertex(outputNode);
				outputNodes.add(outputNode);
			}
		}
		else {
			NeuralNode outputNode = new NeuralNode(target.getName(), TYPE.OUTPUT, random);
			outputNode.setOutput(Double.NaN);
			outputNode.setAttr(target);
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
							List<Double> weights = hiddenNode.getWeights();
							weights.add(NeuralNode.getSomeHardCodeWeight(hiddenNode.getName()));
							hiddenNode.setWeights(weights);
							List<Double> cweights = hiddenNode.getChangeInweights();
							cweights.add(0d);
							hiddenNode.setChangeInweights(cweights);
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
							List<Double> weights = hiddenNode.getWeights();
							weights.add(NeuralNode.getSomeHardCodeWeight(hiddenNode.getName()));
							hiddenNode.setWeights(weights);
							List<Double> cweights = hiddenNode.getChangeInweights();
							cweights.add(0d);
							hiddenNode.setChangeInweights(cweights);
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
								List<Double> weights = outputNodes.get(k).getWeights();
								weights.add(NeuralNode.getSomeHardCodeWeight(outputNodes.get(k).getName()));
								outputNodes.get(k).setWeights(weights);
								List<Double> cweights = outputNodes.get(k).getChangeInweights();
								cweights.add(0d);
								outputNodes.get(k).setChangeInweights(cweights);
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
		instance.stochasticTrainNeuralNetwork(network, learningRate);
		
	}
}
