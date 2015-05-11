package com.inferneon.core.arffparser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.inferneon.core.Attribute;
import com.inferneon.core.Value;

public class ArffParserTests {

	private static final String ROOT = "ArffParserTestResources";

	@Test
	public void testNominalValuedAttributes(){
		String fileName = "AllNominalValues.arff";
		
		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		List<String> attributeNames = new ArrayList<>();
		attributeNames.add("Outlook"); attributeNames.add("Temperature"); attributeNames.add("Humidity"); 
		attributeNames.add("Wind");  attributeNames.add("PlayTennis"); 

		String [] attr1Values = new String[]{"Sunny", "Overcast", "Rain"};
		String [] attr2Values = new String[]{"Hot", "Mild", "Cool"};
		String [] attr3Values = new String[]{"High", "Normal"};
		String [] attr4Values = new String[]{"Strong", "Weak"};
		String [] attr5Values = new String[]{"Yes", "No"};
		List<String[]> attrValueNames = new ArrayList<>();
		attrValueNames.add(attr1Values);attrValueNames.add(attr2Values);attrValueNames.add(attr3Values);
		attrValueNames.add(attr4Values);attrValueNames.add(attr5Values);

		List<Attribute.Type> types = new ArrayList<>();
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);

		checkAttributes(attributes, attributeNames, attrValueNames, types);
		
		System.out.println("Data:");
		System.out.println(arffElements.getData());		
		System.out.println("Data start line:" + arffElements.getDataStartLine());
		
