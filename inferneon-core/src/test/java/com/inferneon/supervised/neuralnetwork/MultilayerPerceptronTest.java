package com.inferneon.supervised.neuralnetwork;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.supervised.SupervisedLearningTest;
import com.inferneon.supervised.neuralnetwork.MultiLayerPerceptron;

public class MultilayerPerceptronTest  extends SupervisedLearningTest{
	
	private static final String ROOT = "/TestResources/MLP";
	private static final String APP_TEMP_FOLDER = "Inferneon";

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testNumWeightsComputation() {
		Integer [] hiddenNodeNConfig = new Integer[] {};
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(hiddenNodeNConfig, 0.3, false);
		try{
			int numWts = mlp.getNumWeights(3, hiddenNodeNConfig);
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
		test("newMLPData5-07092015.arff");	
	}

	private void test(String inputFileName) throws Exception{

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, inputFileName);		
		List<Attribute> attributes = arffElements.getAttributes();	
		Integer [] hiddenNodeNConfig = new Integer[] {2, 2};
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(hiddenNodeNConfig, 0.3, false);

		String csvFilePath = getCreatedCSVFilePath(inputFileName, arffElements.getData(), APP_TEMP_FOLDER);
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		mlp.train(instances);
		//		MultilayerNeuralNetwork network = mlp.getNetwork();
		//		System.out.println("********** TREE:");
		//		network.emitTree();
	}	
}
