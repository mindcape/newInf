package com.inferneon.supervised.linearregression;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inferneon.core.Attribute;
import com.inferneon.core.IInstances;
import com.inferneon.core.Instance;
import com.inferneon.core.InstancesFactory;
import com.inferneon.core.Value;
import com.inferneon.core.arffparser.ArffElements;
import com.inferneon.core.arffparser.ParserUtils;
import com.inferneon.core.matrices.IMatrix;
import com.inferneon.core.matrices.Matrix;
import com.inferneon.core.utils.MathUtils;
import com.inferneon.supervised.SupervisedLearningTest;


public class LinearRegressionTests extends SupervisedLearningTest {

	private static final Logger LOG = LoggerFactory.getLogger(LinearRegressionTests.class);
	private static final String ROOT = "/TestResources/LinearRegression";
	private static final String APP_TEMP_FOLDER = "Inferneon";
	
	static {
		try {
			Class.forName("com.inferneon.core.Instances");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void simpleLinearRegression() throws Exception {
		
		LinearRegression linearRegression = new LinearRegression();		
		IInstances instances = loadInstances("ex0.arff");		
		linearRegression.train(instances);
		
		IMatrix parameters = (IMatrix) linearRegression.getParameters();
		LOG.info("parameters : {}", parameters);		
		parameters.roundDoubleValues(3);
		
		Matrix expectedParameters = new Matrix(new double[][] {{3.00774324}, {1.69532264}});
		expectedParameters.roundDoubleValues(3);
		Assert.assertTrue(parameters.equals(expectedParameters));	
		
		List<Value> values = new ArrayList<Value>(); values.add(new Value(1.0)); values.add(new Value(0.9456));
		values.add(new Value());                    // Missing (to be classified or predicted)
		Instance testInstance = new Instance(values);
		Value predicted = linearRegression.classify(testInstance);
		Assert.assertNotNull(predicted);
		double predictedVal = MathUtils.roundDouble(predicted.getNumericValueAsDouble().doubleValue(), 5);
		Assert.assertTrue(Double.compare(predictedVal, 4.61079) == 0);
	}	
	
	@Test
	public void ridgeRegression() throws Exception {
		
		LinearRegression linearRegression = new LinearRegression(0.135335283237);		
		IInstances instances = loadInstances("ex0.arff");		
		linearRegression.train(instances);
		
		IMatrix parameters = (IMatrix) linearRegression.getParameters();
		LOG.info("parameters : {}", parameters);		
		parameters.roundDoubleValues(6);
		
		Matrix expectedParameters = new Matrix(new double[][] {{ 0.0},{ 0.14474926}});
		expectedParameters.roundDoubleValues(6);
		Assert.assertTrue(parameters.equals(expectedParameters));		
		
		List<Value> values = new ArrayList<Value>(); values.add(new Value(1.0)); values.add(new Value(0.9456));
		values.add(new Value());                    // Missing (to be classified or predicted)
		Instance testInstance = new Instance(values);
		Value predicted = linearRegression.classify(testInstance);
		Assert.assertNotNull(predicted);
		double predictedVal = MathUtils.roundDouble(predicted.getNumericValueAsDouble().doubleValue(), 5);
		Assert.assertTrue(Double.compare(predictedVal, 4.61079) == 0);
	}	
	
	@Test
	public void stagewiseRegression() throws Exception {
		
		LinearRegression linearRegression = new LinearRegression(0.01, 200);		
		IInstances instances = loadInstances("abalone.arff");		
		linearRegression.train(instances);
		
		IMatrix parameters = (IMatrix) linearRegression.getParameters();
		LOG.info("parameters : {}", parameters);		
		parameters.roundDoubleValues(2);
		
		Matrix expectedParameters = new Matrix(new double[][] {{ 0.05},  {0.0}, {0.09},  {0.03},  {0.31}, {-0.64},  {0.0},    {0.36}});
		expectedParameters.roundDoubleValues(6);
		Assert.assertTrue(parameters.equals(expectedParameters));		
		
		List<Value> values = new ArrayList<Value>(); 
		values.add(new Value(1.0)); values.add(new Value(0.475)); values.add(new Value(0.36));
		values.add(new Value(0.14)); values.add(new Value(0.5135)); values.add(new Value(0.241));
		values.add(new Value(0.1045)); values.add(new Value(0.155));
		values.add(new Value());                    // Missing (to be classified or predicted)
		Instance testInstance = new Instance(values);
		Value predicted = linearRegression.classify(testInstance);
		Assert.assertNotNull(predicted);
		double predictedVal = MathUtils.roundDouble(predicted.getNumericValueAsDouble().doubleValue(), 2);
		Assert.assertTrue(Double.compare(predictedVal, 9.15) == 0);
	}	
	
	private IInstances loadInstances(String inputFileName) throws Exception{
		ArffElements arffElements = ParserUtils.getArffElements(ROOT, inputFileName);		
		List<Attribute> attributes = arffElements.getAttributes();		
		String csvFilePath = getCreatedCSVFilePath(inputFileName, arffElements.getData(), APP_TEMP_FOLDER);
		IInstances instances = InstancesFactory.getInstance().createInstances("STAND_ALONE", 
				attributes, attributes.size() -1, csvFilePath);
		
		return instances;
	}
	
	@Test
	public void gradientDescent() throws Exception {
		LinearRegression linearRegression = new LinearRegression(0.01, 500, false);
		IInstances instances = loadInstances("Gradient.arff");
		linearRegression.train(instances);
		double[] parameters = linearRegression.getLinearRegressionParams();
		double[] expectedParameters = new double[parameters.length];
		expectedParameters[0] = 7.160844908286279;
		expectedParameters[1] = 3.6295147381292714;
		Assert.assertTrue(Arrays.equals(parameters, expectedParameters));
		
		List<Value> values = new ArrayList<Value>(); 
		values.add(new Value(1.0)); values.add(new Value(0.3203146));
		values.add(new Value()); 
		Instance testInstance = new Instance(values);
		Value predicted = linearRegression.classify(testInstance);
		Assert.assertNotNull(predicted);
		double predictedVal = MathUtils.roundDouble(predicted.getNumericValueAsDouble().doubleValue(), 3);
		Assert.assertTrue(Double.compare(predictedVal, 8.323) == 0);
	}
	
	@Test
	public void stochasticGradientDescent() throws Exception {
		LinearRegression linearRegression = new LinearRegression(0.01, 1, true);
		IInstances instances = loadInstances("Gradient.arff");
		linearRegression.train(instances);
	}
	
	@After
	public void tearDown(){
		File tempPath = new File(getAppTempDir(APP_TEMP_FOLDER));
		try {
			FileUtils.cleanDirectory(tempPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
