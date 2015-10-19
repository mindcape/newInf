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
	 //   this.weights[0] = r.nextDouble() * .1 - .05;
	    this.weights[0] = getSomeHardCodeWeight(name);
	}

	private double getSomeHardCodeWeight(String id) {
		if(id.contains("haproxy.roundtrip")){
			return 0.03456;
		}
		else if(id.contains("haproxy.queuelength")){
			return 0.03456;
		}
		else if(id.contains("apache.Activity_Total_Traffic")){
			return 0.03456;
		}
		else if(id.equals("0")){
			return 0.0987;
		}
		else if(id.equals("1")){
			return 0.00123;
		}
		else if(id.equals("2")){
			return 0.00123;
		}
		else if(id.equals("3")){
			return 0.00123;
		}
		else if(id.equals("4")){
			return 0.00123;
		}
		else {
			return 0.034;
		}
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
	
	public double[] getWeights() {
		return weights;
	}

	
}
