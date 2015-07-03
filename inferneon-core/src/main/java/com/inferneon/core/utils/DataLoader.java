package com.inferneon.core.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.IInstances.Context;
import com.inferneon.core.Instance;
import com.inferneon.core.Instances;
import com.inferneon.core.Value;
import com.inferneon.core.exceptions.InvalidDataException;

public class DataLoader {

	private static final Context CONTEXT = Context.STAND_ALONE;
	
//	public static final IInstances loadData(List<Attribute> attributes, String filePath) throws IOException, InvalidDataException{
//		InputStream inputStream = new FileInputStream(new File(filePath));
//		return loadData(attributes, inputStream, true);
//	}

//	public static IInstances loadData(List<Attribute> attributes, String data,
//			String fileName)  throws IOException, InvalidDataException{
//		InputStream inputStream = new ByteArrayInputStream(data.getBytes());
//		return loadData(attributes, inputStream, false);		
//	}

	
//	public static Double getSumOfWeights(List<Instance> instances){
//		double sum = 0L;
//		for(Instance inst: instances){
//			sum += inst.getWeight();
//		}		
//		return sum;
//	}
}
