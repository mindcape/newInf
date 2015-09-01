package com.inferneon.supervised;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.Value;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.supervised.DecisionTreeBuilder.Criterion;
import com.inferneon.supervised.DecisionTreeBuilder.Method;
import com.inferneon.supervised.utils.DecisionTreeUtils;
import com.inferneon.supervised.utils.DescriptiveTree;

public class DecisionTreeTest extends SupervisedLearningTest{
/*
	private static final String ROOT = "/TestResources";
	private static final String APP_TEMP_FOLDER = "Inferneon";

	static {
		try {
			Class.forName("com.inferneon.core.Instances");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getCreatedCSVFilePath(String arffFileName, String data){
		PrintWriter out = null;
		String csvFileName = null;
		String tempPath = getAppTempDir(APP_TEMP_FOLDER);
		try{
			String fileNameWithouExt = arffFileName.substring(0, arffFileName.lastIndexOf(".arff"));
			csvFileName = tempPath + fileNameWithouExt + ".csv";			
			out = new PrintWriter(csvFileName);
			out.print(data);
		}
		catch(Exception e){
			e.printStackTrace();
			return csvFileName;
		}
		finally{
			out.close();
		}

		return csvFileName;
	}

	@After
	public void tearDown(){
		File tempPath = new File(getAppTempDir());
		try {
			FileUtils.cleanDirectory(tempPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	@Test
	public void testC45NoMissingValues() throws Exception {
		test("C45NoMissingValues.arff", "C45NoMissingValues.json");	
	}

	@Test
	public void testC45OneMissingValue() throws Exception {
		test("C45OneMissingValue.arff", "C45OneMissingValue.json");		
	}
	
	@Test
	public void testC45OneMissingContinuousValue() throws Exception {
		test("C45OneMissingContinuousValue.arff",  "C45OneMissingContinuousValue.json");			
	}

	@Test
	public void testC45TwoMissingContinuousValuesInSameInstance() throws Exception {
		test( "C45TwoMissingContinuousValuesInSameInstance.arff",  "C45TwoMissingContinuousValuesInSameInstance.json");
	}
	
	@Test
	public void testC45OneMissingDiscreteAndOneMissingContinuousValueInSameInstance() throws Exception {
		test( "C45OneMissingDiscreteAndOneMissingContinuousValueInSameInstance.arff",  
				"C45OneMissingDiscreteAndOneMissingContinuousValueInSameInstance.json");
	}

	@Test
	public void testC45TwoMissingValuesOfDiffAttrsInDiffInstances() throws Exception {
		test( "C45TwoMissingValuesOfDiffAttrsInDiffInstances.arff",  
				"C45TwoMissingValuesOfDiffAttrsInDiffInstances.json");
	}

	@Test
	public void testC45ThreeMissingDiscreteValuesOfSameAttrsInDiffInstances() throws Exception {
		test( "C45ThreeMissingDiscreteValuesOfSameAttrInDiffInstances.arff",  
				"C45ThreeMissingDiscreteValuesOfSameAttrInDiffInstances.json");
	}

	@Test
	public void testC45TwoMissingDiscreteValuesOfDiffAttrsInSameInstance() throws Exception {
		test( "C45TwoMissingDiscreteValuesOfDiffAttrsInSameInstance.arff",  
				"C45TwoMissingDiscreteValuesOfDiffAttrsInSameInstance.json");
	}
	
	@Test
	public void testC45OneMissingDiscreteAndOneMissingContinuousValueInDiffInstances() throws Exception {
		test( "C45OneMissingDiscreteAndOneMissingContinuousValueInDiffInstances.arff",  
				"C45OneMissingDiscreteAndOneMissingContinuousValueInDiffInstances.json");	
	}

	@Test
	public void testC45TwoMissingContinuousValuesInDiffInstances() throws Exception {
		test( "C45TwoMissingContinuousValuesInDiffInstances.arff", "C45TwoMissingContinuousValuesInDiffInstances.json");	
	}


	@Test
	public void testC45ManyMissingValuesAtRandom() throws Exception {
		test("C45ManyMissingValuesAtRandom.arff", "C45ManyMissingValuesAtRandom.json");
	}
	
	private void test(String inputFileName, String jsonFormatFileExpected) throws Exception{
		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder(Method.C45, Criterion.GAIN_RATIO);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, inputFileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(inputFileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dtBuilder.train(instances);

		dtBuilder.train(instances);			
		DecisionTree dt = (DecisionTree)dtBuilder.getDecisionTree();		
		DescriptiveTree expectedTree = DecisionTreeUtils.getDescriptiveTreeFromJSON(ROOT, jsonFormatFileExpected);
		System.out.println("********** EXPECTED  TREE:");
		expectedTree.emitTree();
		check(expectedTree, dt);	
	}
	*/
}
