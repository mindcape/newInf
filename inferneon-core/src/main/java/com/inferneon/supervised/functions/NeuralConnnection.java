package com.inferneon.supervised.functions;

import org.jgraph.graph.DefaultEdge;

public class NeuralConnnection extends DefaultEdge{

	private double weight;
	public NeuralConnnection(double weight) {
		this.weight = weight;
	}
	public double getWeight() {
		return weight;
	}
}
