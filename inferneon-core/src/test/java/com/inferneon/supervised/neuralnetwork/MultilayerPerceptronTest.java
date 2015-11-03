package com.inferneon.supervised.neuralnetwork;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import com.inferneon.core.Attribute;
import com.inferneon.core.CommonTestUtils;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.supervised.SupervisedLearningTest;

public class MultilayerPerceptronTest  extends SupervisedLearningTest{

	private static final String ROOT = "/TestResources/MLP";
	private static final String APP_TEMP_FOLDER = "Inferneon";
	private MultiLayerPerceptron mlp ;
	
	static {
		try {
			Class.forName("com.inferneon.core.Instances");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown(){
		File tempPath = new File(getAppTempDir(APP_TEMP_FOLDER));
		try {
			FileUtils.cleanDirectory(tempPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNumWeightsComputation() {
		Integer [] hiddenNodeNConfig = new Integer[] {};
		MultiLayerPerceptron mlp = new MultiLayerPerceptron();
		mlp.setHiddenLayersConfig(hiddenNodeNConfig);
		mlp.setLearningRate(0.3);
		mlp.setStochastic(false);
		mlp.setMomentum(1);
		try{
			mlp.getNumWeights(3, hiddenNodeNConfig);
		}
		catch(IllegalArgumentException e){
			Assert.assertTrue(e.getMessage().equals("MLP must have at least one hidden layer."));
		}

		try{
			hiddenNodeNConfig = new Integer[] {-1};
			mlp.getNumWeights(3, hiddenNodeNConfig);
		}
		catch(IllegalArgumentException e){
			Assert.assertTrue(e.getMessage().equals("A hidden layer must have at least one node."));
		}

		hiddenNodeNConfig = new Integer[] {1};
		int numWts = mlp.getNumWeights(3, hiddenNodeNConfig);
		Assert.assertTrue(numWts == 5);

		hiddenNodeNConfig = new Integer[] {2, 2};
		numWts = mlp.getNumWeights(3, hiddenNodeNConfig);
		Assert.assertTrue(numWts == 15);

		hiddenNodeNConfig = new Integer[] {2, 3, 5};
		numWts = mlp.getNumWeights(3, hiddenNodeNConfig);
		Assert.assertTrue(numWts == 41);

		hiddenNodeNConfig = new Integer[] {2, 3, 5};
		numWts = mlp.getNumWeights(4, hiddenNodeNConfig);
		Assert.assertTrue(numWts == 43);
	}

	@Test
	public void createNetwork() throws Exception {
		testNetwork("testing.arff");	
	}
	
	private MultilayerNeuralNetwork testNetwork(String inputFileName) throws Exception{

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, inputFileName);		
		List<Attribute> attributes = arffElements.getAttributes();	
		Integer [] hiddenNodeNConfig = new Integer[] {2, 2};
		String csvFilePath = getCreatedCSVFilePath(inputFileName, arffElements.getData(), APP_TEMP_FOLDER);
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);
		
		mlp = new MultiLayerPerceptron();
		mlp.setHiddenLayersConfig(hiddenNodeNConfig);
		mlp.setLearningRate(0.3);
		mlp.setStochastic(false);
		mlp.setMomentum(1);
		mlp.setNumEpoch(2);
		mlp.setInstances(instances);
		mlp.setAttributes(attributes);
		mlp.createNetwork();
		return mlp.getNetwork();
	}	

	@Test
	public void testCalculateOutput() throws Exception{

		
		MultilayerNeuralNetwork network = testNetwork("test2.arff");
		List<NeuralNode> inputNodes = network.getInputNodes();
		List<NeuralNode> outputNodes = network.getOutputNodes();
		List<List<NeuralNode>> hiddenLayers = network.getHiddenLayers();
	
		Instances instances = (Instances)  CommonTestUtils.createInstancesFromArffFile(ROOT, "test2.arff");
		List<Instance> insts= instances.getInstances();
//		for(int ins=0; ins<insts.size(); ins++){
			Instance instance = insts.get(0);
		network.calculateOutputs(instance, inputNodes, hiddenLayers, outputNodes);
//		}
		for(int inp = 0; inp < inputNodes.size(); inp++) {
			double output = inputNodes.get(inp).getOutput();

			if(inp == 0){
				Assert.assertTrue(output == 118);
			}
			else{
				Assert.assertTrue(output == 1);
			}
		}
		for(int i = 0; i < hiddenLayers.size(); i++) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){

				NeuralNode hiddenNode = hiddenNodes.get(j);
				double output = hiddenNode.getOutput();
				if(i == 0 && j == 0 ){
					Assert.assertTrue(output == 0.9998060274669902);
				}
				else if(i == 0 && j == 1 ){
					Assert.assertTrue(output == 1.0);
				}
				else if(i == 1 && j == 0 ){
					Assert.assertTrue(output == 0.5256483079985875);
				}
				else{
					Assert.assertTrue(output == 0.5546991553952902);
				}
			}
		}
		NeuralNode outputNode = outputNodes.get(0);
		double output = outputNode.getOutput();
		Assert.assertTrue(output == 0.20547591895941328);


	}

	@Test
	public void testCalculateErrors() throws Exception { 

		MultilayerNeuralNetwork network = testNetwork("test2.arff");
		List<NeuralNode> outputNodes = network.getOutputNodes();
		List<List<NeuralNode>> hiddenLayers = network.getHiddenLayers();
		List<NeuralNode> inputNodes = network.getInputNodes();	
		Instances instances = (Instances)  CommonTestUtils.createInstancesFromArffFile(ROOT, "test2.arff");
		List<Instance> insts= instances.getInstances();
//		for(int ins=0; ins<insts.size(); ins++){
			Instance instance = insts.get(0);
			network.calculateOutputs(instance, inputNodes, hiddenLayers, outputNodes);
		network.calculateError(instance, hiddenLayers, outputNodes, instances.getAttributes());
		for(int i = 0; i < hiddenLayers.size(); i++) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){

				NeuralNode hiddenNode = hiddenNodes.get(j);
				double output = hiddenNode.getError();
				if(i == 0 && j == 0 ){
					Assert.assertTrue(output == 2.1721908244683876E-5);
					}
				else if(i == 0 && j == 1 ){
					Assert.assertTrue(output == 0.0);
				}
				else if(i == 1 && j == 0 ){
					Assert.assertTrue(output == 1.0489977308454055);
				}
				else{
					Assert.assertTrue(output == 1.0391777690227053);
				}
			}
		}
		NeuralNode outputNode = outputNodes.get(0);
		double error = outputNode.getError();
		Assert.assertTrue(error == 42.59452408104058);

	}

	@Test
	public void testUpdateWeight() throws Exception {
		
		MultilayerNeuralNetwork network = testNetwork("test2.arff");
		final List<List<NeuralNode>> hiddenLayers = network.getHiddenLayers();
		final List<NeuralNode> outputNodes = network.getOutputNodes();

		for(int op = 0; op < outputNodes.size(); op++ ) {
			NeuralNode outputNode = outputNodes.get(op);		
			//Set<NeuralConnnection> outincommingEdges = network.incomingEdgesOf(outputNode);
			//Iterator<NeuralConnnection> connectionIterator = outincommingEdges.iterator();
			List<Double> weights = outputNode.getWeights();	
			int b=0;
			for(double seeWeight : weights){				
				System.out.println("weight of in comming edges at output node " + b + " " + seeWeight);
				//Assert.assertTrue(seeWeight == 12.778357224312174);
				b++;
			}

		}

		for(int i = hiddenLayers.size() - 1 ; i >= 0 ; i--) {
			List<NeuralNode> hiddenNodes = hiddenLayers.get(i);
			for( int j = 0; j < hiddenNodes.size(); j++){
				NeuralNode hiddenNode = hiddenNodes.get(j);
				//Set<NeuralConnnection> hidenincommingEdges = network.incomingEdgesOf(hiddenNode);
				//Iterator<NeuralConnnection> connectionIterator = hidenincommingEdges.iterator();
				List<Double> weights = hiddenNode.getWeights();
				int a=0;
				for(double seeWeight : weights){				
					System.out.println("weight of in comming edges at hiden node " + j + " " + a + " " + seeWeight);
					//Assert.assertTrue(seeWeight == 12.778357224312174);
					a++;
				}
			}
			
		}



	}
}



