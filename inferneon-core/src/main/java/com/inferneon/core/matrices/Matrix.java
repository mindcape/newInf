package com.inferneon.core.matrices;

import com.inferneon.core.exceptions.DeterminantForNonSquareMatrix;
import com.inferneon.core.exceptions.IllegalOperationOnSigularMatrix;
import com.inferneon.core.exceptions.IncompatibleMatrixOperation;
import com.inferneon.core.exceptions.LUDecompositionForNonSquareMatrix;
import com.inferneon.core.exceptions.MatrixElementIndexOutOfBounds;
import com.inferneon.core.utils.MathUtils;

public class Matrix implements IMatrix{

	private static final String INCOMPATIBLE_MATRIX_MULTIPLICATION = "Cannot multiply a %s x %s matrix with a %s x %s matrix. The rows of the first matrix must equal the columns of the second matrix";
	private static final String INCOMPATIBLE_MATRIX_ADDITION = "Cannot add a %s x %s matrix with a %s x %s matrix. The two matrices must have the same dimension.";
	private static final String MATRIX_ELEMENT_INDEX_OUT_OF_BOUNDS = "Invalid index for %s : %s";
	private static final String DETERMINANT_FOR_NON_SQUARE_MATRIX = "Cannot compute determinants for a non-square matrix";
	private static final String LU_DECOMP_FOR_NON_SQUARE_MATRIX = "Cannot compute LU decomposition for a non-square matrix";
	private static final String LU_DECOMP_FOR_SINGULAR_MATRIX = "Cannot compute LU decomposition for a singular matrix";
	private static final String INCOMPATIBLE_MATRIX_DIVISION= "Cannot do matrix division: incompatible matrices";

	protected double data[][];
	protected long mRows;
	protected long nColumns;

	public Matrix(double [][] data){
		if(data == null){
			this.data = new double[][]{};
		}
		else{
			this.data = data;
		}

		mRows = this.data.length;
		if(mRows == 0){
			nColumns = 0;
		}
		else{
			nColumns = this.data[0].length;
		}
	}


	@Override
	public IMatrix initialize(double value, boolean rowMatrix, boolean columnMatrix) {
		double newMatData[][] = null;

		if((rowMatrix && columnMatrix) || (!rowMatrix && !columnMatrix)){
			newMatData = new double[(int)mRows][(int)nColumns];
			
			for(int m = 0; m < mRows;m++){
				for(int n = 0; n < nColumns; n++){
					newMatData[m][n] = value;
				}				
			}
			return new Matrix(newMatData);
		}

		if(rowMatrix){
			newMatData = new double[(int)mRows][1];
			for(int m = 0; m < mRows; m++){
				newMatData[m][0] = value;
			}			
			return new Matrix(newMatData);
		}
		else {
			newMatData = new double[1][(int)nColumns];
			for(int n = 0; n < nColumns; n++){
				newMatData[0][n] = value;
			}			
			return new Matrix(newMatData);
		}
	}

	@Override
	public IMatrix transpose() {

		double [][] transpose = new double[(int)nColumns][(int)mRows];
		for(int m = 0; m < mRows; m++){
			for(int n = 0; n < nColumns; n++){
				transpose[n][m] = data[m][n];
			}
		}
		return new Matrix(transpose);
	}

	@Override
	public IMatrix product(IMatrix M) throws IncompatibleMatrixOperation, MatrixElementIndexOutOfBounds{

		if(isEmptyArray()){
			// TODO What does it mean to multiply with an empty array?
			return this;
		}

		if(M.isEmptyArray()){
			// TODO What does it mean to multiply with an empty array?
			return M;
		}

		int numColumsOfM = (int)M.getNumColumns();
		int numRowsOfM = (int)M.getNumRows();

		if(nColumns != numRowsOfM){
			throw new IncompatibleMatrixOperation(INCOMPATIBLE_MATRIX_MULTIPLICATION, mRows, nColumns, M.getNumRows(), M.getNumColumns());
		}

		double [][] product = new double[(int) mRows][numColumsOfM];

		for(int m = 0; m < mRows; m++){
			double [] rowElements = data[m];
			for(int n = 0; n < numColumsOfM; n++){
				double element = 0.0;
				for(int k = 0; k < rowElements.length; k++){
					element += rowElements[k] * M.getElement(k, n);							
				}

				product[m][n] = element;
			}
		}		

		return new Matrix(product);
	}

