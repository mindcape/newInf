package com.inferneon.supervised.functions;

import java.util.List;
import java.util.Random;

public class NeuralNode {
	public enum TYPE {INPUT,OUTPUT,HIDDEN}; 
	
	private double output;
	private double error;
	private double[] weights;
	private TYPE type;
	private String name;

	public NeuralNode(String name, TYPE type, Random r) {
		this.name = name;
		this.type = type;
	    this.weights = new double[1];
	    this.weights[0] = r.nextDouble() * .1 - .05;
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
