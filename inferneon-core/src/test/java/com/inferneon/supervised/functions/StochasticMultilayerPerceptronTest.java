package com.inferneon.supervised.functions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.supervised.SupervisedLearningTest;

public class StochasticMultilayerPerceptronTest  extends SupervisedLearningTest{
/* testcases for multilayer perceptron */
	private static final String ROOT = "/TestResources";
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
	public void createNetwork() throws Exception {
		test("newMLPData5-07092015.arff");	
	}
		
	private void test(String inputFileName) throws Exception{

		StochasticMultilayerPerceptron smlp = new StochasticMultilayerPerceptron("2, 2", 0.3);
		ArffElements arffElements = ParserUtils.getArffElements(ROOT, inputFileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(inputFileName, arffElements.getData(), APP_TEMP_FOLDER);
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		smlp.learn(instances);
//		MultilayerNeuralNetwork network = mlp.getNetwork();
//		System.out.println("********** TREE:");
//		network.emitTree();
	}	
}