	@Override
	public IMatrix divide(boolean column, IMatrix divisorMat) throws MatrixElementIndexOutOfBounds, IncompatibleMatrixOperation{
		int numRowsOfOtherMat = (int) divisorMat.getNumRows();
		int numColumnsOfOtherMat = (int) divisorMat.getNumColumns();

		double[][] resultData = new double[(int)mRows][(int)nColumns];
		IMatrix resultMat = new Matrix(resultData);
		if(column){
			if(!(numRowsOfOtherMat == 1 && numColumnsOfOtherMat == nColumns)){
				throw new IncompatibleMatrixOperation(INCOMPATIBLE_MATRIX_DIVISION);
			}

			for(int n = 0; n < nColumns; n++){
				double divisorDataAtColumn = divisorMat.getElement(0, n);
				for(int m = 0; m < mRows; m++){					
					resultData[m][n] = data[m][n] * (1.0 / divisorDataAtColumn);
				}
			}
		}
		else{
			if(!( numColumnsOfOtherMat == 1 && numRowsOfOtherMat == mRows)){
				throw new IncompatibleMatrixOperation(INCOMPATIBLE_MATRIX_DIVISION);
			}

			for(int m = 0; m < mRows; m++){
				double divisorDataAtRow = divisorMat.getElement(m, 0);
				for(int n = 0; n < nColumns; n++){					
					resultData[m][n] = data[m][n]  * (1.0 / divisorDataAtRow);
				}
			}
		}

		return resultMat;
	}

	@Override
	public Double determinant() throws DeterminantForNonSquareMatrix, MatrixElementIndexOutOfBounds {		

		if(mRows != nColumns){
			throw new DeterminantForNonSquareMatrix(DETERMINANT_FOR_NON_SQUARE_MATRIX);
		}

		return determinant(this);
	}

	private double determinant( Matrix M) throws MatrixElementIndexOutOfBounds{
		int dimension = (int)M.getNumColumns();

		if(dimension == 1){
			return M.getElement(0, 0);
		}

		if(dimension == 2){
			double result = M.getElement(0, 0) * M.getElement(1, 1) - M.getElement(0, 1) * M.getElement(1, 0);
			return result;
		}

		double det = 0.0;
		double dataResidual[][] = new double[dimension -1][dimension -1];

		for(int pivot = 0; pivot < dimension; pivot++){
			double pivotElement = M.getElement(0, pivot);
			int signFactor = pivot == 0 || pivot % 2 == 0 ? 1 : -1;				
			double factor = signFactor * pivotElement;

			for(int m = 1; m < dimension; m++){
				for(int n = 0; n < dimension; n++){
					if(n < pivot){
						dataResidual[m-1][n] = M.getElement(m, n);
					}
					if(n > pivot){
						dataResidual[m-1][n-1] = M.getElement(m, n);
					}					
				}
			}

			Matrix residualMat = new Matrix(dataResidual);
			det +=  factor * determinant(residualMat);
		}

		return det;
	}


	@Override
	public IMatrix LUDecomposition() throws IllegalOperationOnSigularMatrix, LUDecompositionForNonSquareMatrix {
		if(mRows != nColumns){
			throw new LUDecompositionForNonSquareMatrix(LU_DECOMP_FOR_NON_SQUARE_MATRIX);
		}

		double LUData[][] = ((Matrix)copy()).getData();
		LUDecomposition LU = new LUDecomposition(LUData);
		double pivot =  data[0][0];
		int pivotRow = 0;
		int paritySign = 1;
		int mutate[] = new int[(int)mRows];
		for(int i = 0; i < (int) mRows; i++){
			mutate[i] = i;
		}

		for(int j = 0; j < nColumns; j++){
			for(int i = 0; i < mRows; i++){		

				int maxIndex = Math.min(j,  i);

				double sum = 0.0;
				for(int k = 0; k < maxIndex; k++){
					sum += LUData[i][k] * LUData[k][j];
				}				
				LUData[i][j] = LUData[i][j] - sum;
			}			

			pivot = Math.abs(LUData[j][j]);
			pivotRow = j;
			for(int i = j+1; i < nColumns; i++){				
				if(Double.compare(Math.abs(LUData[i][j]), pivot) > 0){
					pivot = LUData[i][j];
					pivotRow = i;
				}
			}

			if(Double.compare(pivot, 0.0) == 0){
				throw new IllegalOperationOnSigularMatrix(LU_DECOMP_FOR_SINGULAR_MATRIX);
			}

			if(pivotRow != j){
				LU.swapRows(pivotRow, j);
				int temp = mutate[pivotRow]; mutate[pivotRow] = mutate[j]; mutate[j] = temp; 
				paritySign = - paritySign;
			}

			for(int i = j+1; i < nColumns; i++){
				LUData[i][j] /= LUData[j][j];
			}
		}		

		LU.setMutations(mutate);
		LU.setPivotSign(paritySign);

		return LU;
	}

