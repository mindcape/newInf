package com.inferneon.supervised;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;

import com.inferneon.core.Attribute;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.utils.DataLoader;

public class NaiveBayesTest  extends SupervisedLearningTest  {
	
	@Test
	public void testSimpleNaiveBayes() throws Exception {
		
		NaiveBayes nb = new NaiveBayes();
		
		List<String> attrNames = new ArrayList<>();
		attrNames.add("Outlook"); attrNames.add("Temperature");attrNames.add("Humidity"); attrNames.add("Wind"); attrNames.add("PlayTennis");
		
		List<String> attrNominalValues = new ArrayList<>();
		attrNominalValues.add("Sunny"); attrNominalValues.add("Overcast"); attrNominalValues.add("Rain");
		attrNominalValues.add("Hot"); attrNominalValues.add("Mild"); attrNominalValues.add("Cool");
		attrNominalValues.add("High"); attrNominalValues.add("Normal");
		attrNominalValues.add("Strong"); attrNominalValues.add("Weak");
		attrNominalValues.add("Yes"); attrNominalValues.add("No");
				
		
		int lengths[] = new int[5]; lengths[0] = 3; lengths[1] = 3; lengths[2] = 2; lengths[3] = 2; lengths[4] = 2;
		
		List<Attribute> attributes = createAttributesWithNominalValues(attrNames, lengths, attrNominalValues); 
		
		Instances instances = DataLoader.loadData(attributes, "TestResources/PlayTennis.csv");
		
		nb.train(instances);
		
		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "Cool", "High", "Strong");
		Instance newInstance = new Instance(newValues);
		
		Value targetClassValue = nb.classify(newInstance);
		
		Assert.assertTrue(targetClassValue.getName().equals("No"));		
		
	}
}
