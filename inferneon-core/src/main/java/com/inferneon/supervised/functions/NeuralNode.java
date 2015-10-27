package com.inferneon.supervised.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.inferneon.core.Attribute;

public class NeuralNode {
	public enum TYPE {INPUT,OUTPUT,HIDDEN}; 
	
	private double output;
	private double error;
	private List<Double> weights;
	private TYPE type;
	private String name;
	private Attribute attr;
	private List<Double> changeInweights; 
//	public NeutralNode(double output,
//	double error,
//	double[] weights,
//	TYPE type,
//	String name) {
//		this.output = output;
//		private double error;
//		private double[] weights;
//		private TYPE type;
//		private String name;
//	}

	public NeuralNode(String name, TYPE type, Random r) {
		this.name = name;
		this.type = type;
	    this.weights = new ArrayList<Double>();
	    this.changeInweights = new ArrayList<Double>();
	 //   this.weights[0] = r.nextDouble() * .1 - .05;
//	    this.weights[0] = getSomeHardCodeWeight(name);
	    this.changeInweights.add(0d);
		this.weights.add(getSomeHardCodeWeight(name));
	}

	public static double getSomeHardCodeWeight(String id) {
		if(id.contains("rtt")){
			return 0.03456;
		}
		else if(id.contains("ql")){
			return 0.07456;
		}
		else if(id.contains("ttr")){
			return 0.09877;
		}
		else if(id.equals("0")){
			return 0.09877;
		}
		else if(id.equals("hidden_1_1")){
			return 0.07123;
		}
		else if(id.equals("hidden_1_2")){
			return 0.89123;
		}
		else if(id.equals("hidden_2_1")){
			return 0.03423;
		}
		else if(id.equals("hidden_2_2")){
			return 0.07323;
		}
		else {
			return 0.35834;
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
	
	public List<Double> getWeights() {
		return weights;
	}
	public void setWeights(List<Double> weights) {
		this.weights = weights;
	}
	public Attribute getAttr() {
		return attr;
	}

	public void setAttr(Attribute attr) {
		this.attr = attr;
	}

	public List<Double> getChangeInweights() {
		return changeInweights;
	}

	public void setChangeInweights(List<Double> changeInweights) {
		this.changeInweights = changeInweights;
	}
	
}