		Assert.assertTrue(arffElements.getRelationName().equals( "play-tennis"));		
		Assert.assertTrue(arffElements.getDataStartLine() == 10);
	}
	
	@Test
	public void testNewLinesCommentsAndDataStartingLine(){
		String fileName = "NewLinesCommentsAndDataStartingLine.arff";
		
		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		List<String> attributeNames = new ArrayList<>();
		attributeNames.add("Outlook"); attributeNames.add("Temperature"); attributeNames.add("Humidity"); 
		attributeNames.add("Wind");  attributeNames.add("PlayTennis"); 

		String [] attr1Values = new String[]{"Sunny", "Overcast", "Rain"};
		String [] attr2Values = new String[]{"Hot", "Mild", "Cool"};
		String [] attr3Values = new String[]{"High", "Normal"};
		String [] attr4Values = new String[]{"Strong", "Weak"};
		String [] attr5Values = new String[]{"Yes", "No"};
		List<String[]> attrValueNames = new ArrayList<>();
		attrValueNames.add(attr1Values);attrValueNames.add(attr2Values);attrValueNames.add(attr3Values);
		attrValueNames.add(attr4Values);attrValueNames.add(attr5Values);

		List<Attribute.Type> types = new ArrayList<>();
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);

		checkAttributes(attributes, attributeNames, attrValueNames, types);
		
		Assert.assertTrue(arffElements.getRelationName().equals( "play-tennis"));
			
		System.out.println("Data:");
		System.out.println(arffElements.getData());		
		System.out.println("Data start line:" + arffElements.getDataStartLine());
		
		Assert.assertTrue(arffElements.getDataStartLine() == 28);
	}
	
	@Test
	public void testNominalValuedAttributesNamesInQuotes(){
		String fileName = "AllNominalValuesNamesInQuotes.arff";
		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		List<String> attributeNames = new ArrayList<>();
		attributeNames.add("Outlook"); attributeNames.add("Temperature"); attributeNames.add("Humidity"); 
		attributeNames.add("Wind");  attributeNames.add("PlayTennis"); 

		String [] attr1Values = new String[]{"Sunny", "Overcast", "Rain"};
		String [] attr2Values = new String[]{"Hot", "Mild", "Cool"};
		String [] attr3Values = new String[]{"High", "Normal"};
		String [] attr4Values = new String[]{"Strong", "Weak"};
		String [] attr5Values = new String[]{"Yes", "No"};
		List<String[]> attrValueNames = new ArrayList<>();
		attrValueNames.add(attr1Values);attrValueNames.add(attr2Values);attrValueNames.add(attr3Values);
		attrValueNames.add(attr4Values);attrValueNames.add(attr5Values);

		List<Attribute.Type> types = new ArrayList<>();
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);

		checkAttributes(attributes, attributeNames, attrValueNames, types);
		Assert.assertTrue(arffElements.getRelationName().equals( "play-tennis"));
		Assert.assertTrue(arffElements.getDataStartLine() == 10);
	}

	@Test
	public void testAllContinuousValuedAttributes(){
		String fileName = "AllContinuousValuedAttributes.arff";
		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		List<String> attributeNames = new ArrayList<>();
		attributeNames.add("MYCT"); attributeNames.add("MMIN"); attributeNames.add("MMAX"); 
		attributeNames.add("CACH");  attributeNames.add("CHMIN"); attributeNames.add("CHMAX");  attributeNames.add("class"); 

		List<Attribute.Type> types = new ArrayList<>();
		types.add(Attribute.Type.NUMERIC); types.add(Attribute.Type.NUMERIC); types.add(Attribute.Type.NUMERIC);
		types.add(Attribute.Type.NUMERIC); types.add(Attribute.Type.NUMERIC); types.add(Attribute.Type.NUMERIC);
		types.add(Attribute.Type.NUMERIC);

		List<String[]> attrValueNames = new ArrayList<>();
		attrValueNames.add(null); attrValueNames.add(null); attrValueNames.add(null); attrValueNames.add(null);
		attrValueNames.add(null); attrValueNames.add(null); attrValueNames.add(null);

		checkAttributes(attributes, attributeNames, attrValueNames, types);
		Assert.assertTrue(arffElements.getRelationName().equals("cpu"));
		Assert.assertTrue(arffElements.getDataStartLine() == 17);
	}

	@Test
	public void testBothNominalAndContinuousAttrs(){
		String fileName = "BothNominalAndContinuousAttrs.arff";
		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		List<String> attributeNames = new ArrayList<>();
		attributeNames.add("Outlook"); attributeNames.add("Temperature"); attributeNames.add("Humidity"); 
		attributeNames.add("Windy");  attributeNames.add("PlayTennis"); 

		String [] attr1Values = new String[]{"Sunny", "Overcast", "Rainy"};
		String [] attr2Values = null;
		String [] attr3Values = null;
		String [] attr4Values = new String[]{"true", "false"};
		String [] attr5Values = new String[]{"yes", "no"};
		List<String[]> attrValueNames = new ArrayList<>();
		attrValueNames.add(attr1Values);attrValueNames.add(attr2Values);attrValueNames.add(attr3Values);
		attrValueNames.add(attr4Values);attrValueNames.add(attr5Values);

		List<Attribute.Type> types = new ArrayList<>();
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NUMERIC); types.add(Attribute.Type.NUMERIC);
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);

		checkAttributes(attributes, attributeNames, attrValueNames, types);
		Assert.assertTrue(arffElements.getRelationName().equals( "play-tennis"));
		Assert.assertTrue(arffElements.getDataStartLine() == 10);
	}
	
	@Test
	public void testCaseSensitivityAndAttrNameInQuotes(){
		String fileName = "CaseSensitivityAndAttrNameInQuotes.arff";
		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		List<String> attributeNames = new ArrayList<>();
		attributeNames.add("Outlook"); attributeNames.add("Temperature"); attributeNames.add("Humidity"); 
		attributeNames.add("Wind");  attributeNames.add("PlayTennis"); 

		String [] attr1Values = new String[]{"Sunny", "Overcast", "Rain"};
		String [] attr2Values = new String[]{"Hot", "Mild", "Cool"};
		String [] attr3Values = new String[]{"High", "Normal"};
		String [] attr4Values = new String[]{"Strong", "Weak"};
		String [] attr5Values = new String[]{"Yes", "No"};
		List<String[]> attrValueNames = new ArrayList<>();
		attrValueNames.add(attr1Values);attrValueNames.add(attr2Values);attrValueNames.add(attr3Values);
		attrValueNames.add(attr4Values);attrValueNames.add(attr5Values);

		List<Attribute.Type> types = new ArrayList<>();
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);

		checkAttributes(attributes, attributeNames, attrValueNames, types);
		
		Assert.assertTrue(arffElements.getRelationName().equals( "play-tennis"));
		Assert.assertTrue(arffElements.getDataStartLine() == 10);	
	}
	
	@Test
	public void testSpecialCharsInNominalVals(){	
		String fileName = "SpecialCharsInNominalVals.arff";
		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		List<String> attributeNames = new ArrayList<>();
		attributeNames.add("Alt"); attributeNames.add("Bar"); attributeNames.add("IsFri"); 
		attributeNames.add("Hungry"); attributeNames.add("Patrons"); attributeNames.add("Price"); 
		attributeNames.add("Raining"); attributeNames.add("Reservation"); attributeNames.add("Type"); 
		attributeNames.add("WaitEstimate"); attributeNames.add("WillWait"); 
		
		String [] attr1Values = new String[]{"Y", "N"};
		String [] attr2Values = new String[]{"Y", "N"};
		String [] attr3Values = new String[]{"Y", "N"};
		String [] attr4Values = new String[]{"Y", "N"};
		String [] attr5Values = new String[]{"Some", "Full", "None"};
		String [] attr6Values = new String[]{"Exp", "Mod", "Chp"};
		String [] attr7Values = new String[]{"Y", "N"};
		String [] attr8Values = new String[]{"Y", "N"};
		String [] attr9Values = new String[]{"French", "Thai", "Italian", "Burger"};
		String [] attr10Values = new String[]{"0-10", "10-30", "30-60", ">60"};
		String [] attr11Values = new String[]{"Y", "N"};
		
		List<String[]> attrValueNames = new ArrayList<>();
		attrValueNames.add(attr1Values);attrValueNames.add(attr2Values);attrValueNames.add(attr3Values);
		attrValueNames.add(attr4Values);attrValueNames.add(attr5Values);attrValueNames.add(attr6Values);
		attrValueNames.add(attr7Values);attrValueNames.add(attr8Values);attrValueNames.add(attr9Values);
		attrValueNames.add(attr10Values);attrValueNames.add(attr11Values);

		List<Attribute.Type> types = new ArrayList<>();
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);
		types.add(Attribute.Type.NOMINAL); types.add(Attribute.Type.NOMINAL);

		checkAttributes(attributes, attributeNames, attrValueNames, types);
		
		Assert.assertTrue(arffElements.getRelationName().equals("restaurant-visit"));
		Assert.assertTrue(arffElements.getDataStartLine() == 15);
	}
	
	@Test
	public void testInvalidAttributeName(){
		String fileName = "InvalidAttributeName.arff";
		
		ArffElements arffElements = ParserUtils.getArffElements(ROOT + fileName);		
		List<Attribute> attributes = arffElements.getAttributes();

		Assert.assertTrue(attributes.size() == 4);
		
	}

	private void checkAttributes(List<Attribute> attributes,
			List<String> attributeNames, List<String[]> attrValueNames, List<Attribute.Type> types) {
		Assert.assertTrue(attributes.size() == attributes.size());
		for(int i = 0; i < attributes.size(); i++){

			// Check attribute names
			String attrName = attributeNames.get(i);
			Attribute attribute = attributes.get(i);
			Assert.assertTrue(attribute.getName().equals(attrName));

			// Check value names
			List<Value> values = attribute.getAllValues();
			if(values == null || values.size() == 0){
				Assert.assertTrue(attrValueNames.get(i) == null);
			}
			else{
				String[] valueNames = attrValueNames.get(i);
				Assert.assertTrue(values.size() == valueNames.length);
				for(int j = 0; j < values.size(); j++){
					String valueName = valueNames[j];
					Value value = values.get(j);
					Assert.assertTrue(value.getName().equals(valueName));
				}	
			}		
		}
		
		// Check attribute types
		Assert.assertTrue(attributes.size() == types.size());
		for(int k = 0; k < types.size(); k++){
			Attribute attribute = attributes.get(k);
			Attribute.Type attrType = attribute.getType();
			Assert.assertTrue(attrType == types.get(k));
		}	
	}	
}
