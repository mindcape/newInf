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
	public void testID3Simple1() throws Exception {
		String fileName = "ID3Simple1.arff";

		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder();

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);
		dtBuilder.train(instances);

		DecisionTreeNode rootNode = dtBuilder.getRootNode();
		Assert.assertTrue(rootNode.toString().equals("Outlook"));

		checkClassification("No", attributes, dtBuilder, "Sunny", "Hot", "High", "Strong");
		checkClassification("No", attributes, dtBuilder, "Sunny", "Cool", "High", "Strong");
		checkClassification("No", attributes, dtBuilder, "Sunny", "Mild", "High", "Weak");
		checkClassification("Yes", attributes, dtBuilder, "Sunny", "Mild", "Normal", "Weak");
		checkClassification("Yes", attributes, dtBuilder, "Sunny", "Hot", "Normal", "Strong");
		checkClassification("Yes", attributes, dtBuilder, "Sunny", "Cool", "Normal", "Weak");

		checkClassification("Yes", attributes, dtBuilder, "Overcast", "Mild", "High", "Weak");
		checkClassification("Yes", attributes, dtBuilder, "Overcast", "Hot", "Normal", "Strong");
		checkClassification("Yes", attributes, dtBuilder, "Overcast", "Cool", "High", "Weak");
		checkClassification("Yes", attributes, dtBuilder, "Overcast", "Mild", "Normal", "Strong");

		checkClassification("Yes", attributes, dtBuilder, "Rain", "Mild", "High", "Weak");
		checkClassification("Yes", attributes, dtBuilder, "Rain", "Cool", "Normal", "Weak");
		checkClassification("Yes", attributes, dtBuilder, "Rain", "Hot", "High", "Weak");
		checkClassification("No", attributes, dtBuilder, "Rain", "Hot", "Normal", "Strong");
		checkClassification("No", attributes, dtBuilder, "Rain", "Cool", "High", "Strong");
		checkClassification("No", attributes, dtBuilder, "Rain", "Mild", "Normal", "Strong");

		//List<DecisionTreeNode> leafNodes = getLeafNodes(decisionTree);		
	}

	private void checkClassification(String target, List<Attribute> attributes, DecisionTreeBuilder dtBuilder,
			String ... values) {
		List<Value> newValues = getValueListForTestInstance(attributes, values);
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dtBuilder.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equals(target));				
	}

	@Test
	public void testID3Simple2() throws Exception {

		String fileName = "ID3Simple2.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder();

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);
		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Y", "N", "Y", "N", "None", "Mod", "N", "Y", "Thai", "0-10");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);

		Assert.assertTrue(targetClassValue.getName().equals("N"));		
	}	

	@Ignore
	@Test
	public void testC45NoMissingValues() throws Exception {
		String fileName = "C45NoMissingValues.arff";
		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);
		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);

		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));		
	}

	@Ignore
	@Test
	public void testC45OneMissingValue() throws Exception {

		String fileName = "C45OneMissingValue.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge>  decisionTree = dt.getDecisionTree();

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));		
	}

	@Ignore
	@Test
	public void testC45TwoMissingValuesOfDiffAttrsInDiffInstances() throws Exception {

		String fileName = "C45TwoMissingValuesOfDiffAttrsInDiffInstances.arff";
		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));
	}

	@Ignore
	@Test
	public void testC45ThreeMissingDiscreteValuesOfSameAttrsInDiffInstances() throws Exception {

		String fileName = "C45ThreeMissingDiscreteValuesOfSameAttrInDiffInstances.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));

	}

	@Ignore
	@Test
	public void testC45TwoMissingDiscreteValuesOfDiffAttrsInSameInstance() throws Exception {

		String fileName = "C45TwoMissingDiscreteValuesOfDiffAttrsInSameInstance.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));

	}

	@Ignore
	@Test
	public void testC45OneMissingContinuousValue() throws Exception {

		String fileName = "C45OneMissingContinuousValue.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));		
	}

	@Ignore
	@Test
	public void testC45OneMissingDiscreteAndOneMissingContinuousValueInSameInstance() throws Exception {

		String fileName = "C45OneMissingDiscreteAndOneMissingContinuousValueInSameInstance.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));		
	}

	@Ignore
	@Test
	public void testC45OneMissingDiscreteAndOneMissingContinuousValueInDiffInstances() throws Exception {

		String fileName = "C45OneMissingDiscreteAndOneMissingContinuousValueInDiffInstances.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));		
	}

	@Ignore
	@Test
	public void testC45TwoMissingContinuousValuesInSameInstance() throws Exception {

		String fileName = "C45TwoMissingContinuousValuesInSameInstance.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("Yes"));		
	}

	@Ignore
	@Test
	public void testC45TwoMissingContinuousValuesInDiffInstances() throws Exception {

		String fileName = "C45TwoMissingContinuousValuesInDiffInstances.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);
		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));		
	}

	@Ignore
	@Test
	public void testC45ManyMissingValuesAtRandom() throws Exception {

		String fileName = "C45ManyMissingValuesAtRandom.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45, Criterion.GAIN_RATIO);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("NO"));		
	}
	
	@Test
	public void testC45GainRatioManyMissingValuesAtRandom() throws Exception {

		String fileName = "C45ManyMissingValuesAtRandom.arff";
		String jsonFormatFile = "C45ManyMissingValuesAtRandom.json";

		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder(Method.C45, Criterion.GAIN_RATIO);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		dtBuilder.train(instances);

		dtBuilder.train(instances);			
		DecisionTree dt = (DecisionTree)dtBuilder.getDecisionTree();		
		DescriptiveTree expectedTree = DecisionTreeUtils.getDescriptiveTreeFromJSON(ROOT, jsonFormatFile);
		System.out.println("********** EXPECTED  TREE:");
		expectedTree.emitTree();
		check(expectedTree, dt);	

	}
}