	protected void swapRows(int rowIndex1, int rowIndex2){
		for(int n = 0; n < nColumns; n++){
			double temp = data[rowIndex1][n];
			data[rowIndex1][n] = data[rowIndex2][n];
			data[rowIndex2][n] = temp;
		}
	}

	@Override
	public IMatrix copy(){
		double[][] newData = new double[(int)mRows][(int)nColumns];
		for(int m = 0; m < mRows; m++){
			for(int n = 0; n < nColumns; n++){
				newData[m][n] = data[m][n];
			}
		}
		return new Matrix(newData);
	}

	public double[][] getData(){
		return data;
	}

	@Override
	public IMatrix inverse() {
		if(mRows != nColumns){
			// TODO Computer pseudo-inverse
			return null;
		}
		else{
			Matrix identity = (Matrix)identity();
			try {
				Matrix solution = (Matrix) solve(identity);
				return solution;
			} catch (IllegalOperationOnSigularMatrix
					| LUDecompositionForNonSquareMatrix e) {
				// TODO Log error
				e.printStackTrace();
				return null;
			}
		}		
	}

	@Override
	public IMatrix solve(IMatrix rhs) throws IllegalOperationOnSigularMatrix, LUDecompositionForNonSquareMatrix {
		if(mRows != nColumns){
			// TODO For non-square matrices, use QR decomposition
			return null;
		}

		LUDecomposition lu = (LUDecomposition) LUDecomposition();
		Matrix solution = (Matrix)lu.solve(rhs);
		return solution;
	}

	@Override
	public long getNumRows() {
		return mRows;
	}

	@Override
	public long getNumColumns() {
		return nColumns;
	}

	@Override
	public boolean isEmptyArray(){
		if(mRows == 0 & nColumns == 0){
			return true;
		}

		return false;
	}

	@Override
	public IMatrix add(IMatrix otherMatrix) throws IncompatibleMatrixOperation {

		int otherMatNumRows = (int) otherMatrix.getNumRows();
		int otherMatNumColumns = (int) otherMatrix.getNumColumns();

		boolean columnAddition = false;
		boolean rowAddition = false;
		if(otherMatNumRows != mRows){
			if(! (otherMatNumColumns == nColumns && otherMatNumRows == 1)){
				throw new IncompatibleMatrixOperation(INCOMPATIBLE_MATRIX_ADDITION, mRows, nColumns, otherMatNumRows, otherMatNumColumns);
			}	
			columnAddition = true;
		}

		if(otherMatNumColumns != nColumns){
			if(! (otherMatNumRows == mRows && otherMatNumColumns == 1)){
				throw new IncompatibleMatrixOperation(INCOMPATIBLE_MATRIX_ADDITION, mRows, nColumns, otherMatNumRows, otherMatNumColumns);
			}	
			rowAddition = true;
		}

		double[][] sum = new double[(int) mRows][(int)nColumns];
		Matrix sumMatrix = new Matrix(sum);

		try{
			if(columnAddition){
				for(int n = 0; n < nColumns; n++){
					double columnMean = otherMatrix.getElement(0, n);
					for(int m = 0; m < mRows; m++){
						sum[m][n] = data[m][n] + columnMean;
					}
				}
			}
			else if(rowAddition){
				for(int m = 0; m < mRows; m++){
					double rowMean = otherMatrix.getElement(m, 0);
					for(int n = 0; n < nColumns; n++){
						sum[m][n] = data[m][n] + rowMean;
					}
				}
			}
			else{
				for (int i = 0; i < mRows; i++) {
					for (int j = 0; j < nColumns; j++) {
						sum[i][j] = data[i][j] + otherMatrix.getElement(i, j);
					}
				}
			}
		}
		catch(MatrixElementIndexOutOfBounds me){
			// TODO : Log here. Should not happen
		}

		return sumMatrix;
	}

