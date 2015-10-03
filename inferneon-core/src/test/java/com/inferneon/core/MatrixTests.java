package com.inferneon.core;
import junit.framework.Assert;

import org.junit.Test;

import com.inferneon.core.exceptions.DeterminantForNonSquareMatrix;
import com.inferneon.core.exceptions.IncompatibleMatrixOperation;
import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;
import com.inferneon.core.matrices.LUDecomposition;
import com.inferneon.core.matrices.Matrix;
public class MatrixTests {

	@Test
	public void testInitialize() throws Exception {
		double data[][] = new double[][]{{3.0, 1.0, 2.0}, {4.0, 7.0, 6.0}};
		Matrix m1 = new Matrix(data);
		Matrix init1 = (Matrix) m1.initialize(0.0, true, true);
		Matrix testMat1 = new Matrix(new double[][]{{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}});
		Assert.assertTrue(init1.equals(testMat1));
		
		data= new double[][]{{3.0, 1.0},{ 2.0, 4.0}, {7.0, 6.0}};
		Matrix m2 = new Matrix(data);
		Matrix init2 = (Matrix) m2.initialize(1.0, false, false);
		Matrix testMat2 = new Matrix(new double[][]{{1.0, 1.0}, {1.0, 1.0},{1.0, 1.0}});
		Assert.assertTrue(init2.equals(testMat2));
		
		data= new double[][]{{3.0, 1.0},{ 2.0, 4.0}, {7.0, 6.0}};
		Matrix m3 = new Matrix(data);
		Matrix init3= (Matrix) m3.initialize(2.0, true, false);
		Matrix testMat3 = new Matrix(new double[][]{{2.0}, {2.0}, {2.0}});
		Assert.assertTrue(init3.equals(testMat3));
		
		data= new double[][]{{3.0, 1.0, 2.0}, {4.0, 7.0, 6.0}};
		Matrix m4 = new Matrix(data);
		Matrix init4= (Matrix) m4.initialize(3.0, false, true);
		Matrix testMat4 = new Matrix(new double[][]{{3.0, 3.0, 3.0}});
		Assert.assertTrue(init4.equals(testMat4));	
	}
	
	@Test
	public void testTransposeEmptyMatrix() throws Exception {
		double data[][] = new double[][]{};
		Matrix m1 = new Matrix(data);
		Matrix transpose1 = (Matrix)m1.transpose();
		Assert.assertTrue(transpose1.isEmptyArray());

		data = null;
		Matrix m2 = new Matrix(data);
		Matrix transpose2 = (Matrix)m2.transpose();
		Assert.assertTrue(transpose2.isEmptyArray());		
	}

	@Test
	public void testTransposeSquareMatrix() throws Exception {			
		double data1[][] = new double[][]{{3.0, 1.0, 2.0}, {4.0, 7.0, 6.0}, {12.0, 3.0, 8.0}};
		Matrix m1 = new Matrix(data1);
		Matrix transpose = (Matrix)m1.transpose();

		double expected [][] = new double[][]{{3.0, 4.0, 12.0}, {1.0, 7.0, 3.0},{2.0, 6.0, 8.0}};
		Matrix expectedMat = new Matrix(expected);
		Assert.assertTrue(transpose.equals(expectedMat));		
	}

	@Test
	public void testTransposeNonSquareMatrix() throws Exception {			
		double data1[][] = new double[][]{{3.0, 1.0, 2.0}, {4.0, 7.0, 6.0}};
		Matrix m1 = new Matrix(data1);	
		Matrix transpose = (Matrix)m1.transpose();
		double expected [][] = new double[][]{{3.0, 4.0}, {1.0, 7.0},{2.0, 6.0}};
		Matrix expectedMat = new Matrix(expected);
		Assert.assertTrue(transpose.equals(expectedMat));	
	}

