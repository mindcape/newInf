package com.inferneon.supervised;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
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
	
	private static final String ROOT = "/TestResources/NaiveBayes";
	private static final String APP_TEMP_FOLDER = "Inferneon";
	
	static {
		try {
			Class.forName("com.inferneon.core.Instances");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static {
		try {
			Class.forName("com.inferneon.core.Instances");
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
	
	@Test
	public void testSimpleNaiveBayes() throws Exception {
		
		NaiveBayes nb = new NaiveBayes();
		
		String fileName = "PlayTennis.arff";

		ArffElements arffElements = ParserUtils.getArffElements(ROOT, fileName);	
		String csvFilePath = getCreatedCSVFilePath(fileName, arffElements.getData());
		List<Attribute> attributes = arffElements.getAttributes();
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);
		nb.train(instances);
		List<Value> newValues = getValueListForTestInstance(attributes, "Sunny", "Cool", "High", "Strong");
		Instance newInstance = new Instance(newValues);
		
		Value targetClassValue = nb.classify(newInstance);
		
		Assert.assertTrue(targetClassValue.getName().equals("No"));		
		
		System.out.println(nb.getProbablityTable());
	}
}
