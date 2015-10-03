package com.ipsg.inferneon.spark;

import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;

import com.inferneon.core.exceptions.DeterminantForNonSquareMatrix;
import com.inferneon.core.exceptions.IllegalOperationOnSigularMatrix;
import com.inferneon.core.exceptions.IncompatibleMatrixOperation;
import com.inferneon.core.exceptions.LUDecompositionForNonSquareMatrix;
import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;
import com.inferneon.core.matrices.IMatrix;

public abstract class SparkMatrix implements IMatrix {

	private RowMatrix rowMatrix;
	private IndexedRowMatrix indexedRowMatrix;
	
	public SparkMatrix(RowMatrix rowMatrix) {
	}

	public IMatrix transpose() {
		return null;
	}

	public IMatrix product(IMatrix M) throws IncompatibleMatrixOperation,
			MatrixElementIndexOutOfBounds {		
		// TODO Auto-generated method stub
		return null;
	}

	public IMatrix inverse() throws IllegalOperationOnSigularMatrix {
		// TODO Auto-generated method stub
		return null;
	}

	public IMatrix LUDecomposition() throws IllegalOperationOnSigularMatrix,
			LUDecompositionForNonSquareMatrix {
		// TODO Auto-generated method stub
		return null;
	}

	public Double determinant() throws DeterminantForNonSquareMatrix,
			MatrixElementIndexOutOfBounds {
		// TODO Auto-generated method stub
		return null;
	}

	public long getNumRows() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getNumColumns() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getElement(long rowNum, long columnNum)
			throws MatrixElementIndexOutOfBounds {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEmptyArray() {
		// TODO Auto-generated method stub
		return false;
	}

	public void roundDoubleValues(int precision) {
		// TODO Auto-generated method stub
		
	}

	public IMatrix identity() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMatrix solve(IMatrix rhs) throws IllegalOperationOnSigularMatrix,
			LUDecompositionForNonSquareMatrix {
		// TODO Auto-generated method stub
		return null;
	}

	public IMatrix getMatrixSubset(int[] rows, int startColumnIndex,
			int endColumnIndex) throws MatrixElementIndexOutOfBounds {
		// TODO Auto-generated method stub
		return null;
	}

	public IMatrix add(IMatrix otherMatrix) throws IncompatibleMatrixOperation {
		// TODO Auto-generated method stub
		return null;
	}

	public void product(Double factor) {
		// TODO Auto-generated method stub
		
	}
}
