package com.inferneon.core.matrices;

import com.inferneon.core.exceptions.IllegalOperationOnSigularMatrix;
import com.inferneon.core.exceptions.LUDecompositionForNonSquareMatrix;
import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;

public class LUDecomposition extends Matrix {

	private int pivotSign;
	private int[] mutations;

	public LUDecomposition(double data[][]){
		super(data);
	}

	public LUDecomposition(double data[][], int pivotSign, int[] mutations){
		super(data);		
		this.pivotSign = pivotSign;
		this.mutations = mutations;		
	}

	public int getPivotSign() {
		return pivotSign;
	}

	public int[] getMutations() {
		return mutations;
	}

	public void setPivotSign(int pivotSign) {
		this.pivotSign = pivotSign;
	}

	public void setMutations(int[] mutations) {
		this.mutations = mutations;
	}

	public Matrix getLower(){
		double[][] L = new double[(int)mRows][(int)nColumns];

		for (int i = 0; i < mRows; i++) {
			for (int j = 0; j < nColumns; j++) {
				if (i > j) {
					L[i][j] = data[i][j];
				} else if (i == j) {
					L[i][j] = 1.0;
				} else {
					L[i][j] = 0.0;
				}
			}
		}

		Matrix lower = new Matrix(L);
		return lower;
	}

	public Matrix getUpper() {
		double[][] U = new double[(int)mRows][(int)nColumns];
		for (int i = 0; i < nColumns; i++) {
			for (int j = 0; j < nColumns; j++) {
				if (i <= j) {
					U[i][j] = data[i][j];
				} else {
					U[i][j] = 0.0;
				}
			}
		}
		Matrix upper = new Matrix(U);
		return upper;
	}

	public IMatrix solve(IMatrix rhs) throws LUDecompositionForNonSquareMatrix {
		if(mRows != nColumns){
			// TODO Use meaningful message in the exception
			throw new LUDecompositionForNonSquareMatrix("");
		}

		Matrix Xmat = null;
		
		// Copy right hand side with pivoting
		int numXColumns = (int)rhs.getNumColumns();

		try{
			Xmat = (Matrix) rhs.getMatrixSubset(mutations, 0, numXColumns-1);
			double[][] X = Xmat.getData();

			// Solve L*Y = RHS (piv,:)
			for (int k = 0; k < nColumns; k++) {
				for (int i = k+1; i < nColumns; i++) {
					for (int j = 0; j < numXColumns; j++) {
						X[i][j] -= X[k][j]* data[i][k];
					}
				}
			}
			// Solve U*X = Y;
			for (int k = (int)nColumns-1; k >= 0; k--) {
				for (int j = 0; j < numXColumns; j++) {
					X[k][j] /= data[k][k];
				}
				for (int i = 0; i < k; i++) {
					for (int j = 0; j < numXColumns; j++) {
						X[i][j] -= X[k][j]*data[i][k];
					}
				}
			}
			return Xmat;

		}
		catch( MatrixElementIndexOutOfBounds exception){
			// TODO Should not happen: log this and exit
		}
		
		return Xmat;
	}
}