	@Test
	public void testEmptyMatricesMultiplication() throws Exception {				
		double data1[][] = new double[][]{};
		Matrix m1 = new Matrix(data1);			
		double data2[][] = new double[][]{};
		Matrix m2 = new Matrix(data2);

		Matrix product = (Matrix)m1.product(m2);
		Assert.assertTrue(product.isEmptyArray());
	}	

	@Test
	public void testMultiplicationWithEmptyMatrix() throws Exception {				
		double data1[][] = new double[][]{{3.0, 4.0}, {1.0, 7.0},{2.0, 6.0}};
		Matrix m1 = new Matrix(data1);			
		double data2[][] = new double[][]{};
		Matrix m2 = new Matrix(data2);
		Matrix product = (Matrix)m1.product(m2);
		Assert.assertTrue(product.isEmptyArray());

		product = (Matrix)m2.product(m1);
		Assert.assertTrue(product.isEmptyArray());
	}	

	@Test
	public void testIncompatibleMatrixMultiplication() throws Exception {				
		double data1[][] = new double[][]{{3.0, 1.0, 2.0}, {4.0, 7.0, 6.0}};
		Matrix m1 = new Matrix(data1);			
		double data2[][] = new double[][]{{1.0, 8.0}, {7.0, 4.0}};
		Matrix m2 = new Matrix(data2);

		try{
			m1.product(m2);
			Assert.assertTrue(false);
		}
		catch(IncompatibleMatrixOperation e){
			Assert.assertTrue(true);
		}
	}	

	@Test
	public void testSquareMatrixMultiplication() throws Exception {

		double data1[][] = new double[][]{{3.0, 1.0, 2.0}, {4.0, 7.0, 6.0}, {2.0, 7.0, 1.0}};
		double data2[][] = new double[][]{{1.0, 8.0, 3.0}, {7.0, 4.0, 2.0}, {5.0, 1.0, 2.0}};
		Matrix m1 = new Matrix(data1);				
		Matrix m2 = new Matrix(data2);

		Matrix product = (Matrix) m1.product(m2);
		double expected [][] = new double[][]{{20.0, 30.0, 15.0}, {83.0, 66.0, 38.0}, {56.0, 45.0, 22.0}};
		Matrix expectedMat = new Matrix(expected);
		Assert.assertTrue(product.equals(expectedMat));	
	}

	@Test
	public void testMatrixMultiplication1() throws Exception {
		double data1[][] = new double[][]{{3.0, 1.0, 2.0}, {4.0, 7.0, 6.0}};
		double data2[][] = new double[][]{{1.0, 8.0}, {7.0, 4.0}, {1.0, 2.0}};
		Matrix m1 = new Matrix(data1);				
		Matrix m2 = new Matrix(data2);

		Matrix product = (Matrix) m1.product(m2);	
		double expected [][] = new double[][]{{12.0, 32.0}, {59.0, 72.0}};
		Matrix expectedMat = new Matrix(expected);
		Assert.assertTrue(product.equals(expectedMat));	
	}		


