package com.inferneon.supervised.functions;

import java.util.List;

public class NeuralNode {
	public enum TYPE {INPUT,OUTPUT,HIDDEN}; 
	private List<NeuralNode> inputNodes;
	private List<NeuralNode> outputNodes;
	
	private double output;
	private double error;
	
	private TYPE type;
	private String name;

	public NeuralNode(String name, TYPE type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}
	
	public TYPE getType() {
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NeuralNode> getOutputNodes() {
		return outputNodes;
	}

	public void setOutputNodes(List<NeuralNode> outputNodes) {
		this.outputNodes = outputNodes;
	}

	public List<NeuralNode> getInputNodes() {
		return inputNodes;
	}

	public void setInputNodes(List<NeuralNode> inputNodes) {
		this.inputNodes = inputNodes;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public double getOutput() {
		return output;
	}

	public void setOutput(double output) {
		this.output = output;
	}
	
}
