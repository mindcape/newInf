package com.ipsg.inferneon.supervised;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import com.inferneon.supervised.DecisionTreeBuilder;
import com.inferneon.supervised.DecisionTreeBuilder.Criterion;
import com.inferneon.supervised.DecisionTreeBuilder.Method;

public class DecisionTreeTest {
	
	private static final String ROOT = "/TestResources";
	private static final String APP_TEMP_FOLDER = "Inferneon";

	static {
		try {
			Class.forName("com.ipsg.inferneon.spark.SparkInstances");
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
	
	@Test
	public void testC45NoMissingValues() throws Exception {
		String fileName = "C45NoMissingValues.arff";
		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45, Criterion.GAIN_RATIO);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("SPARK", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);
	}	
	
	@Test
	public void testC45TwoMissingContinuousValuesInSameInstance() throws Exception {

		String fileName = "C45TwoMissingContinuousValuesInSameInstance.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45, Criterion.GAIN_RATIO);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("SPARK", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);	
	}	

	@Test
	public void testC45GainRatioManyMissingValuesAtRandom() throws Exception {

		String fileName = "C45ManyMissingValuesAtRandom.arff";

		DecisionTreeBuilder dt = new DecisionTreeBuilder(Method.C45, Criterion.GAIN_RATIO);

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		IInstances instances = InstancesFactory.getInstance().createInstances("SPARK", 
				attributes, attributes.size() -1, csvFilePath);

		dt.train(instances);
		
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
}
