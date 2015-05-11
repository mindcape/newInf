package com.inferneon.supervised;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.core.utils.DataLoader;
import com.inferneon.supervised.DecisionTreeBuilder.Method;

public class DecisionTreeTest extends SupervisedLearningTest{

	private static final String ROOT = "/TestResources/";
	@Ignore
	@Test
	public void testID3Simple1() throws Exception {
		String fileName = "PlayTennis.arff";
		DecisionTreeBuilder dt = new DecisionTreeBuilder();
		
		String filePath = ROOT + fileName;
		ArffElements arffElements = ParserUtils.getArffElements(filePath);		
		List<Attribute> attributes = arffElements.getAttributes();

		String data = arffElements.getData();
		System.out.println("Data : " + data);
		Instances instances = DataLoader.loadData(attributes, data, filePath);

		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "Cool", "High", "Strong");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);

		Assert.assertTrue(targetClassValue.getName().equals("No"));		
	}
	@Ignore
	@Test
	public void testID3Simple2() throws Exception {

		String fileName = "RestaurantVisit.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder();

		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
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
	public void testC45OnlyWithContinuousValuedAttrs() throws Exception {
		String trainingSamples = "/TestResources/PlayTennisContinuousAttrs.csv";

		List<String> nominalAttributeNames = new ArrayList<>();
		nominalAttributeNames.add("Outlook"); nominalAttributeNames.add("Windy"); nominalAttributeNames.add("PlayTennis");

		List<String> continuousValuedAttributeNames = new ArrayList<>();
		continuousValuedAttributeNames.add("Temperature");continuousValuedAttributeNames.add("Humidity"); 

		List<String> attrNominalValues = new ArrayList<>();
		attrNominalValues.add("Sunny"); attrNominalValues.add("Overcast"); attrNominalValues.add("Rainy");
		attrNominalValues.add("true"); attrNominalValues.add("false");
		attrNominalValues.add("yes"); attrNominalValues.add("no");

		int lengths[] = new int[3]; lengths[0] = 3; lengths[1] = 2; lengths[2] = 2;

		List<Attribute> nominalAttributesList = createAttributesWithNominalValues(nominalAttributeNames, lengths, attrNominalValues); 

		List<Attribute> continuousValuedAttributesList = createAttributesWithContinuousValues(Attribute.NumericType.INTEGER, continuousValuedAttributeNames);

		List<Attribute> attributes = new ArrayList<>(5);
		attributes.add(0, nominalAttributesList.get(0)); 
		attributes.add(1, continuousValuedAttributesList.get(0)); 
		attributes.add(2, continuousValuedAttributesList.get(1));
		attributes.add(3, nominalAttributesList.get(1));
		attributes.add(4, nominalAttributesList.get(2));

		Instances instances = DataLoader.loadData(attributes, trainingSamples);

		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder(Method.C45);
		dtBuilder.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dtBuilder.classify(newInstance);

		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));		
	}

	@Test
	public void testC45WithContinuousValuedAttrsAndOneMissingDiscreteValue() throws Exception {

		String trainingSamples = "/TestResources/PlayTennisContinuousAttrsAndOneMissingValue.csv";

		List<String> nominalAttributeNames = new ArrayList<>();
		nominalAttributeNames.add("Outlook"); nominalAttributeNames.add("Windy"); nominalAttributeNames.add("PlayTennis");

		List<String> continuousValuedAttributeNames = new ArrayList<>();
		continuousValuedAttributeNames.add("Temperature");continuousValuedAttributeNames.add("Humidity"); 

		List<String> attrNominalValues = new ArrayList<>();
		attrNominalValues.add("Sunny"); attrNominalValues.add("Overcast"); attrNominalValues.add("Rainy");
		attrNominalValues.add("true"); attrNominalValues.add("false");
		attrNominalValues.add("yes"); attrNominalValues.add("no");

		int lengths[] = new int[3]; lengths[0] = 3; lengths[1] = 2; lengths[2] = 2;

		List<Attribute> nominalAttributesList = createAttributesWithNominalValues(nominalAttributeNames, lengths, attrNominalValues); 

		List<Attribute> continuousValuedAttributesList = createAttributesWithContinuousValues(Attribute.NumericType.INTEGER, continuousValuedAttributeNames);

		List<Attribute> attributes = new ArrayList<>(5);
		attributes.add(0, nominalAttributesList.get(0)); 
		attributes.add(1, continuousValuedAttributesList.get(0)); 
		attributes.add(2, continuousValuedAttributesList.get(1));
		attributes.add(3, nominalAttributesList.get(1));
		attributes.add(4, nominalAttributesList.get(2));

		Instances instances = DataLoader.loadData(attributes, trainingSamples);

		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder(Method.C45);
		dtBuilder.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dtBuilder.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));		
	}
	
	@Test
	public void testC45TwoMissingDiscreteValuesOfDiffAttrsInDiffInstances() throws Exception {

		String trainingSamples = "/TestResources/PlayTennisContinuousAttrsAndTwoMissingDiscreteValuesInDiffInstances.csv";

		List<String> nominalAttributeNames = new ArrayList<>();
		nominalAttributeNames.add("Outlook"); nominalAttributeNames.add("Windy"); nominalAttributeNames.add("PlayTennis");

		List<String> continuousValuedAttributeNames = new ArrayList<>();
		continuousValuedAttributeNames.add("Temperature");continuousValuedAttributeNames.add("Humidity"); 

		List<String> attrNominalValues = new ArrayList<>();
		attrNominalValues.add("Sunny"); attrNominalValues.add("Overcast"); attrNominalValues.add("Rainy");
		attrNominalValues.add("true"); attrNominalValues.add("false");
		attrNominalValues.add("yes"); attrNominalValues.add("no");

		int lengths[] = new int[3]; lengths[0] = 3; lengths[1] = 2; lengths[2] = 2;

		List<Attribute> nominalAttributesList = createAttributesWithNominalValues(nominalAttributeNames, lengths, attrNominalValues); 

		List<Attribute> continuousValuedAttributesList = createAttributesWithContinuousValues(Attribute.NumericType.INTEGER, continuousValuedAttributeNames);

		List<Attribute> attributes = new ArrayList<>(5);
		attributes.add(0, nominalAttributesList.get(0)); 
		attributes.add(1, continuousValuedAttributesList.get(0)); 
		attributes.add(2, continuousValuedAttributesList.get(1));
		attributes.add(3, nominalAttributesList.get(1));
		attributes.add(4, nominalAttributesList.get(2));

		Instances instances = DataLoader.loadData(attributes, trainingSamples);

		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder(Method.C45);
		dtBuilder.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dtBuilder.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));
	}

	@Test
	public void testC45TwoMissingDiscreteValuesOfSameAttrsInDiffInstances() throws Exception {

		String trainingSamples = "/TestResources/ThreeMissingDiscreteValuesOfSameAttrInDiffInstances .csv";

		List<String> nominalAttributeNames = new ArrayList<>();
		nominalAttributeNames.add("Outlook"); nominalAttributeNames.add("Windy"); nominalAttributeNames.add("PlayTennis");

		List<String> continuousValuedAttributeNames = new ArrayList<>();
		continuousValuedAttributeNames.add("Temperature");continuousValuedAttributeNames.add("Humidity"); 

		List<String> attrNominalValues = new ArrayList<>();
		attrNominalValues.add("Sunny"); attrNominalValues.add("Overcast"); attrNominalValues.add("Rainy");
		attrNominalValues.add("true"); attrNominalValues.add("false");
		attrNominalValues.add("yes"); attrNominalValues.add("no");

		int lengths[] = new int[3]; lengths[0] = 3; lengths[1] = 2; lengths[2] = 2;

		List<Attribute> nominalAttributesList = createAttributesWithNominalValues(nominalAttributeNames, lengths, attrNominalValues); 

		List<Attribute> continuousValuedAttributesList = createAttributesWithContinuousValues(Attribute.NumericType.INTEGER, continuousValuedAttributeNames);

		List<Attribute> attributes = new ArrayList<>(5);
		attributes.add(0, nominalAttributesList.get(0)); 
		attributes.add(1, continuousValuedAttributesList.get(0)); 
		attributes.add(2, continuousValuedAttributesList.get(1));
		attributes.add(3, nominalAttributesList.get(1));
		attributes.add(4, nominalAttributesList.get(2));

		Instances instances = DataLoader.loadData(attributes, trainingSamples);

		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder(Method.C45);
		dtBuilder.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "65", "90", "true");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dtBuilder.classify(newInstance);
		Assert.assertTrue(targetClassValue.getName().equalsIgnoreCase("No"));
		
		
