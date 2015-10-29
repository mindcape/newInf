package com.inferneon.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;

public class CommonTestUtils {

	private static final String APP_TEMP_FOLDER = "Inferneon";

	public static IInstances createInstancesFromArffFile(String filePath, String inputFileName) throws Exception{		
		ArffElements arffElements = ParserUtils.getArffElements(filePath, inputFileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(inputFileName, arffElements.getData(), APP_TEMP_FOLDER);
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);

		return instances;
	}

	protected static String getCreatedCSVFilePath(String arffFileName, String data, String appTempFolder){
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

	public static String getAppTempDir(String appTempFolder){

		String sysTempFolder = System.getProperty("java.io.tmpdir");
		String tempPath = sysTempFolder + (sysTempFolder.endsWith(File.separator)? "": File.separator) + appTempFolder + File.separator;
		File tempDir = new File(tempPath);
		tempDir.mkdir();

		return tempPath;
	}

	public static void clearAppTempFolder(){
		File tempPath = new File(getAppTempDir(APP_TEMP_FOLDER));
		try {
			FileUtils.cleanDirectory(tempPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
