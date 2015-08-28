package com.inferneon.supervised.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.supervised.DecisionTree;
import com.inferneon.supervised.DecisionTreeEdge;
import com.inferneon.supervised.DecisionTreeNode;

public final class DecisionTreeUtils {

	public static void emitDecisionTreeJSON(String path, String fileName, DecisionTree decisionTree) throws JSONException, FileNotFoundException{

		DecisionTreeNode rootNode = decisionTree.getDecisionTreeRootNode();

		JSONArray finalArray = new JSONArray();
		buildJSON(decisionTree, rootNode, null, finalArray);

		String finalJsonStr = finalArray.toString();
		String fullPath = path + "/" + fileName;		
		File file = new File(fullPath);

		PrintWriter out = new PrintWriter(file);
		out.print(finalJsonStr.toCharArray());
		out.close();

		System.out.println(finalJsonStr);
	}

	private static void buildJSON(DecisionTree decisionTree, DecisionTreeNode node, String incomingEdgeName, JSONArray finalArray) throws JSONException {

		JSONObject jsonObject = createJSONObjectFromNode(node, incomingEdgeName);		
		Set<DecisionTreeEdge> outgoingEdges = decisionTree.outgoingEdgesOf(node);
		Iterator<DecisionTreeEdge> iterator = outgoingEdges.iterator();		
		List<DecisionTreeNode> childNodes = new ArrayList<>();
		List<String> edgeNames = new ArrayList<>();
		JSONArray childJsonsArray = new JSONArray();
		while(iterator.hasNext()){
			DecisionTreeEdge edge = iterator.next();
			String edgeName = edge.toString();			
			DecisionTreeNode childNode = decisionTree.getEdgeTarget(edge);	
			childNodes.add(childNode);
			edgeNames.add(edgeName);
			JSONObject childJson = new JSONObject();
			childJson.put("edgeName", edgeName);
			childJson.put("id", childNode.hashCode());
			childJsonsArray.put(childJson);
		}

		jsonObject.put("children", childJsonsArray);
		finalArray.put(jsonObject);

		int numChildren = outgoingEdges.size();
		for(int i = 0; i < numChildren; i++){
			DecisionTreeNode childNode = childNodes.get(i);
			String edgeName = edgeNames.get(i);			
			buildJSON(decisionTree, childNode, edgeName, finalArray);			
		}		
	}

	private static JSONObject createJSONObjectFromNode(DecisionTreeNode node, String incomingEdge) throws JSONException{
		JSONObject jsonNode = new JSONObject();
		jsonNode.put("id", node.hashCode());
		jsonNode.put("name", node.toString());
		if(incomingEdge != null){
			jsonNode.put("incomingEdge", incomingEdge);
		}		
		if(node.isLeaf()){
			jsonNode.put("leafDist", node.getFrequencyCounts().getDistrbutionDesc());
		}
		return jsonNode;
	}

	public static DescriptiveTree getDescriptiveTreeFromJSON(String rootPath, String filePath) throws 
	JSONException, CycleFoundException, IOException, java.text.ParseException, URISyntaxException{

		String fullPath = rootPath + "/" + filePath;
		URL url = DecisionTreeUtils.class.getResource(fullPath);

		JSONArray allNodes =  (JSONArray) readJSonFromFile(url.toURI());
		JSONObject rootJSonNode = allNodes.getJSONObject(0);
		DescriptiveTree descriptiveTree = new DescriptiveTree();

		buildDescriptiveTree(descriptiveTree, rootJSonNode, null, null, allNodes);
		return descriptiveTree;
	}

	private static JSONArray readJSonFromFile(URI file) throws IOException, JSONException{
		byte[] encoded = Files.readAllBytes(Paths.get(file));
		String contents = new String(encoded, "UTF-8");
		
		JSONArray jsonArray = new JSONArray(contents);
		return jsonArray;

	}

	private static void buildDescriptiveTree(DescriptiveTree descriptiveTree, JSONObject jsonNode,
			DescriptiveTreeNode parent, String incomingEdgeName, JSONArray allNodes) throws JSONException,
			CycleFoundException {
		String nodeName = (String) jsonNode.get("name");
		DescriptiveTreeNode descriptiveTreeNode = new DescriptiveTreeNode(nodeName);
		if(parent == null){ // Root node
			descriptiveTree.setRootNode(descriptiveTreeNode);
		}

		String leafDist = null;
		boolean isLeaf = false;
		try{
			leafDist = (String)jsonNode.get("leafDist");
			descriptiveTreeNode.setLeafDistribution(leafDist);
			isLeaf = true;
		}
		catch(JSONException jsonException){ //Leaf node
		}
		
		descriptiveTree.addVertex(descriptiveTreeNode);

		if(parent != null){
			descriptiveTree.addDagEdge(parent, descriptiveTreeNode, new DescriptiveTreeEdge(incomingEdgeName));
		}

		if(isLeaf){
			return;
		}
		
		List<JSONObject> childJsonNodes = new ArrayList<>();
		List<String> edgeNames = new ArrayList<>();
		JSONArray childArray = (JSONArray) jsonNode.get("children");
		int numChildren = childArray.length();
		for(int i = 0; i < numChildren; i++){
			JSONObject childJson = (JSONObject) childArray.get(i);
			Integer childId = (Integer) childJson.get("id");
			JSONObject childJsonObj = getJSONObjectFromID(childId, allNodes);
			String edgeToChildName = (String) childJson.get("edgeName");
			childJsonNodes.add(childJsonObj);
			edgeNames.add(edgeToChildName);
		}		

		for(int i = 0; i < numChildren; i++){
			String edgeToChildName = edgeNames.get(i);
			JSONObject childJsonObj = childJsonNodes.get(i);			
			buildDescriptiveTree(descriptiveTree, childJsonObj, descriptiveTreeNode, edgeToChildName, allNodes);			
		}		
	}

	private static JSONObject getJSONObjectFromID(Integer refId, JSONArray allNodes) throws JSONException {		
		JSONObject jsonObject = null;		
		int numNodes = allNodes.length();
		for(int i = 0; i < numNodes; i++){
			JSONObject obj = (JSONObject) allNodes.get(i);
			Integer id = (Integer) obj.get("id");
			if(id.equals(refId)){
				return obj;
			}
		}

		return jsonObject;
	}	
}