	@Override
	public double getElement(long rowNum, long columnNum) throws MatrixElementIndexOutOfBounds {

		if(rowNum < 0 || rowNum > mRows -1){
			throw new MatrixElementIndexOutOfBounds(MATRIX_ELEMENT_INDEX_OUT_OF_BOUNDS, true, rowNum, mRows);
		}
		if(columnNum < 0 || columnNum > nColumns -1){
			throw new MatrixElementIndexOutOfBounds(MATRIX_ELEMENT_INDEX_OUT_OF_BOUNDS, false, columnNum, nColumns);
		}

		return data[(int)rowNum][(int)columnNum];
	}	

	@Override
	public void setElement(long rowNum, long columnNum, double operand,  MatrixElementOperation operation) throws MatrixElementIndexOutOfBounds {

		if(rowNum < 0 || rowNum > mRows -1){
			throw new MatrixElementIndexOutOfBounds(MATRIX_ELEMENT_INDEX_OUT_OF_BOUNDS, true, rowNum, mRows);
		}
		if(columnNum < 0 || columnNum > nColumns -1){
			throw new MatrixElementIndexOutOfBounds(MATRIX_ELEMENT_INDEX_OUT_OF_BOUNDS, false, columnNum, nColumns);
		}

		if(operation == MatrixElementOperation.REPLACE){
			data[(int)rowNum][(int)columnNum] = operand;
		}
		else if(operation == MatrixElementOperation.ADD){
			data[(int)rowNum][(int)columnNum] += operand ;
		}
		else if(operation == MatrixElementOperation.MULTIPLY){
			data[(int)rowNum][(int)columnNum] *= operand;
		}
		else if(operation == MatrixElementOperation.POWER){
			double val = data[(int)rowNum][(int)columnNum] ;
			data[(int)rowNum][(int)columnNum] = Math.pow(val, operand);
		}
	}

	@Override
	public IMatrix getMatrixSubset(int[] rows, int startColumnIndex, int endColumnIndex) throws MatrixElementIndexOutOfBounds {
		if(rows.length > mRows){
			throw new MatrixElementIndexOutOfBounds(MATRIX_ELEMENT_INDEX_OUT_OF_BOUNDS, true, rows.length, mRows);
		}
		if(startColumnIndex < 0 || endColumnIndex < 0 
				|| startColumnIndex >= nColumns || endColumnIndex >= nColumns) {
			throw new MatrixElementIndexOutOfBounds(MATRIX_ELEMENT_INDEX_OUT_OF_BOUNDS, false, rows.length, mRows);
		}

		int numRows = rows.length;
		double[][] subset = new double[numRows][endColumnIndex - startColumnIndex + 1];
		Matrix subsetMatrix = new Matrix(subset);

		for (int i = 0; i < numRows; i++) {
			for (int j = startColumnIndex; j <= endColumnIndex; j++) {
				subset[i][j - startColumnIndex] = data[rows[i]][j];
			}
		}

		return subsetMatrix;
	}

	@Override
	public String toString(){

		if(mRows == 0 && nColumns == 0){
			return "Empty array";
		}

		String newLine = System.getProperty("line.separator");
		String description = mRows + " x " + nColumns + " matrix :" + newLine; 

		description += "[";
		for(int i = 0; i < mRows; i++){
			for(int j = 0; j < nColumns; j++){
				description += (i == 0 && j == 0 ? "" : " ")  + data[i][j] + " ";				
			}
			description += newLine;
		}
		description += "]" + newLine;

		return description;
	}

	@Override
	public IMatrix identity() {
		double inverseData [][] = new double[(int)mRows][(int)nColumns];
		for (int i = 0; i < mRows; i++) {
			for (int j = 0; j < nColumns; j++) {
				inverseData[i][j] = (i == j ? 1.0 : 0.0);
			}
		}
		return new Matrix(inverseData);
	}

	@Override
	public void product(Double factor) {
		for(int m = 0; m < mRows; m++){
			for(int n = 0; n < nColumns; n++){
				data[m][n] *= factor;
			}
		}
	}

	@Override
	public void roundDoubleValues(int precision) {
		for(int m = 0; m < mRows; m++){
			for(int n = 0; n < nColumns; n++){
				data[m][n] = MathUtils.roundDouble(data[m][n], precision);
			}
		}
	}