	@Test
	public void testMatrixDivisionColumnwise(){
		// Column division :Wrong number of rows
		double data1[][] = new double[][]{{8.0, 9.0, 12.0}, {4.0, 21.0, 16.0}};
		double data2[][] = new double[][]{{1.0, 2.0, 4.0},  {6.0, 3.0, 6.0}};		
		Matrix m1 = new Matrix(data1);				
		Matrix m2 = new Matrix(data2);
		try {
			m1.divide(true, m2);
		} catch (MatrixElementIndexOutOfBounds
				| IncompatibleMatrixOperation e) {
			Assert.assertTrue(e instanceof IncompatibleMatrixOperation);
		}
		
		// Column division : more than required number of columns
		data2 = new double[][]{{1.0, 2.0}};	
		try {
			m1.divide(true, m2);
		} catch (MatrixElementIndexOutOfBounds
				| IncompatibleMatrixOperation e) {
			Assert.assertTrue(e instanceof IncompatibleMatrixOperation);
		}
		
		// Correct division
		data2 = new double[][]{{4.0, 3.0, 2.0}};
		m2 = new Matrix(data2);
		try {
			Matrix result = (Matrix)m1.divide(true, m2);
			double expected[][] = new double[][]{{2.0, 3.0, 6.0}, {1.0, 7.0, 8.0}};	
			Matrix expectedMat = new Matrix(expected);
			Assert.assertTrue(result.equals(expectedMat));
		} catch (MatrixElementIndexOutOfBounds
				| IncompatibleMatrixOperation e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testMatrixDivisionRowwise(){
		// Row division :Wrong number of columns
		double data1[][] = new double[][]{{8.0, 12.0, 16.0}, {4.0, 28.0, 4.0}};
		double data2[][] = new double[][]{{4.0, 4.0}, {3.0, 3.0}, {2.0, 2.0}};		
		Matrix m1 = new Matrix(data1);				
		Matrix m2 = new Matrix(data2);
		try {
			m1.divide(false, m2);
		} catch (MatrixElementIndexOutOfBounds
				| IncompatibleMatrixOperation e) {
			Assert.assertTrue(e instanceof IncompatibleMatrixOperation);
		}
		
		// Correct division
		data2 = new double[][]{{4.0},{2.0}};
		m2 = new Matrix(data2);
		try {
			Matrix result = (Matrix)m1.divide(false, m2);
			double expected[][] = new double[][]{{2.0, 3.0, 4.0}, {2.0, 14.0, 2.0}};	
			Matrix expectedMat = new Matrix(expected);
			Assert.assertTrue(result.equals(expectedMat));
		} catch (MatrixElementIndexOutOfBounds
				| IncompatibleMatrixOperation e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testDeterminantOnSingleElementMatrix(){

	}

	@Test
	public void testDeterminantOnNonSquareMatrix(){

	}

	@Test
	public void testDeterminantOn2x2Matrix(){

		double data[][] = new double[][]{{3.0, 1.0}, {2.0, 4.0}};
		Matrix mat = new Matrix(data);
		try {
			double det = mat.determinant();
			Assert.assertTrue(Double.compare(det, 10.0) == 0);
		} catch (DeterminantForNonSquareMatrix e) {
			Assert.assertTrue(false);
		} catch (MatrixElementIndexOutOfBounds e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testDeterminantOnSquareMatrix1(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0}, 
				{6.0, 1.0, 7.0},
				{2.0, 4.0, 2.0}
				};
		Matrix mat = new Matrix(data);
		try{
			double det = mat.determinant();
			System.out.println("Det = " + det);
			Assert.assertTrue(Double.compare(det, -32.0) == 0);
		}
		catch(Exception e){
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testDeterminantOnSquareMatrix2(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0, 4.0}, 
				{6.0, 1.0, 7.0, 3.0},
				{2.0, 4.0, 2.0, 1.0},
				{8.0, 9.0, 8.0, 5.0}
				};

		Matrix mat = new Matrix(data);
		try{
			double det = mat.determinant();
			Assert.assertTrue(Double.compare(det, 3.0) == 0);			
		}
		catch(Exception e){

		}
	}

	@Test
	public void testDeterminantOnSquareMatrix3(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0, 4.0, 2.0}, 
				{6.0, 1.0, 7.0, 3.0, 1.0},
				{2.0, 4.0, 2.0, 1.0, 9.0},
				{8.0, 9.0, 8.0, 5.0, 7.0},
				{7.0, 1.0, 6.0, 2.0, 6.0},
				};

		Matrix mat = new Matrix(data);
		try{
			double det = mat.determinant();
			System.out.println("Det = " + det);
			Assert.assertTrue(Double.compare(det, 2130.0) == 0);			
		}
		catch(Exception e){

		}
	}

	@Test
	public void testLUDecompositionOnSquareMatrix1(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0, 4.0}, 
				{6.0, 1.0, 7.0, 3.0},
				{2.0, 4.0, 2.0, 1.0},
				{8.0, 9.0, 8.0, 5.0}
				};

		Matrix mat = new Matrix(data);
		try{
			LUDecomposition LU = (LUDecomposition)mat.LUDecomposition();
			LUDecomposition expectedLU = new LUDecomposition(new double[][]
					{{8.0, 9.0, 8.0, 5.0},			
					{0.75, -5.75, 1.0, -0.75}, 
					{0.375, 0.41304347826086957, -1.4130434782608696, 2.4347826086956523}, 
					{0.25, -0.30434782608695654, -0.2153846153846154, 0.04615384615384632}
					});	
			Assert.assertTrue(LU.equals(expectedLU));

			Matrix lower = LU.getLower();
			Matrix expectedLower = new Matrix(new double [][]{
					{1,    0,    0,    0},  
					{ 0.75, 1,    0,    0},  
					{0.38, 0.41, 1,    0},   
					{ 0.25, -0.3, -0.22, 1 }});
			lower.roundDoubleValues(2);
			Assert.assertTrue(lower.equals(expectedLower));
			Matrix upper = LU.getUpper();
			upper.roundDoubleValues(2);
			Matrix expectedUpper= new Matrix(new double [][]{
					{8,    9,     8,     5},   
					{0,    -5.75,  1,    -0.75},
					{ 0,     0,    -1.41,  2.43},
					{0,     0,     0,     0.05}});
			Assert.assertTrue(upper.equals(expectedUpper));			
			Assert.assertTrue(LU.getPivotSign() == 1);
			checkIntegerRow(LU.getMutations(), new int[]{3,1,0,2});

		}
		catch(Exception e){
			Assert.assertTrue(false);
		}
	}

