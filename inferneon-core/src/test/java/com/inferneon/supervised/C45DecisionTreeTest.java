package com.inferneon.supervised;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.supervised.DecisionTreeBuilder.Criterion;
import com.inferneon.supervised.DecisionTreeBuilder.Method;
import com.inferneon.supervised.utils.DecisionTreeUtils;
import com.inferneon.supervised.utils.DescriptiveTree;

public class C45DecisionTreeTest extends SupervisedLearningTest{

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
	public void noMissingValues() throws Exception {
		test("C45NoMissingValues.arff", "C45NoMissingValues.json", false, false);	
	}

	@Test
	public void oneMissingValue() throws Exception {
		test("C45OneMissingValue.arff", "C45OneMissingValue.json", false, false);		
	}
	
	@Test
	public void oneMissingContinuousValue() throws Exception {
		test("C45OneMissingContinuousValue.arff",  "C45OneMissingContinuousValue.json", false, false);			
	}

	@Test
	public void twoMissingContinuousValuesInSameInstance() throws Exception {
		test( "C45TwoMissingContinuousValuesInSameInstance.arff",  "C45TwoMissingContinuousValuesInSameInstance.json", false, false);
	}
	
	@Test
	public void oneMissingDiscreteAndOneMissingContinuousValueInSameInstance() throws Exception {
		test( "C45OneMissingDiscreteAndOneMissingContinuousValueInSameInstance.arff",  
				"C45OneMissingDiscreteAndOneMissingContinuousValueInSameInstance.json", false, false);
	}

	@Test
	public void twoMissingValuesOfDiffAttrsInDiffInstances() throws Exception {
		test( "C45TwoMissingValuesOfDiffAttrsInDiffInstances.arff",  
				"C45TwoMissingValuesOfDiffAttrsInDiffInstances.json", false, false);
	}

	@Test
	public void threeMissingDiscreteValuesOfSameAttrsInDiffInstances() throws Exception {
		test( "C45ThreeMissingDiscreteValuesOfSameAttrInDiffInstances.arff",  
				"C45ThreeMissingDiscreteValuesOfSameAttrInDiffInstances.json", false, false);
	}

	@Test
	public void twoMissingDiscreteValuesOfDiffAttrsInSameInstance() throws Exception {
		test( "C45TwoMissingDiscreteValuesOfDiffAttrsInSameInstance.arff",  
				"C45TwoMissingDiscreteValuesOfDiffAttrsInSameInstance.json", false, false);
	}
	
	@Test
	public void tneMissingDiscreteAndOneMissingContinuousValueInDiffInstances() throws Exception {
		test( "C45OneMissingDiscreteAndOneMissingContinuousValueInDiffInstances.arff",  
				"C45OneMissingDiscreteAndOneMissingContinuousValueInDiffInstances.json", false, false);	
	}

	@Test
	public void twoMissingContinuousValuesInDiffInstances() throws Exception {
		test( "C45TwoMissingContinuousValuesInDiffInstances.arff", "C45TwoMissingContinuousValuesInDiffInstances.json", false, false);	
	}

	@Test
	public void manyMissingValuesAtRandom() throws Exception {
		test("C45ManyMissingValuesAtRandom.arff", "C45ManyMissingValuesAtRandom.json", false, false);
	}
	
	@Test
	public void manyMissingValuesAtRandomCollapsed() throws Exception {
		test("C45ManyMissingValuesAtRandomCollapsed.arff", "C45ManyMissingValuesAtRandomCollapsed.json", true, false);
	}
	
	@Test
	public void manyMissingValuesAtRandomCollapsedAndPruned() throws Exception {
		test("C45ManyMissingValuesAtRandomCollapsedAndPruned.arff", "C45ManyMissingValuesAtRandomCollapsedAndPruned.json", true, true);
	}
		
	private void test(String inputFileName, String jsonFormatFileExpected,
			boolean collapseTree, boolean pruneTree) throws Exception{
		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder(Method.C45, Criterion.GAIN_RATIO, collapseTree, pruneTree);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, inputFileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(inputFileName, arffElements.getData(), APP_TEMP_FOLDER);
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dtBuilder.train(instances);
		DecisionTree dt = (DecisionTree)dtBuilder.getDecisionTree();		
		DescriptiveTree expectedTree = DecisionTreeUtils.getDescriptiveTreeFromJSON(ROOT, jsonFormatFileExpected);
		System.out.println("********** EXPECTED  TREE:");
		expectedTree.emitTree();
		check(expectedTree, dt);	
	}	
}
