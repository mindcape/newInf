package com.inferneon.supervised.neuralnetwork;

import org.jgraph.graph.DefaultEdge;

public class NeuralConnnection extends DefaultEdge{

	private double weight;
	public NeuralConnnection() {
		//this.weight = weight;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
}