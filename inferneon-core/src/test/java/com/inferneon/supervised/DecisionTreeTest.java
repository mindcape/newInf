package com.inferneon.supervised;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.junit.Assert;
import org.junit.Test;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.DecisionTreeBuilder.Method;

public class DecisionTreeTest extends SupervisedLearningTest{

	private static final String ROOT = "/TestResources";
	
	@Test
	public void testID3Simple1() throws Exception {
		String fileName = "ID3Simple1.arff";
		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder();

		System.out.println("Current path = " + new File(".").getAbsolutePath());
		
		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);

		dtBuilder.train(instances);
		
		DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> decisionTree = dtBuilder.getDecisionTree();
		DecisionTreeNode rootNode = dtBuilder.getRootNode();
		Assert.assertTrue(rootNode.toString().equals("Outlook"));

//		@attribute Outlook 			{Sunny, Overcast, Rain}
//		@attribute Temperature	{Hot, Mild, Cool}
//		@attribute Humidity		{High, Normal}
//		@attribute Wind	{Strong, Weak}
//		@attribute PlayTennis	{Yes, No}
//		
//		Outlook:
//			(Rain) -> Wind
//			(Sunny) -> Humidity
//			(Overcast) -> Yes(Yes: 4.0)
//		Wind:
//			(Strong) -> No(No: 2.0)
//			(Weak) -> Yes(Yes: 3.0)
//		Humidity:
//			(Normal) -> Yes(Yes: 2.0)
//			(High) -> No(No: 3.0)

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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
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
		
		String data = arffElements.getData();
		Instances instances = DataLoader.loadData(attributes, data, fileName);
		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);
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
