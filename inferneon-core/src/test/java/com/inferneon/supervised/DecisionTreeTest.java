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

	@Test
	public void testC45ManyMissingValuesAtRandom() throws Exception {

		String fileName = "C45ManyMissingValuesAtRandom.arff";

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
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("NO"));		
	}

	@Test
	public void testC45GainRatioManyMissingValuesAtRandom() throws Exception {

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
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("Yes"));	

		newValues = getValueListForTestInstance(attributes, "Overcast", "65", "95", "true");
		newInstance = new Instance(newValues);

		targetClassValue = dt.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));	

	}
	
	private void verifyTree(DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> decisionTree, DecisionTreeNode rootNode,
			String rootNodeName, Map<String, List<String>> parentAndOutgoingEdgeNames, Map<String, String> edgeAndTargetNode){

		Assert.assertTrue(rootNodeName.equals(rootNode.getAttribute().getName()));

		Set<Entry<String, List<String>>> entries = parentAndOutgoingEdgeNames.entrySet();
		Iterator<Entry<String, List<String> >> iterator = entries.iterator();
		while(iterator.hasNext()){
			Entry<String, List<String> > entry = iterator.next();
			String attrName = entry.getKey();
			List<String> outgoingEdgeNames = entry.getValue();

			DecisionTreeNode nodeInTree = getNodeByName(decisionTree, attrName);
			Set<DecisionTreeEdge>  outgoingEdgesOfNodeInTree = decisionTree.outgoingEdgesOf(nodeInTree);

			Assert.assertTrue(outgoingEdgesOfNodeInTree.size() == outgoingEdgeNames.size());

			Iterator<DecisionTreeEdge> edgesIterator = outgoingEdgesOfNodeInTree.iterator();
			while(edgesIterator.hasNext()){
				DecisionTreeEdge edge = edgesIterator.next();
				String edgeName = edge.toString();
				Assert.assertTrue(outgoingEdgeNames.contains(edgeName));

				String targetNodeName = decisionTree.getEdgeTarget(edge).getAttribute().getName();
				Assert.assertTrue(targetNodeName.equals(edgeAndTargetNode.get(edgeName)));				
			}
		}		
	}

	private DecisionTreeNode getNodeByName(DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> decisionTree, String name){
		Set<DecisionTreeNode> nodes = decisionTree.vertexSet();
		Iterator<DecisionTreeNode> iterator = nodes.iterator();
		while(iterator.hasNext()){
			DecisionTreeNode node = iterator.next();
			if(name.equals(node.getAttribute().getName())){
				return node;
			}
		}

		return null;
	}

	private DecisionTreeEdge getEdgeByName(DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> decisionTree, String name){
		Set<DecisionTreeEdge> edges = decisionTree.edgeSet();
		Iterator<DecisionTreeEdge> iterator = edges.iterator();
		while(iterator.hasNext()){
			DecisionTreeEdge edge = iterator.next();
			if(edge.toString().equals(name)){
				return edge;
			}
		}

		return null;
	}


	private Set<String> getEdgeNames(Set<DecisionTreeEdge>  edgesInTree){		
		Set<String> edgeNames = new HashSet<>();
		Iterator<DecisionTreeEdge> iterator = edgesInTree.iterator();
		while(iterator.hasNext()){
			DecisionTreeEdge edge = iterator.next();
			edgeNames.add(edge.toString());
		}
		return edgeNames;		
	}
}
