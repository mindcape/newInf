package com.inferneon.supervised.decisiontree;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.Value;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.supervised.SupervisedLearningTest;
import com.inferneon.supervised.decisiontree.DecisionTreeBuilder;
import com.inferneon.supervised.decisiontree.DecisionTreeNode;

public class ID3DecisionTreeTest extends SupervisedLearningTest{
	
	private static final String ROOT = "/TestResources/ID3";
	private static final String APP_TEMP_FOLDER = "Inferneon";
	
	static {
		try {
			Class.forName("com.inferneon.core.Instances");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testID3Simple1() throws Exception {
		String fileName = "ID3Simple1.arff";

		DecisionTreeBuilder dtBuilder = new DecisionTreeBuilder();

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData(), APP_TEMP_FOLDER);
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
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData(), APP_TEMP_FOLDER);
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);
		dt.train(instances);

		List<Value> newValues = getValueListForTestInstance(attributes, "Y", "N", "Y", "N", "None", "Mod", "N", "Y", "Thai", "0-10");
		Instance newInstance = new Instance(newValues);

		Value targetClassValue = dt.classify(newInstance);

		Assert.assertTrue(targetClassValue.getName().equals("N"));		
	}	

}
