package com.ipsg.inferneon.supervised;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
import com.inferneon.supervised.decisiontree.DecisionTree;
import com.inferneon.supervised.decisiontree.DecisionTreeBuilder;
import com.inferneon.supervised.decisiontree.DecisionTreeBuilder.Criterion;
import com.inferneon.supervised.decisiontree.DecisionTreeBuilder.Method;
import com.inferneon.supervised.utils.DecisionTreeUtils;
import com.inferneon.supervised.utils.DescriptiveTree;

@Ignore
public class C45DecisionTreeTest extends SupervisedLearningTest{
	
	private static final String ROOT = "/TestResources";
	private static final String APP_TEMP_FOLDER = "Inferneon";

	static {
		try {
			Class.forName("com.ipsg.inferneon.spark.SparkInstances");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getAppTempDir(){

		String sysTempFolder = System.getProperty("java.io.tmpdir");
		String tempPath = sysTempFolder + (sysTempFolder.endsWith(File.separator)? "": File.separator) + APP_TEMP_FOLDER + File.separator;
		File tempDir = new File(tempPath);
		tempDir.mkdir();

		return tempPath;
	}

	private String getCreatedCSVFilePath(String arffFileName, String data){
		PrintWriter out = null;
		String csvFileName = null;
		String tempPath = getAppTempDir();
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
	
	@Test
	public void testC45GainRatioManyMissingValuesAtRandom() throws Exception {
		test("C45ManyMissingValuesAtRandom.arff", "C45ManyMissingValuesAtRandom.json");
	}

	private void test(String fileName, String jsonFormatFileExpected) throws Exception {

		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder(Method.C45, Criterion.GAIN_RATIO);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("SPARK", 
				attributes, attributes.size() -1, csvFilePath);

		dtBuilder.train(instances);			
		DecisionTree dt = (DecisionTree)dtBuilder.getDecisionTree();		
		DescriptiveTree expectedTree = DecisionTreeUtils.getDescriptiveTreeFromJSON(ROOT, jsonFormatFileExpected);
		System.out.println("********** EXPECTED  TREE:");
		expectedTree.emitTree();
		check(expectedTree, dt);
		
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
}
