package com.inferneon.supervised;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Assert;

import com.inferneon.core.Attribute;
import com.inferneon.core.Value;
import com.inferneon.supervised.decisiontree.DecisionTree;
import com.inferneon.supervised.decisiontree.DecisionTreeEdge;
import com.inferneon.supervised.decisiontree.DecisionTreeNode;
import com.inferneon.supervised.utils.DescriptiveTree;
import com.inferneon.supervised.utils.DescriptiveTreeEdge;
import com.inferneon.supervised.utils.DescriptiveTreeNode;

public class SupervisedLearningTest {

	protected String getCreatedCSVFilePath(String arffFileName, String data, String appTempFolder){
		PrintWriter out = null;
		String csvFileName = null;
		String tempPath = getAppTempDir(appTempFolder);
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

	protected String getAppTempDir(String appTempFolder){
		
		String sysTempFolder = System.getProperty("java.io.tmpdir");
		String tempPath = sysTempFolder + (sysTempFolder.endsWith(File.separator)? "": File.separator) + appTempFolder + File.separator;
		File tempDir = new File(tempPath);
		tempDir.mkdir();
		
		return tempPath;
	}
	
	
	protected List<Attribute> createAttributesWithNominalValues(
			List<String> attrNames, int[] lengths,
			List<String> attrNominalValues) {

		List<Attribute> attributes = new ArrayList<>();
		int count = 0, startIndex = 0;
		for(String attrName : attrNames){

			List<String> namesForAttr = attrNominalValues.subList(startIndex, startIndex + lengths[count]);

			startIndex = startIndex + lengths[count];

			Attribute attr = new Attribute(attrName, namesForAttr);
			attributes.add(attr);

			count++;

		}		
		return attributes;		
	}

	protected List<Attribute> createAttributesWithContinuousValues(Attribute.NumericType numericType, 
			List<String> continuousValuedAttributeNames){
		List<Attribute> attributes = new ArrayList<>();

		for(String attrName : continuousValuedAttributeNames){
			Attribute attr = new Attribute(attrName, numericType);
			attributes.add(attr);
		}

		return attributes;
	}

	protected List<Value> getValueListForTestInstance(List<Attribute> attributes, String ... strVals) {
		int numAttrs = attributes.size();

		List<Value> vals = new ArrayList<>(numAttrs);

		for(int i = 0 ; i < numAttrs -1; i++){
			Attribute attr = attributes.get(i);
			Value value = null;
			if(attr.getType() == Attribute.Type.NOMINAL){
				value =  attr.getNominalValueForName(strVals[i]);
			}
			else{
				try{		
					Long longVal = Long.valueOf(strVals[i]);
					value = new Value(longVal);
				}
				catch(NumberFormatException nfe1){
					try{						
						Double doubleValue = Double.valueOf(strVals[i]);
						value = new Value(doubleValue);		
					}
					catch(NumberFormatException nfe2){
						// TODO Log warning here.
					}
				}
			}
			vals.add(value);
		}

		vals.add(null);		
		return vals;		
	}	
	
	protected void check(DescriptiveTree expectedTree, DecisionTree dt) {		
		DescriptiveTreeNode rootNameExpected = expectedTree.getRootNode();
		DecisionTreeNode dtRootNode = dt.getDecisionTreeRootNode();
		check(expectedTree, rootNameExpected, dt, dtRootNode);		
	}
	
	private void check(DescriptiveTree expectedTree, DescriptiveTreeNode expectedNode,
			DecisionTree dt, DecisionTreeNode dtNode) {
		Assert.assertTrue(checkLeafNodeName(dt, expectedTree, dtNode, expectedNode));
		
		if(dtNode.isLeaf()){
			Assert.assertTrue(expectedNode.isLeaf());
			String nodeLeafDist = dtNode.getFrequencyCounts().getDistrbutionDesc();
			String expectedNodeLeafDist = expectedNode.getLeafDistribution();
			Assert.assertTrue(expectedNodeLeafDist.equals(nodeLeafDist));
			return;
		}
		
		Set<DecisionTreeEdge> outgoingEdges = dt.outgoingEdgesOf(dtNode);
		Set<DescriptiveTreeEdge> expectedOutgoingEdges = expectedTree.outgoingEdgesOf(expectedNode);		
		Assert.assertTrue(outgoingEdges.size() == expectedOutgoingEdges.size());
		
		Iterator<DecisionTreeEdge> outgoingEdgeIterator = outgoingEdges.iterator();
		
		List<DecisionTreeNode> targetNodes = new ArrayList<DecisionTreeNode>();
		List<DescriptiveTreeNode> expectedTargetNodes = new ArrayList<DescriptiveTreeNode>();
		
		while(outgoingEdgeIterator.hasNext()){
			DecisionTreeEdge edge = outgoingEdgeIterator.next();
			String edgeName = edge.toString();
			DecisionTreeNode targetNode = dt.getEdgeTarget(edge);
			targetNodes.add(targetNode);
			
			DescriptiveTreeNode expectedTargetNode = getExpectedTargetNode(expectedTree, expectedNode, edgeName);
			Assert.assertNotNull(expectedTargetNode);				
			expectedTargetNodes.add(expectedTargetNode);			
		}
		
		int numTargets = targetNodes.size();
		for(int i = 0; i < numTargets; i++){
			DecisionTreeNode targetNode = targetNodes.get(i);
			DescriptiveTreeNode expectedTargetNode = expectedTargetNodes.get(i);
			
			check(expectedTree, expectedTargetNode, dt, targetNode);			
		}		
	}

	private boolean checkLeafNodeName(DecisionTree dt, DescriptiveTree expectedTree, 
			DecisionTreeNode dtNode, DescriptiveTreeNode expectedNode) {
		if(dtNode.toString().equals(expectedNode.getName())){
			return true;
		}

		FrequencyCounts frequencyCounts = dtNode.getFrequencyCounts();

		if(Double.compare(frequencyCounts.getSumOfWeights(), 0.0) == 0){
			// Does not matter what the leaf is
			return true;
		}

		Map<Value, Double> targetCounts = frequencyCounts.getTotalTargetCounts();
		Set<Entry<Value, Double>> entries = targetCounts.entrySet();
		Iterator<Entry<Value, Double>> iterator = entries.iterator();
		
		boolean firstNumFound = false;
		Double referenceValue = null;
		boolean allCountsEqual = true;
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Double numInstances = entry.getValue();
			
			if(!firstNumFound){
				firstNumFound = true;
				referenceValue = numInstances;
				continue;
			}

			if(Double.compare(referenceValue, numInstances) != 0){
				allCountsEqual = false;
				break;
			}
		}
		
		if(allCountsEqual){
			// Does not matter
			return true;
		}
		
		return false;
		
	}