	@Override
	public boolean equals(Object otherObject){
		if(!(otherObject instanceof Matrix)){
			return false;
		}

		Matrix otherMatrix = (Matrix)otherObject;
		if(!(mRows == otherMatrix.getNumRows() && nColumns == otherMatrix.getNumColumns())){
			return false;
		}

		try{
			for(int m = 0; m < mRows; m++){
				for(int n = 0; n < nColumns; n++){
					double thisElement = data[m][n];
					double otherElement = otherMatrix.getElement(m, n);
					if(Double.compare(thisElement, otherElement) != 0){
						return false;
					}
				}
			}
		}catch(MatrixElementIndexOutOfBounds e){
			return false;
		}

		return true;
	}

	@Override
	public IMatrix normalize(boolean column, boolean useVariance) {

		IMatrix means = null;
		IMatrix variances = null;
		IMatrix diff = null;
		try {
			if(column){
				means = means(true);
				if(useVariance){
					variances = variances(means, true);	
				}
				means.product(-1.0);
				diff = add(means);
				if(useVariance){								
					return diff.divide(column, variances);
				}
				else{
					return diff;
				}
			}
			else{
				means = means(false);				
				if(useVariance){
					variances = variances(means, false);	
				}
				means.product(-1.0);
				diff = add(means);
				if(useVariance){
					return diff.divide(column, variances);
				} 
				else{
					return diff;
				}
			}
		} catch (IncompatibleMatrixOperation e) {
			// TODO Log here. Should not happen
			return null;
		}
		catch (MatrixElementIndexOutOfBounds e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	@Override
	public IMatrix variances(IMatrix means, boolean columns) throws MatrixElementIndexOutOfBounds{
		if(means == null){
			means = means(columns);
		}

		Matrix variancesMat = null;
		if(columns){
			// Column-wise
			double variances[][] = new double[1][(int)nColumns];
			variancesMat = new Matrix(variances);
			for(int n = 0; n < nColumns; n++){				
				double var = 0.0;		
				double columnMean = means.getElement(0,n);
				for(int m = 0; m < mRows; m++){					
					var += Math.pow(data[m][n] - columnMean, 2.0);
				}
				if(Double.compare(var, 0.0) == 0){
					// No variance in this feature, lets use the same data as variance
					variances[0][n] = data[0][n];
					if(Double.compare(variances[0][n], 0.0) == 0){
						variances[0][n] = 1.0; // Arbitrarily choose 1.
					}
				}
				else{
					variances[0][n] = var / mRows;
				}
			}
		}
		else{
			// Row-wise
			double variances[][] = new double[(int)mRows][1];
			variancesMat = new Matrix(variances);
			for(int m = 0; m < mRows; m++){				
				double var = 0.0;
				double rowMean = means.getElement(m,0);
				for(int n = 0; n < nColumns; n++){					
					var += Math.pow(data[m][n] - rowMean, 2.0);
				}
				if(Double.compare(var, 0.0) == 0){
					// No variance in this feature, lets use the same data as variance
					variances[m][0] = data[m][0];
					if(Double.compare(variances[m][0], 0.0) == 0){
						variances[m][0] = 1.0; // Arbitrarily choose 1.
					}
				}
				else{
					variances[m][0] = var / nColumns;
				}
			}
		}

		return variancesMat;
	}

	@Override
	public Double mean() {
		Double sum = 0.0;
		for(int m = 0; m < mRows; m++){		
			for(int n = 0; n < mRows; n++){		
				sum += data[m][n];
			}
		}

		return sum / (mRows * nColumns);
	}

	@Override
	public IMatrix means(boolean columns) {
		IMatrix meansMatrix = null;
		if(columns){
			double means[][] = new double[1][(int)nColumns];
			meansMatrix = new Matrix(means);

			for(int n = 0; n < nColumns; n++){				
				double sum = 0.0;				
				for(int m = 0; m < mRows; m++){					
					sum += data[m][n];
				}
				means[0][n] = sum / mRows;
			}

		}
		else{
			double means[][] = new double[(int)mRows][1];
			meansMatrix = new Matrix(means);

			for(int m = 0; m < mRows; m++){				
				double sum = 0.0;				
				for(int n = 0; n < nColumns; n++){					
					sum += data[m][n];
				}
				means[m][0] = sum / nColumns;
			}

		}

		return meansMatrix;
	}
}
