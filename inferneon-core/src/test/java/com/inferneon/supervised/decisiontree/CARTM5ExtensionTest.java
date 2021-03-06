package com.inferneon.supervised.decisiontree;

import org.junit.After;
import org.junit.Test;

import com.inferneon.core.CommonTestUtils;
import com.inferneon.core.IInstances;
import com.inferneon.supervised.SupervisedLearningTest;

public class CARTM5ExtensionTest extends SupervisedLearningTest{

	private static final String ROOT = "/TestResources/CARTM5";

	static {
		try {
			Class.forName("com.inferneon.core.Instances");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown(){
		CommonTestUtils.clearAppTempFolder();
	}
	
	@Test
	public void test1() throws Exception {
		//test("Sample1.arff", "servoSomeMissing.json", false, false);	
		test("ADCSDDC.arff", "servoSomeMissing.json", false, false);
	}
	
	private void test(String inputFileName, String jsonFormatFileExpected,
			boolean collapseTree, boolean pruneTree) throws Exception{
		CARTM5Extension cartM5 = new CARTM5Extension();

		IInstances instances = CommonTestUtils.createInstancesFromArffFile(ROOT, inputFileName);

		cartM5.train(instances);
//		DecisionTree dt = (DecisionTree)dtBuilder.getDecisionTree();		
//		DescriptiveTree expectedTree = DecisionTreeUtils.getDescriptiveTreeFromJSON(ROOT, jsonFormatFileExpected);
//		System.out.println("********** EXPECTED  TREE:");
//		expectedTree.emitTree();
//		check(expectedTree, dt);	
	}	
}
