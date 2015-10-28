package com.inferneon.supervised.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
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
	
	public void calculateOutputs(Instance instance,  List<NeuralNode> inputNodes,
			List<List<NeuralNode>> hiddenLayers, List<NeuralNode> outputNodes) {

		for(int inp = 0; inp < inputNodes.size(); inp++) {
			double dblValue = instance.getValue(inp).getNumericValueAsDouble();
			inputNodes.get(inp).setOutput(dblValue);
		}

		for(int i = 0; i < hiddenLayers.size(); i++) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){
				//double totalOutput = 0d;
				NeuralNode hiddenNode = hiddenNodes.get(j);
				Set<NeuralConnnection> inputConnections = incomingEdgesOf(hiddenNode);
				Iterator<NeuralConnnection> connectionIterator = inputConnections.iterator();
				int c = 0;
				List<Double> weights = hiddenNode.getWeights();
				double value = weights.get(0);
				while (connectionIterator.hasNext()){
					NeuralConnnection connection = connectionIterator.next();
					NeuralNode sourceNode = (NeuralNode) connection.getSource();
					value += sourceNode.getOutput()*weights.get(c+1);
					c++;
				} 
				//sigmoid function
				value = convertToSigmoid(value);
				hiddenNode.setOutput(value);
			}
		}

		for(int op = 0; op < outputNodes.size(); op++) {
			NeuralNode outputNode = outputNodes.get(op);
			Set<NeuralConnnection> inputConnections = incomingEdgesOf(outputNode);
			Iterator<NeuralConnnection> connectionIterator = inputConnections.iterator();
			List<Double> weights = outputNode.getWeights();
			double value = weights.get(0);
			for (int c = 0; connectionIterator.hasNext(); c++){
				NeuralConnnection connection = connectionIterator.next();
				NeuralNode sourceNode = (NeuralNode) connection.getSource();
				value += sourceNode.getOutput()*weights.get(c+1);
			}
			outputNode.setOutput(value);
		}
		for(int inp = 0; inp < inputNodes.size(); inp++) {
			NeuralNode inpuNode = inputNodes.get(inp);
			System.out.println("input "+inp+": "+inpuNode.getOutput());
		}
		for(int i = 0; i < hiddenLayers.size(); i++) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){

				NeuralNode hiddenNode = hiddenNodes.get(j);
				System.out.println(""+hiddenNode.getName()+": "+hiddenNode.getOutput());
			}
		}
		for(int op = 0; op < outputNodes.size(); op++) {
			NeuralNode outputNode = outputNodes.get(op);
			System.out.println("output "+op+": "+outputNode.getOutput());
		}
	}

	public void calculateError(Instance instance,  List<List<NeuralNode>> hiddenLayers, 
			List<NeuralNode> outputNodes, List<Attribute> attributes) {
		double error = 0d;
		// double totalError = 0d;
		for(int op = 0; op < outputNodes.size(); op++ ) {
			NeuralNode outputNode = outputNodes.get(op);
			for(int k = 0; k < attributes.size(); k++){
				if(attributes.get(k).getName().equals(outputNode.getAttr().getName())) {
					double calculatedOutput = outputNode.getOutput();
					double expectedValue = instance.getValue(k).getNumericValueAsDouble();
					error = expectedValue - calculatedOutput;
					// TODO check this later
					//	totalError = calculatedOutput*(1-calculatedOutput) * (error);
				}
			}
			outputNode.setError(error);
		}

		for(int i = hiddenLayers.size() - 1 ; i >= 0 ; i--) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){
				//double totalOutput = 0d;
				NeuralNode hiddenNode = hiddenNodes.get(j);
				double errorConnection = 0;
				Set<NeuralConnnection> outputConnections = outgoingEdgesOf(hiddenNode);
				Iterator<NeuralConnnection> connectionIterator = outputConnections.iterator();
				int c = 0;

				while (connectionIterator.hasNext()){

					NeuralConnnection connection = connectionIterator.next();
					NeuralNode targetNode = (NeuralNode) connection.getTarget();
					List<Double> weights = targetNode.getWeights();
					errorConnection+= targetNode.getError()*weights.get(c+1);
					c++;
				}
				error =  hiddenNode.getOutput()*( 1 - hiddenNode.getOutput() )*(errorConnection);
				hiddenNode.setError(error);
			}
		}
		for(int i = 0; i < hiddenLayers.size(); i++) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){

				NeuralNode hiddenNode = hiddenNodes.get(j);
				System.out.println("error"+hiddenNode.getName()+": "+hiddenNode.getError());
			}
		}
		for(int op = 0; op < outputNodes.size(); op++) {
			NeuralNode outputNode = outputNodes.get(op);
			System.out.println("outputerror "+op+": "+outputNode.getError());
		}

	}

	public void updateWeight(List<List<NeuralNode>> hiddenLayers, List<NeuralNode> outputNodes, double learningRate,
			boolean isStochastic) {
		if(isStochastic){
			stochastiUpdateWeight(hiddenLayers, outputNodes, learningRate);
			return;
		}
		
		// Batch gradient descent weight update
		for(int op = 0; op < outputNodes.size(); op++ ) {
			NeuralNode outputNode = outputNodes.get(op);
			Set<NeuralConnnection> inutConnections = incomingEdgesOf(outputNode);
			Iterator<NeuralConnnection> connectionIterator = inutConnections.iterator();
			List<Double> weights = outputNode.getWeights();
			List<Double> cweights = outputNode.getChangeInweights();
			List<Double> tempWeights = new ArrayList<>();
			List<Double> tempCWeights = new ArrayList<>();
			for (int c = 0; connectionIterator.hasNext(); c++){
				NeuralConnnection connection = connectionIterator.next();
				NeuralNode sourceNode = (NeuralNode) connection.getSource();
				if(c==0){
					double learnTimesError = learningRate * outputNode.getError();
					double weightChange = cweights.get(c)+ (learnTimesError);
					tempWeights.add(weights.get(c) + weightChange);
					tempCWeights.add(weightChange);
				}
				double weightChange = cweights.get(c+1)+ (sourceNode.getOutput()*(learningRate * outputNode.getError()));
				tempWeights.add(weights.get(c+1) + weightChange);
				tempCWeights.add(weightChange);
			}
			outputNode.setWeights(tempWeights);
			outputNode.setChangeInweights(tempCWeights);
		}
		
		for(int i = hiddenLayers.size() - 1 ; i >= 0 ; i--) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){
				//double totalOutput = 0d;
				NeuralNode hiddenNode = hiddenNodes.get(j);
				Set<NeuralConnnection> inputConnections = incomingEdgesOf(hiddenNode);
				Iterator<NeuralConnnection> connectionIterator = inputConnections.iterator();
				int c = 0;
				List<Double> weights = hiddenNode.getWeights();
				List<Double> cweights = hiddenNode.getChangeInweights();
				List<Double> tempWeights = new ArrayList<>();
				List<Double> tempCWeights = new ArrayList<>();
				while (connectionIterator.hasNext()){

					NeuralConnnection connection = connectionIterator.next();
					NeuralNode sourceNode = (NeuralNode) connection.getSource();
					//					   double learnTimesError = 0;
					//					    learnTimesError = learningRate * hiddenNode.getError();
					//					double count = learnTimesError + momentum * cWeights[0];
					//***weights.set(c+1)***=weights.get(c+1) + ( sourceNode.getOutput()*hiddenNode.getError()*sourceNode.getLearningRate() );
					if(c==0){
						double weightChange = cweights.get(c)+ (learningRate * hiddenNode.getError());
						tempWeights.add(weights.get(c) + weightChange);
						tempCWeights.add(weightChange);
					}
					double weightChange = cweights.get(c+1)+ (sourceNode.getOutput()*(learningRate * hiddenNode.getError()));
					tempWeights.add(weights.get(c+1) + weightChange);
					tempCWeights.add(weightChange);
					c++;
				}
				hiddenNode.setWeights(tempWeights);
				hiddenNode.setChangeInweights(tempCWeights);
			}
		}

		for(int i = 0; i < hiddenLayers.size(); i++) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){

				NeuralNode hiddenNode = hiddenNodes.get(j);
				System.out.println(""+hiddenNode.getName()+": "+hiddenNode.getWeights());
				System.out.println("change"+hiddenNode.getName()+": "+hiddenNode.getChangeInweights());
			}
		}
		for(int op = 0; op < outputNodes.size(); op++) {
			NeuralNode outputNode = outputNodes.get(op);
			System.out.println("output "+op+": "+outputNode.getWeights());
			System.out.println("outputchange "+op+": "+outputNode.getChangeInweights());
		}

	}

	private void stochastiUpdateWeight(List<List<NeuralNode>> hiddenLayers, List<NeuralNode> outputNodes, double learningRate) {
		
		for(int op = 0; op < outputNodes.size(); op++ ) {
			NeuralNode outputNode = outputNodes.get(op);
			Set<NeuralConnnection> inutConnections = incomingEdgesOf(outputNode);
			Iterator<NeuralConnnection> connectionIterator = inutConnections.iterator();
			List<Double> weights = outputNode.getWeights();
			//List<Double> cweights = outputNode.getChangeInweights();
			List<Double> tempWeights = new ArrayList<>();
			List<Double> tempCWeights = new ArrayList<>();
			for (int c = 0; connectionIterator.hasNext(); c++){
				NeuralConnnection connection = connectionIterator.next();
				NeuralNode sourceNode = (NeuralNode) connection.getSource();
				if(c==0){
					double learnTimesError = learningRate * outputNode.getError();
					double weightChange =learnTimesError;
					tempWeights.add(weights.get(c) + weightChange);
					//double weightChange = cweights.get(c)+ (learnTimesError);
					tempCWeights.add(weightChange);
				}
				double weightChange = (sourceNode.getOutput()*(learningRate * outputNode.getError()));
				tempWeights.add(weights.get(c+1) + weightChange);
				tempCWeights.add(weightChange);
			}
			outputNode.setWeights(tempWeights);
			outputNode.setChangeInweights(tempCWeights);
		}
		for(int i = hiddenLayers.size() - 1 ; i >= 0 ; i--) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){
				//double totalOutput = 0d;
				NeuralNode hiddenNode = hiddenNodes.get(j);
				Set<NeuralConnnection> inputConnections = incomingEdgesOf(hiddenNode);
				Iterator<NeuralConnnection> connectionIterator = inputConnections.iterator();
				int c = 0;
				List<Double> weights = hiddenNode.getWeights();
				//List<Double> cweights = hiddenNode.getChangeInweights();
				List<Double> tempWeights = new ArrayList<>();
				List<Double> tempCWeights = new ArrayList<>();
				while (connectionIterator.hasNext()){

					NeuralConnnection connection = connectionIterator.next();
					NeuralNode sourceNode = (NeuralNode) connection.getSource();
					//					   double learnTimesError = 0;
					//					    learnTimesError = learningRate * hiddenNode.getError();
					//					double count = learnTimesError + momentum * cWeights[0];
					//***weights.set(c+1)***=weights.get(c+1) + ( sourceNode.getOutput()*hiddenNode.getError()*sourceNode.getLearningRate() );
					if(c==0){
						double weightChange = learningRate * hiddenNode.getError();
						tempWeights.add(weights.get(c) + weightChange);
						tempCWeights.add(weightChange);
					}
					double weightChange = (sourceNode.getOutput()*(learningRate * hiddenNode.getError()));
					tempWeights.add(weights.get(c+1) + weightChange);
					tempCWeights.add(weightChange);
					c++;
				}
				hiddenNode.setWeights(tempWeights);
				hiddenNode.setChangeInweights(tempCWeights);
			}
		}

		for(int i = 0; i < hiddenLayers.size(); i++) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){

				NeuralNode hiddenNode = hiddenNodes.get(j);
				System.out.println(""+hiddenNode.getName()+": "+hiddenNode.getWeights());
				System.out.println("change"+hiddenNode.getName()+": "+hiddenNode.getChangeInweights());
			}
		}
		for(int op = 0; op < outputNodes.size(); op++) {
			NeuralNode outputNode = outputNodes.get(op);
			System.out.println("output "+op+": "+outputNode.getWeights());
			System.out.println("outputchange "+op+": "+outputNode.getChangeInweights());
		}

	}

	private double convertToSigmoid(double value) {
		if (value < -45) {
			value = 0;
		}
		else if (value > 45) {
			value = 1;
		}
		else {
			value = 1 / (1 + Math.exp(-value));
		}  
		return value;
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
