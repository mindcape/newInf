package com.inferneon.core.matrices;

import com.inferneon.core.exceptions.DeterminantForNonSquareMatrix;
import com.inferneon.core.exceptions.IllegalOperationOnSigularMatrix;
import com.inferneon.core.exceptions.IncompatibleMatrixOperation;
import com.inferneon.core.exceptions.LUDecompositionForNonSquareMatrix;
import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;

public interface IMatrix {

	/*
	 * Initializes a matrix with the values given. 
	 * If rowMatrix is true, a row matrix with the same number of rows as this matrix is returned.
	 * If columnMatrix is true, a column matrix with the same number of column as this matrix is returned.
	 * If both rowMatrix and columnMatrix are true (or both are false), a matrix with the same dimension as this matrix is returned.
	 */
	public IMatrix initialize(double value, boolean rowMatrix, boolean columnMatrix);
	
	public IMatrix copy();
	
	public IMatrix transpose();
	
	public IMatrix add(IMatrix otherMatrix) throws IncompatibleMatrixOperation;

	public IMatrix product(IMatrix M) throws IncompatibleMatrixOperation, MatrixElementIndexOutOfBounds;	

	public IMatrix inverse() throws IllegalOperationOnSigularMatrix;
	
	public IMatrix LUDecomposition()  throws IllegalOperationOnSigularMatrix, LUDecompositionForNonSquareMatrix;
	
	public Double determinant() throws DeterminantForNonSquareMatrix, MatrixElementIndexOutOfBounds;
	
	public long getNumRows();
	
	public long getNumColumns();
	
	public double getElement(long rowNum, long columnNum) throws MatrixElementIndexOutOfBounds;
	
	public void setElement(long rowNum, long columnNum, double operand, MatrixElementOperation operatior) throws MatrixElementIndexOutOfBounds;

	public boolean isEmptyArray();
	
	public void roundDoubleValues(int precision);
	
	public IMatrix identity();
	
	public IMatrix solve(IMatrix rhs) throws IllegalOperationOnSigularMatrix, LUDecompositionForNonSquareMatrix;
	
	public IMatrix getMatrixSubset(int[] rows, int startColumnIndex, int endColumnIndex) throws MatrixElementIndexOutOfBounds;

	public void product(Double factor);
	
	public IMatrix divide(boolean column, IMatrix divisorMat) throws MatrixElementIndexOutOfBounds, IncompatibleMatrixOperation;
	
	public IMatrix normalize(boolean column, boolean useVariance);
	
	public Double mean();
	
	public IMatrix means(boolean columns);
	
	public IMatrix variances(IMatrix means, boolean columns) throws MatrixElementIndexOutOfBounds;
	
}