	private DescriptiveTreeNode getExpectedTargetNode(DescriptiveTree expectedTree, DescriptiveTreeNode nameExpected,
			String edgeName) {
		DescriptiveTreeNode targetExpectedNode = null;
		
		Set<DescriptiveTreeEdge> edges = expectedTree.outgoingEdgesOf(nameExpected);
		Iterator<DescriptiveTreeEdge> iterator = edges.iterator();
		while(iterator.hasNext()){
			DescriptiveTreeEdge edge = iterator.next();
			if(edgeNamesAreSimilar(edge.getName(), edgeName)){
				return expectedTree.getEdgeTarget(edge);
			}
			
		}
		
		return targetExpectedNode;
	}

	private boolean edgeNamesAreSimilar(String name1, String name2) {
		if(name1.equals(name2)){
			return true;
		}
		
		boolean foundNumberedEdge = false;
		if(name1.startsWith("<= ") && name2.startsWith("<= ")){
			name1 = name1.substring(3, name1.length());
			name2 = name2.substring(3, name2.length());
			foundNumberedEdge = true;
		}
		
		if(name1.startsWith("> ") && name2.startsWith("> ")){
			name1 = name1.substring(2, name1.length());
			name2 = name2.substring(2, name2.length());
			foundNumberedEdge = true;
		}
		
		if(!foundNumberedEdge){
			return false;
		}
		
		boolean isName1Int = false;
		boolean isName2Int = false;
		boolean isName1Dbl = false;
		boolean isName2Dbl = false;
		
		try{
			Integer intName1 = Integer.parseInt(name1);
			isName1Int = true;
		}
		catch(NumberFormatException nfe){
			try{
				Double dblName1 = Double.parseDouble(name1);
				isName1Dbl = true;
			}
			catch(NumberFormatException nfe1){}		
		}
		
		try{
			Integer intName2 = Integer.parseInt(name2);
			isName2Int = true;
		}
		catch(NumberFormatException nfe){
			try{
				Double dblName2 = Double.parseDouble(name2);
				isName2Dbl = true;
			}
			catch(NumberFormatException nfe1){}		
		}
		
		if(isName1Int && isName2Dbl){
			Integer intName1 = Integer.parseInt(name1);
			Double dblName2 = Double.parseDouble(name2);
			String dblName2Str = dblName2.toString();
			if(dblName2Str.endsWith(".0")){
				double name2Val = dblName2.doubleValue();
				if(intName1.intValue() == (int)name2Val){
					return true;
				}
			}			
		}
		
		if(isName2Int && isName1Dbl){
			Integer intName2 = Integer.parseInt(name2);
			Double dblName1 = Double.parseDouble(name1);
			String dblName1Str = dblName1.toString();
			if(dblName1Str.endsWith(".0")){
				double name1Val = dblName1.doubleValue();
				if(intName2.intValue() == (int)name1Val){
					return true;
				}
			}			
		}		
		return false;		
	}	
}
