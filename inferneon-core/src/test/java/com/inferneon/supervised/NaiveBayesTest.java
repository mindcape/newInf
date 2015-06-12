package com.inferneon.supervised;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.Value;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.core.utils.DataLoader;

public class NaiveBayesTest  extends SupervisedLearningTest  {
	
	private static final String ROOT = "/TestResources";
	
	@Ignore	
	@Test
	public void testSimpleNaiveBayes() throws Exception {
		
		NaiveBayes nb = new NaiveBayes();
		
		String fileName = "PlayTennis.csv";
		System.out.println("Current path = " + new File(".").getAbsolutePath());
		
		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, fileName);
		
		nb.train(instances);
		
		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "Cool", "High", "Strong");
		Instance newInstance = new Instance(newValues);
		
		Value targetClassValue = nb.classify(newInstance);
		
		Assert.assertTrue(targetClassValue.getName().equals("No"));		
	}
}
