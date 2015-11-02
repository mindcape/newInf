package com.inferneon.supervised.neuralnetwork;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.Value;
import com.inferneon.supervised.Supervised;
import com.inferneon.supervised.neuralnetwork.NeuralNode.TYPE;

public class MultiLayerPerceptron extends Supervised {

	private IInstances instances;
	private List<Attribute> attributes;
	/** The number of attributes. */
	private int numEpoch = 2;
	//TODO get set

	


	private Random random;
	private MultilayerNeuralNetwork network;
	private double learningRate;
	private double momentum;
	private boolean isStochastic;

	
	private Integer[] hiddenLayersConfig;

	public MultiLayerPerceptron(Integer[] hiddenLayersConfig,  double learningRate, double momentum ,boolean isStochastic){
		this.hiddenLayersConfig = hiddenLayersConfig;
		this.learningRate = learningRate;
		this.isStochastic = isStochastic;
		this.momentum = momentum;
	}

	@Override
	public void train(IInstances instances) throws Exception {

		// TODO Ensure that there must be at least one hidden layer

		this.instances = instances;
		attributes = instances.getAttributes();

		int numWeights = getNumWeights(attributes.size(), hiddenLayersConfig);

		// Create the network
		createNetwork();

		// Train the network
		trainNetwork();			
	}

	protected int getNumWeights(int numAttributes, Integer[] hiddenLayersConfig) {

		// TODO Also check for some upper bound of layers and number of nodes in each layer

		if(hiddenLayersConfig.length == 0){
			throw new IllegalArgumentException("MLP must have at least one hidden layer.");
		}
		int numInputs = numAttributes -1;

		int sum = 0;
		int count = 0;
		for(Integer numNodesInLayer :  hiddenLayersConfig){
			if(numNodesInLayer < 1){
				// At least one node in a layer
				throw new IllegalArgumentException("A hidden layer must have at least one node.");
			}
			if(count == 0){
				sum = (numInputs * numNodesInLayer) + numNodesInLayer;
			}
			else if(count <= hiddenLayersConfig.length -1){
				int prevLayerIndex = count -1;
				sum += (hiddenLayersConfig[prevLayerIndex] * numNodesInLayer) + numNodesInLayer;
			}			
			count++;
		}

		sum +=  hiddenLayersConfig[count-1] +1;  // Assume only one output node (used only regression)

		return sum;
	}

	private void createNetwork(){

		network = new MultilayerNeuralNetwork(NeuralConnection.class);

		// Create the input nodes
		List<NeuralNode> inputNodes = new ArrayList<>();
		int classIndex = instances.getClassIndex();
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
		int numLayers = hiddenLayersConfig.length;
		List<NeuralNode> prevHiddenNodes = new ArrayList<>();
		List<List<NeuralNode>> hiddenLayers = new ArrayList<>(); 
		for(int i = 0; i < numLayers; i++){
			Integer numNodesInLayer = hiddenLayersConfig[i];
			List<NeuralNode> currHiddenNodes = new ArrayList<>();
			for(int j = 0; j < numNodesInLayer; j++){
				NeuralNode hiddenNode = new NeuralNode("hidden_"+(i+1)+"_"+(j+1), TYPE.HIDDEN, random);
				network.addVertex(hiddenNode);
				if(i == 0){
					// First hidden layer
					for(int k = 0; k < inputNodes.size(); k++) {
						//	double weight = Math.random();
						NeuralConnection connection = new NeuralConnection();
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
						NeuralConnection connection = new NeuralConnection();
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
							NeuralConnection connection = new NeuralConnection();
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
		instances.trainNeuralNetwork(network , learningRate ,momentum ,numEpoch, isStochastic);

	}
	
	public int getNumEpoch() {
		return numEpoch;
	}

	public void setNumEpoch(int numEpoch) {
		this.numEpoch = numEpoch;
	}
	@Override
	public Value classify(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}
}
