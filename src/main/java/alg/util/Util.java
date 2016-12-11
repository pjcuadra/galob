package alg.util;

import java.util.Random;

public class Util {

  /**
   * Get ones matrix of size rows*cols.
   * 
   * @param rows rows of the matrix
   * @param cols columns of the matrix
   * @return ones matrix
   */
  public static double[][] getOnesMatrix(int rows, int cols) {
    double[][] ones = new double[rows][cols];
    // Initialize ones matrix with ones}
    for (int currRow = 0; currRow < rows; currRow++) {
      for (int currCol = 0; currCol < cols; currCol++) {
        ones[currRow][currCol] = 1;
      }
    }

    return ones;
  }


  /**
   * Get dependency matrix of size rows*cols which is an upper triangular matrix.
   * 
   * @param numTasks number of tasks
   * @return dependency matrix
   */

  public static double[][] getDeltaMatrix(int numTasks) {
    Random randomGen = new Random();

    double[][] depend = new double[numTasks][numTasks];
    // Initialize the upper triangular matrix with ones randomly

    for (int currRow = 0; currRow < numTasks; currRow++) {  

      for (int currCol = currRow + 1; currCol < numTasks; currCol++) {
        // Randomly fill 0 or 1 with a randomness probability of 49%
        if ((randomGen.nextInt(20)) > 9) {
          depend[currRow][currCol] = 1;
        }
      }

    }

    return depend;
  }




  /**
   * Copy contents of matrix1 to matrix2 and return matrix2.
   * 
   * @param matrix reference matrix to be copied
   * @return cloned matrix from matrix1
   */

  public static double[][] copyMatrix(double[][]matrix) {

    double[][] copiedMatrix =  new double[matrix.length][matrix[0].length];

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        copiedMatrix[i][j] = matrix[i][j];
      }
    }
    return copiedMatrix;

  }

  /**
   * Get sum of values of a given row.
   * 
   * @param matrix values matrix
   * @param row given row
   * @return sum of values of given row
   */
  public static double getRowSum(double[][] matrix, int row) {
    double rowSum = 0;

    // Iterate over the nodes
    for (int j = 0; j < matrix[0].length; j++) {
      // Add all execution times
      rowSum += matrix[row][j];
    }

    return rowSum;
  }

  /**
   * Multiply to matrix as follows result(i,j) = A(i,j) * B(i,j).
   * 
   * @param matrixA matrix A
   * @param matrixB matrix B
   * @return multiplied matrix
   */
  public static double[][] matrixParallelMultiply(double[][] matrixA, double[][] matrixB) {
    double[][] resMatrix =  new double[matrixA.length][matrixA[0].length];


    for (int i = 0; i < resMatrix.length; i++) {
      for (int j = 0; j < resMatrix[0].length; j++) {
        resMatrix[i][j] =  matrixA[i][j] * matrixB[i][j];
      }
    }

    return resMatrix;
  }

  /**
   * Convert a integer matrix into double matrix.
   * 
   * @param matrix integer matrix
   * @return double matrix
   */
  public static double[][] intMatrixtoDouble(int[][] matrix) {
    double[][] resMatrix =  new double[matrix.length][matrix[0].length];

    for (int i = 0; i < resMatrix.length; i++) {
      for (int j = 0; j < resMatrix[0].length; j++) {
        resMatrix[i][j] = (double) matrix[i][j];
      }
    }

    return resMatrix;

  }
}