//		String rootNodeName = "";
//		Map<String, List<String>> parentAndOutgoingEdgeNames = new HashMap();
//		List<String> outgoingEdgeNames1 = new ArrayList<>(); 
//		outgoingEdgeNames1.add("Sunny"); outgoingEdgeNames1.add("Overcast"); outgoingEdgeNames1.add("Windy");
//		parentAndOutgoingEdgeNames.put("Outlook", outgoingEdgeNames1);
//		
//		Map<String, String> edgeAndTargetNode = new HashMap<>();
		
		//verifyTree(dtBuilder.getDecisionTree(), dtBuilder.getRootNode());

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
	
//	private DecisionTreeEdge getEdgeByName(DirectedAcyclicGraph<DecisionTreeNode, DecisionTreeEdge> decisionTree, String name){
//		Set<DecisionTreeEdge> edges = decisionTree.edgeSet();
//		Iterator<DecisionTreeEdge> iterator = edges.iterator();
//		while(iterator.hasNext()){
//			DecisionTreeEdge edge = iterator.next();
//			if(edge.toString().equals(name)){
//				return edge;
//			}
//		}
//		
//		return null;
//	}
//	
//	
//	private Set<String> getEdgeNames(Set<DecisionTreeEdge>  edgesInTree){		
//		Set<String> edgeNames = new HashSet<>();
//		Iterator<DecisionTreeEdge> iterator = edgesInTree.iterator();
//		while(iterator.hasNext()){
//			DecisionTreeEdge edge = iterator.next();
//			edgeNames.add(edge.toString());
//		}
//		return edgeNames;		
//	}
}
