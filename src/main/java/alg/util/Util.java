package alg.util;

import java.util.Random;

public class Util {

	/**
	 * Get ones matrix of size rows*cols
	 * @param rows rows of the matrix
	 * @param cols columns of the matrix
	 * @return ones matrix
	 */
	public static double[][] getOnesMatrix(int rows, int cols)
	{
		double[][] ones = new double[rows][cols];
		// Initialize ones matrix with ones}
		for (int currRow = 0; currRow < rows; currRow++)
		{
			for (int currCol = 0; currCol < cols; currCol++)
			{
				ones[currRow][currCol] = 1;
			}
		}

		return ones;
	}


	/**
	 * Get dependency matrix of size rows*cols which is an upper triangular matrix
	 * @param rows rows of the matrix
	 * @param cols columns of the matrix
	 * @return dependency matrix
	 */

	public static int[][] getDPNDMatrix(int numTasks){
		Random randomGen = new Random();

		int[][] depend = new int[numTasks][numTasks];
		// Initialize the upper triangular matrix with ones randomly

		for (int currRow = 0; currRow < numTasks; currRow++)
		{  

			for (int currCol = currRow+1; currCol < numTasks; currCol++)
			{
				if((randomGen.nextInt(20))>9) // Randomly fill 0 or 1 with a randomness probability of 49%
				{
					depend[currRow][currCol] = 1;
				}
			}

		}

		return depend;
	}
	
	


	/**
	 * Copy contents of matrix1 to matrix2 and return matrix2
	 * @param Mat1 reference matrix to be copied
	 * @param Mat2 new matrix copied into
	 * @return Mat2 cloned matrix from matrix1
	 */

	static public double[][] copyMatrix(double[][]Mat1,double[][]Mat2){

		for(int i=0; i<Mat1.length; i++)
		{
			for(int j=0; j<Mat1[i].length; j++)
			{
				Mat2[i][j]=Mat1[i][j];
			}
		}
		return Mat2;

	}
}
