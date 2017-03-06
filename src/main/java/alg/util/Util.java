package alg.util;

import java.util.ArrayList;
import java.util.Random;

/**
 * Utils class 
 * 
 * @author Pedro Cuadra
 *
 */
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
   * Create empty matrix of size rows*cols.
   * 
   * @param rows rows of the matrix
   * @param cols columns of the matrix
   * @return empty matrix
   */
  public static double[][] createEmptyMatrix(int rows, int cols) {
    double[][] ones = new double[rows][cols];
    // Initialize ones matrix with ones}
    for (int currRow = 0; currRow < rows; currRow++) {
      for (int currCol = 0; currCol < cols; currCol++) {
        ones[currRow][currCol] = 0;
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
   * Get Communication cost matrix of size rows*cols.
   * 
   * @param  delta the dependency matrix
   * @return communication cost matrix
   */
  public static double[][] getComcostmatrix(double[][] delta) {
    Random randomGen = new Random();

    double[][] comcost = copyMatrix(delta);

    for (int currRow = 0; currRow < comcost.length; currRow++) {
      for (int currCol = currRow + 1; currCol < comcost.length; currCol++) {
        double rand = randomGen.nextDouble();
        if (comcost[currRow][currCol] != 0) {
          comcost[currRow][currCol] = Math.floor(rand * 100) / 100;
        }
      }

      comcost[currRow][currRow] = 0;

    }

    return comcost;
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
   * Check if the given column has all zeroes.
   * 
   * @param matrix values matrix
   * @param col given column
   * @return true if the col has all zeroes; else false
   */
  public static boolean checkColZero(double[][] matrix, int col) {

    // Iterate over the rows
    for (int i = 0; i < matrix.length; i++) {
      // Add all elements in the col
      if (matrix[i][col] != 0) {
        return false;
      }
    }

    return true;
  }

  /**
   * Clear a row; fill the row elements with zero.
   * 
   * @param matrix values matrix
   * @param row row to be cleared
   */
  public static void clearRow(double[][] matrix, int row) {

    // Iterate over the rows
    for (int i = 0; i < matrix.length; i++) {
      // Add all elements in the col
      matrix[row][i] = 0; 
    }
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
  
  /**
   * Create the levels representation of the dependencies.
   * 
   * @param delta dependencies matrix
   */
  public static ArrayList<ArrayList<Integer>> getDependenciesLevels(double[][] delta) {
    ArrayList<ArrayList<Integer>> levels = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> toDet = new ArrayList<Integer>();
    ArrayList<Integer> thisLevel;
    double[][] myDelta = Util.copyMatrix(delta);
    for (int i = 0; i < delta.length ;i++) {
      toDet.add(new Integer(i));
    }

    while (!toDet.isEmpty()) {
      thisLevel =  new ArrayList<Integer>();

      for (Integer task = 0; task < delta.length; task++) {

        if (!toDet.contains(task)) {
          continue;
        }


        /*
         * check if there is a dependency with a successive task
         * check for ones in the column
         */

        if (!(Util.checkColZero(myDelta, task))) {
          continue;
        }

        toDet.remove(task);

        thisLevel.add(task);

      }
      for (Integer iterator:thisLevel) {
        //to clear the elements of the row
        Util.clearRow(myDelta, iterator);
      }

      levels.add(thisLevel);


    }
    
    return levels;

  }
  
  /**
   * Decrement all the values in a row by one until it's zero.
   * 
   * @param commCost communication cost matrix
   * @param row row
   */
  public static void decrementRow(double[][] commCost, int row) {
    for (int i = 0; i < commCost[0].length; i++) {
      if (--commCost[row][i] < 0) {
        commCost[row][i] = 0;
      }
    }
  }
  
  /**
   * Clear communication costs that are zero because of same
   * node allocation.
   * 
   * @param commCost Communication cost matrix
   * @param omega allocation matrix
   */
  public static void allocComCost(double[][] commCost, int[][] omega) {
    for (int i = 0; i < commCost.length; i++) {
      for (int j = 0; j < commCost[0].length; j ++) {
        
        if (commCost[i][j] == 0) {
          continue;
        }
        
        for (int excId = 0; excId < omega.length; excId++) {
          if ((omega[excId][i] == omega[excId][j]) && (omega[excId][i] == 1)) {
            commCost[i][j] = 0;
          }
        }

      }
    }

  }
}