	private void checkIntegerRow(int[] mutations, int[] expected) {
		Assert.assertTrue(mutations.length == expected.length);
		for(int i = 0; i < mutations.length; i++){
			Assert.assertTrue(mutations[i] == expected[i]);
		}	
	}	

	@Test
	public void testInverseOnNonSquareMatrix(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0, 4.0}, 
				{6.0, 1.0, 7.0, 3.0},
				{2.0, 4.0, 2.0, 1.0},
				{8.0, 9.0, 8.0, 5.0}
				};

		Matrix mat = new Matrix(data);
		Matrix inverse = (Matrix) mat.inverse();
		inverse.roundDoubleValues(2);
		Assert.assertNotNull(inverse);	
		double expectedData[][] = new double[][] {
				{-11,    -12,    -55,     27},   
				{  0.67,   0.67,   3.67,  -1.67 },
				{ 7.33,   8.33,  37.33, -18.33 },
				{ 4.67,   4.67,  21.67, -10.67 }};
		Matrix expectedInverse = new Matrix(expectedData);
		Assert.assertTrue(inverse.equals(expectedInverse));
	}		

	@Test
	public void testFlattenedMeanOfMatrix(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0, 4.0}, 
				{6.0, 1.0, 7.0, 3.0},
				{2.0, 4.0, 2.0, 1.0},
				{8.0, 9.0, 8.0, 5.0}
				};

		Matrix mat = new Matrix(data);
		Double mean = mat.mean();
		Assert.assertTrue(Double.compare(mean, 4.125) == 0);
	}

	@Test
	public void testMeanOfMatrix(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0, 4.0}, 
				{6.0, 1.0, 7.0, 3.0},
				{2.0, 4.0, 2.0, 1.0},
				{8.0, 9.0, 8.0, 5.0}
				};

		Matrix mat = new Matrix(data);
		Matrix meansOnColumn = (Matrix)mat.means(true);		
		double expectedData[][] = new double[][]{{ 4.75,  3.75,  4.75,  3.25 }};
		Matrix expectedMeans = new Matrix(expectedData);
		Assert.assertTrue(meansOnColumn.equals(expectedMeans));

		Matrix meansOnRow = (Matrix)mat.means(false);		
		expectedData = new double[][]{{2.5},{4.25 },{2.25 },{7.5 }};
		expectedMeans = new Matrix(expectedData);
		Assert.assertTrue(meansOnRow.equals(expectedMeans));
	}

	@Test
	public void testVarianceOfMatrix(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0, 4.0}, 
				{6.0, 1.0, 7.0, 3.0},
				{2.0, 4.0, 2.0, 1.0},
				{8.0, 9.0, 8.0, 5.0}
				};
		try{
			Matrix mat = new Matrix(data);
			Matrix variancesOnColumn = (Matrix)mat.variances(null, true);		
			double expectedData[][] = new double[][]{{ 5.6875,  10.6875,   7.6875,   2.1875}};
			Matrix expectedVariances = new Matrix(expectedData);
			Assert.assertTrue(variancesOnColumn.equals(expectedVariances));

			Matrix variancesOnRow = (Matrix)mat.variances(null, false);		
			expectedData = new double[][]{{ 1.25  },{ 5.6875},{ 1.1875},{2.25}};
			expectedVariances = new Matrix(expectedData);
			Assert.assertTrue(variancesOnRow.equals(expectedVariances));
		}
		catch(MatrixElementIndexOutOfBounds e){
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testNormalization1(){
		double data[][] = new double[][]
				{{3.0, 1.0, 2.0}, 
				{6.0, 1.0, 7.0},
				{2.0, 4.0, 2.0},
				{8.0, 9.0, 8.0}
				};

		Matrix mat = new Matrix(data);
		Matrix normalizedMat = (Matrix)mat.normalize(true, true);	
		double expectedData[][] = new double[][]{{ -0.30769231, -0.25730994, -0.35772358},
				{ 0.21978022, -0.25730994,  0.29268293},
				{-0.48351648,  0.02339181, -0.35772358},
				{ 0.57142857,  0.49122807,  0.42276423}};
		Matrix expectedNormalizedMat = new Matrix(expectedData);
		normalizedMat.roundDoubleValues(4);
		expectedNormalizedMat.roundDoubleValues(4);
		Assert.assertTrue(normalizedMat.equals(expectedNormalizedMat));

		normalizedMat = (Matrix)mat.normalize(false, true);		
		expectedData = new double[][]{{ 1.5       , -1.5       ,0.0},
				{ 0.19354839, -0.53225806,  0.33870968},
				{-0.75      ,  1.5       , -0.75      },
				{ -1.5      ,  3.0       , -1.5       }};
		expectedNormalizedMat = new Matrix(expectedData);
		normalizedMat.roundDoubleValues(4);
		expectedNormalizedMat.roundDoubleValues(4);
		Assert.assertTrue(normalizedMat.equals(expectedNormalizedMat));
	}

	@Test
	public void testNormalization2(){
		double data1[][] = new double[][]
				{{4.0}, 
				{7.0},
				{2.0},
				{5.0}};

		Matrix mat1 = new Matrix(data1);
		Matrix normalizedMat1 = (Matrix)mat1.normalize(true, false);	
		double expectedData1[][] = new double[][]{{-0.5},{ 2.5},{-2.5},{ 0.5}};
		Matrix expectedNormalizedMat1 = new Matrix(expectedData1);
		normalizedMat1.roundDoubleValues(4);
		expectedNormalizedMat1.roundDoubleValues(4);
		Assert.assertTrue(normalizedMat1.equals(expectedNormalizedMat1));
	}

	@Test
	public void testNormalization3(){
		double data1[][] = new double[][]
				{{3.0, 2.0, 5.0,2.0}};

		Matrix mat1 = new Matrix(data1);
		Matrix normalizedMat1 = (Matrix)mat1.normalize(false, false);	
		double expectedData1[][] = new double[][]{{0.0, -1.0, 2.0, -1}};
		Matrix expectedNormalizedMat1 = new Matrix(expectedData1);
		normalizedMat1.roundDoubleValues(4);
		expectedNormalizedMat1.roundDoubleValues(4);
		Assert.assertTrue(normalizedMat1.equals(expectedNormalizedMat1));
	}	
}