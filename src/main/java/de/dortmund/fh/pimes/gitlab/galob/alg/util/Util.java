/*
 * Genetic Algorithm for Load Balancing
 * Copyright (c) 2016-2017 Pedro Cuadra & Sudheera Reddy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Pedro Cuadra (pjcuadra@gmail.com)
 *    Sudheera Reddy
 */

package de.dortmund.fh.pimes.gitlab.galob.alg.util;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.GraphNode;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;

/**
 * Utils class.
 *
 * @author Pedro Cuadra
 *
 */
public class Util {

  /**
   * Check if the given column has all zeroes.
   *
   * @param matrix
   *          values matrix
   * @param col
   *          given column
   * @return true if the col has all zeroes; else false
   */
  public static boolean checkColZero(double[][] matrix, int col) {

    // Verify parameters
    assert matrix.length > 0;
    assert matrix[0].length >= col : matrix[0].length + " " + col;

    // Iterate over the rows
    for (int i = 0; i < matrix.length; i++) {
      if (matrix[i][col] != 0) {
        return false;
      }
    }

    return true;
  }

  /**
   * Check dependency matrix for cycles.
   *
   * @param delta
   *          dependency matrix
   * @return true if a cycle was found and false otherwise
   */
  public static boolean checkCycleRandomDependencyMatrix(double[][] delta) {
    // Lower diagonal has to be zero
    for (int col = 0; col < delta.length; col++) {
      for (int row = col + 1; row < delta.length; row++) {
        if (delta[row][col] != 0) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Clear a row; fill the row elements with zero.
   *
   * @param matrix
   *          values matrix
   * @param row
   *          row to be cleared
   */
  public static void clearRow(double[][] matrix, int row) {

    // Verify parameters
    assert row < matrix.length;

    matrix[row] = DoubleStream.generate(() -> 0).limit(matrix.length).toArray();

  }

  /**
   * Copy contents of matrix1 to matrix2 and return matrix2.
   *
   * @param matrix
   *          reference matrix to be copied
   * @return cloned matrix from matrix1
   */
  public static double[][] copyMatrix(double[][] matrix) {
    return Arrays.stream(matrix).map(x -> x.clone()).toArray(double[][]::new);
  }

  /**
   * Create empty matrix of size rows*cols.
   *
   * @param rows
   *          rows of the matrix
   * @param cols
   *          columns of the matrix
   * @return empty matrix
   */
  public static double[][] createEmptyMatrix(int rows, int cols) {
    return new double[rows][cols];
  }

  /**
   * Get ones matrix of size rows*cols.
   *
   * @param rows
   *          rows of the matrix
   * @param cols
   *          columns of the matrix
   * @return ones matrix
   */
  public static double[][] createOnesMatrix(int rows, int cols) {
    double[][] ones = new double[rows][cols];

    // Initialize ones matrix with ones}
    for (int currRow = 0; currRow < rows; currRow++) {
      ones[currRow] = DoubleStream.generate(() -> 1).limit(cols).toArray();
    }

    return ones;
  }

  /**
   * Get a random Communication cost matrix out of a dependency matrix.
   *
   * @param delta
   *          the dependency matrix
   * @return communication cost matrix
   */
  public static double[][] createRandomCommunicationCostsMatrix(double[][] delta) {
    return matrixParallelMultiply(createRandomMatrix(delta.length, delta[0].length), delta);
  }

  /**
   * Get a random dependency matrix of size rows*cols which is an upper triangular matrix.
   *
   * @param numTasks
   *          number of tasks
   * @return dependency matrix
   */
  public static double[][] createRandomDependencyMatrix(int numTasks) {
    Random randomGen = new Random();

    double[][] depend = new double[numTasks][numTasks];
    // Initialize the upper triangular matrix with ones randomly

    for (int col = 0; col < numTasks; col++) {
      for (int row = 0; row < col; row++) {
        depend[row][col] = randomGen.nextBoolean() ? 1 : 0;
      }
    }

    return depend;
  }

  /**
   * Create an empty HCE with random number of tasks and cores.
   *
   * @param numTasks
   *          number of tasks
   * @param numCores
   *          number of cores
   * @param maxProvided
   *          numTasks and numCores are maximum values if true or exact values if false
   * @return empty HCE.
   */
  private static HeterogeneousComputingEnv createRandomEnv(
      int numTasks,
      int numCores,
      boolean maxProvided) {

    Random randomGen = new Random();

    int actualNumTasks = numTasks;
    int actualNumCores = numCores;

    // Get random number of tasks and cores
    if (maxProvided) {
      actualNumTasks = 1 + randomGen.nextInt(numTasks);
      actualNumCores = 1 + randomGen.nextInt(numCores);
    }

    // Create the new environment
    return new HeterogeneousComputingEnv(actualNumTasks, actualNumCores);

  }

  /**
   * Get a matrix with random values.
   *
   * @param numRows
   *          number of rows
   * @param numCols
   *          number of columns
   * @return matrix with random values
   */
  public static double[][] createRandomMatrix(int numRows, int numCols) {
    // Verify
    assert numRows > 0;
    assert numCols > 0;

    double[][] randMatrix = new double[numRows][];

    for (int currRow = 0; currRow < numRows; currRow++) {
      randMatrix[currRow] = createRandomRow(numCols);
    }

    return randMatrix;
  }

  /**
   * Create a random matrix row.
   *
   * @param columns
   *          number of columns
   * @return random matrix row
   */
  public static double[] createRandomRow(int columns) {

    Random randomGen = new Random();

    return randomGen.doubles(columns).toArray();
  }

  /**
   * Get sum of values of a given column.
   *
   * @param matrix
   *          values matrix
   * @param col
   *          given col
   * @return sum of values of given row
   */
  public static double getColSum(double[][] matrix, int col) {

    // Verify parameters
    assert col < matrix[0].length;

    double sum = 0;

    // Iterate over the rows
    for (int i = 0; i < matrix.length; i++) {
      // Add all elements in the col
      sum += matrix[i][col];
    }

    return sum;
  }

  /**
   * Get sum of values of a given row.
   *
   * @param matrix
   *          values matrix
   * @param row
   *          given row
   * @return sum of values of given row
   */
  public static double getRowSum(double[][] matrix, int row) {

    // Verify parameters
    assert row < matrix.length;

    return Arrays.stream(matrix[row]).map(i -> i).sum();
  }

  /**
   * Fills the graph inside the HCE.
   *
   * @param env
   *          heterogeneous computing environment
   * @param delta
   *          dependency matrix
   * @param commCost
   *          communication costs matrix
   * @param etc
   *          expected time to compute matrix
   */
  private static void graphBuilder(
      HeterogeneousComputingEnv env,
      double[][] delta,
      double[][] commCost,
      double[][] etc) {

    GraphNode[] tasks;

    // Create tasks array
    tasks = new GraphNode[env.getNumberOfTasks()];

    // Add all tasks with random etc per cores
    for (int i = 0; i < env.getNumberOfTasks(); i++) {
      tasks[i] = env.addTask(etc[i]);
    }

    // Add dependencies
    for (int i = 0; i < env.getNumberOfTasks(); i++) {
      for (int j = 0; j < env.getNumberOfTasks(); j++) {
        if (delta[i][j] == 0) {
          continue;
        }

        // Add the dependency
        env.addDependency(tasks[i], tasks[j], commCost[i][j]);

      }
    }

  }

  /**
   * Multiply to matrix as follows result(i,j) = A(i,j) * B(i,j).
   *
   * @param matrixA
   *          matrix A
   * @param matrixB
   *          matrix B
   * @return multiplied matrix
   */
  public static double[][] matrixParallelMultiply(double[][] matrixA, double[][] matrixB) {
    // Verify parameters
    assert matrixA.length > 0 : "Invalid matrices dimensions";
    assert matrixA.length == matrixB.length : "Invalid matrices dimensions";
    assert matrixA[0].length > 0 : "Invalid matrices dimensions";
    assert matrixA[0].length == matrixB[0].length : "Invalid matrices dimensions";

    double[][] resMatrix = new double[matrixA.length][matrixA[0].length];

    for (int i = 0; i < resMatrix.length; i++) {
      for (int j = 0; j < resMatrix[0].length; j++) {
        resMatrix[i][j] = matrixA[i][j] * matrixB[i][j];
      }
    }

    return resMatrix;
  }

  /**
   * Generates a completely random heterogeneous computing environment.
   *
   * @param numTasks
   *          number of tasks
   * @param numCores
   *          number of cores
   * @param maxProvided
   *          numTasks and numCores are maximum values if true or exact values if false
   * @return random heterogeneous computing environment
   */
  protected static HeterogeneousComputingEnv ofRandom(
      int numTasks,
      int numCores,
      boolean maxProvided) {

    HeterogeneousComputingEnv env = createRandomEnv(numTasks, numCores, maxProvided);

    // Create random dependencies, communication costs and etc matrices
    double[][] delta = createRandomDependencyMatrix(env.getNumberOfTasks());
    double[][] commCost = createRandomCommunicationCostsMatrix(delta);
    double[][] etc = createRandomMatrix(env.getNumberOfTasks(), env.getNumberOfExecutors());

    // Build the graph
    graphBuilder(env, delta, commCost, etc);

    return env;

  }

  /**
   * Generates a random heterogeneous computing environment with: unit execution time, unit
   * communication costs and random dependencies.
   *
   * @param numTasks
   *          number of tasks
   * @param numCores
   *          number of cores
   * @return random heterogeneous computing environment
   */
  protected static HeterogeneousComputingEnv ofRandomUnitary(
      int numTasks,
      int numCores,
      boolean maxProvided) {
    HeterogeneousComputingEnv env = createRandomEnv(numTasks, numCores, maxProvided);

    // Create random dependencies, communication costs and etc matrices
    double[][] delta = createRandomDependencyMatrix(env.getNumberOfTasks());
    double[][] commCost = copyMatrix(delta);
    double[][] etc = createOnesMatrix(env.getNumberOfTasks(), env.getNumberOfExecutors());

    // Build the graph
    graphBuilder(env, delta, commCost, etc);

    return env;

  }

}
