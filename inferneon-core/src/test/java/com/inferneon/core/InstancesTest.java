package com.inferneon.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.inferneon.core.utils.MathUtils;
import com.inferneon.supervised.SupervisedLearningTest;
public class InstancesTest extends SupervisedLearningTest{

	static {
		try {
			Class.forName("com.inferneon.core.Instances");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static final String ROOT = "/TestResources/Core";

	@Test 
	public void insertMissingNominalValuesWithModes() throws Exception{
		String inputFileName = "servoSomeMissing.arff";
		Instances instances = (Instances)  CommonTestUtils.createInstancesFromArffFile(ROOT, inputFileName);	
		Attribute motorAttribute = instances.getAttributeByName("motor");
		Attribute screwAttribute = instances.getAttributeByName("screw");
		Map<Attribute, Map<Value, Double>>  attributeAndValueCounts = instances.getFrequencyCounts().getAttributeValueCounts();
		Map<Value, Double> mtrvalueCounts = attributeAndValueCounts.get(motorAttribute);
		Map<Value, Double> scrvalueCounts = attributeAndValueCounts.get(screwAttribute);
		Map<String, Double> expectedValueNameAndCounts = new HashMap<>();
		expectedValueNameAndCounts.put("E", 8.0); expectedValueNameAndCounts.put("D", 6.0);
		expectedValueNameAndCounts.put("C", 10.0); expectedValueNameAndCounts.put("B", 10.0); expectedValueNameAndCounts.put("A", 5.0);
		checkValueCounts(mtrvalueCounts, expectedValueNameAndCounts);		
		expectedValueNameAndCounts.clear();
		expectedValueNameAndCounts.put("E", 8.0); expectedValueNameAndCounts.put("D", 9.0);
		expectedValueNameAndCounts.put("C", 12.0); expectedValueNameAndCounts.put("B", 7.0); expectedValueNameAndCounts.put("A", 7.0);
		checkValueCounts(scrvalueCounts, expectedValueNameAndCounts);	
		expectedValueNameAndCounts.clear();
				
		Instances modifiedInsts = (Instances) instances.insertMissingNominalValuesWithModes(null);
		
		expectedValueNameAndCounts.put("E", 8.0); expectedValueNameAndCounts.put("D", 6.0);
		expectedValueNameAndCounts.put("C", 10.0); expectedValueNameAndCounts.put("B", 21.0); expectedValueNameAndCounts.put("A", 5.0);
		attributeAndValueCounts = modifiedInsts.getFrequencyCounts().getAttributeValueCounts();
		Map<Value, Double> valueCounts = attributeAndValueCounts.get(instances.getAttributeByName("motor"));
		checkValueCounts(valueCounts, expectedValueNameAndCounts);
		expectedValueNameAndCounts.clear();
		expectedValueNameAndCounts.put("E", 8.0); expectedValueNameAndCounts.put("D", 9.0);
		expectedValueNameAndCounts.put("C", 19.0); expectedValueNameAndCounts.put("B", 7.0); expectedValueNameAndCounts.put("A", 7.0);
		valueCounts = attributeAndValueCounts.get(instances.getAttributeByName("screw"));
		checkValueCounts(valueCounts, expectedValueNameAndCounts);
	}
	
	private void checkValueCounts(Map<Value, Double> valueAndCounts, Map<String, Double> expectedValueNameAndCounts){
		Iterator<Entry<Value, Double>> iterator = valueAndCounts.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Value, Double> entry = iterator.next();
			Value value = entry.getKey();
			Double count =  MathUtils.roundDouble(entry.getValue(), 4) ;
			Double expectedCount =  MathUtils.roundDouble(expectedValueNameAndCounts.get(value.toString()), 4);
			Assert.assertTrue(Double.compare(count, expectedCount) == 0);
		}
	}
	
	@Test
	public void testSortedValuesOnTargetAverages() throws Exception {
		String inputFileName = "servo.arff";
		Instances instances = (Instances)  CommonTestUtils.createInstancesFromArffFile(ROOT, inputFileName);	
		List<Attribute> attributes = instances.getAttributes();
		Map<Attribute, List<Value>>  sortedValuesOfAttributesOnTargetAverages = instances.getSortedValuesOnTargetAverages();

		Assert.assertTrue(sortedValuesOfAttributesOnTargetAverages.size() == 2);

		for(int i = 0; i < attributes.size(); i++){
			Attribute attr = attributes.get(i);
			if(attr.getName().equals("motor")){
				String[] expectedValueNames = new String[]{"D", "E", "C", "B", "A"};
				List<Value> values = sortedValuesOfAttributesOnTargetAverages.get(attr);
				checkOrder(expectedValueNames, values);
			}
		}
	}
	
	@Test
	public void testSortedValuesOnTargetAveragesWithSomeMissingValues() throws Exception {
		String inputFileName = "servoSomeMissing.arff";
		Instances instances = (Instances)  CommonTestUtils.createInstancesFromArffFile(ROOT, inputFileName);	
		List<Attribute> attributes = instances.getAttributes();
		instances = (Instances)instances.insertMissingNominalValuesWithModes(null);
		
		Map<Attribute, List<Value>>  sortedValuesOfAttributesOnTargetAverages = instances.getSortedValuesOnTargetAverages();

		Assert.assertTrue(sortedValuesOfAttributesOnTargetAverages.size() == 2);

		for(int i = 0; i < attributes.size(); i++){
			Attribute attr = attributes.get(i);
			if(attr.getName().equals("motor")){
				String[] expectedValueNames = new String[]{"D", "E", "C", "B", "A"};
				List<Value> values = sortedValuesOfAttributesOnTargetAverages.get(attr);
				checkOrder(expectedValueNames, values);
			}
			if(attr.getName().equals("screw")){
				String[] expectedValueNames = new String[]{"D", "E", "C", "B", "A"};
				List<Value> values = sortedValuesOfAttributesOnTargetAverages.get(attr);
				checkOrder(expectedValueNames, values);
			}
		}
	}
	
	private void checkOrder(String[] expectedValueNames, List<Value> values){
		Assert.assertTrue(expectedValueNames.length == values.size());
		for(int j = 0; j < values.size(); j++){
			Assert.assertTrue(expectedValueNames[j].equals(values.get(j).toString()));
		}
	}
	
	@Test
	public void testConvertNominalToSyntheticBinaryAttributes() throws Exception{
		String inputFileName = "servoSomeMissing.arff";
		Instances instances = (Instances)  CommonTestUtils.createInstancesFromArffFile(ROOT, inputFileName);	
		instances = (Instances) instances.convertNominalToSyntheticBinaryAttributes();
		
		String outputFileName = "servoSomeMissingNomToBinaryOutput.arff";
		Instances expectedInstances = (Instances)  CommonTestUtils.createInstancesFromArffFile(ROOT, outputFileName);		
		Assert.assertTrue(instances.equals(expectedInstances));		
	}
}
