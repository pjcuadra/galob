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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

/**
 * Utils unit testing.
 *
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class UtilTest {
  /**
   * Random generator.
   */
  private Random randomGen;
  /**
   * Number of tasks.
   */
  private int numTask;
  /**
   * Number of cores.
   */
  private int numCores;
  /**
   * Max. number of tasks.
   */
  static final int MAX_NUM_TASKS = 16;
  /**
   * Max. number cores.
   */
  static final int MAX_NUM_CORES = 16;
  /**
   * Delta for double comparison.
   */
  static final double EPSILON = 0.00001;

  /**
   * Test environment set-up.
   *
   * @throws Exception
   *           falure exception
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    numTask = 1 + randomGen.nextInt(MAX_NUM_TASKS);
    numCores = 1 + randomGen.nextInt(MAX_NUM_TASKS);
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Tests createOnesMatrix.
   */
  @Test
  public void testCreateOnesMatrix() {
    double[][] matrix;

    matrix = Util.createOnesMatrix(numTask, numCores);

    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);

    for (int i = 0; i < numTask; i++) {
      assertEquals(numCores, Util.getRowSum(matrix, i), EPSILON);
    }

  }

  /**
   * Tests createEmptyMatrix.
   */
  @Test
  public void testCreateEmptyMatrix() {
    double[][] matrix;

    matrix = Util.createEmptyMatrix(numTask, numCores);

    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);

    for (int i = 0; i < numTask; i++) {
      assertEquals(0, Util.getRowSum(matrix, i), EPSILON);
    }

  }

  @Test
  public void testCreateRandomDependencyMatrix() throws Exception {
    double[][] matrix;
    matrix = Util.createRandomDependencyMatrix(numTask);

    // Verify dimensions
    assertEquals(numTask, matrix.length);
    assertEquals(numTask, matrix[0].length);

    assertEquals(false, Util.checkCycleRandomDependencyMatrix(matrix));
  }

  @Test
  public void testCreateRandomCommunicationCostsMatrix() throws Exception {
    double[][] delta = Util.createRandomDependencyMatrix(numTask);
    double[][] matrix = Util.createRandomCommunicationCostsMatrix(delta);

    // Verify dimensions
    assertEquals(numTask, matrix.length);
    assertEquals(numTask, matrix[0].length);

    assertEquals(false, Util.checkCycleRandomDependencyMatrix(matrix));
  }

  @Test
  public void testMatrixParallelMultiply() throws Exception {
    double[][] matrix = Util.createRandomMatrix(numTask, numTask);
    double[][] maskMatrix = Util.createRandomDependencyMatrix(numTask);
    double[][] resMatrix = Util.matrixParallelMultiply(matrix, maskMatrix);

    // Check that the values where correctly masked
    for (int i = 0; i < numTask; i++) {
      for (int j = 0; j < numTask; j++) {
        if (maskMatrix[i][j] == 0) {
          assertEquals(0, resMatrix[i][j], EPSILON);
        } else {
          assertEquals(matrix[i][j], resMatrix[i][j], EPSILON);
        }
      }
    }

    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Invalid matrices dimensions");

    double[][] wrongSizeMatrix = Util.createRandomDependencyMatrix(numTask + 1);

    Util.matrixParallelMultiply(matrix, wrongSizeMatrix);
  }

  @Test
  public void testClearRow() throws Exception {
    double[][] matrix = Util.createOnesMatrix(numTask, numCores);

    // Check the size
    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);

    // Check the values
    for (int i = 0; i < numTask; i++) {
      // Check that is different than zeros
      assertNotEquals(0, Util.getRowSum(matrix, i), EPSILON);

      // Clear row
      Util.clearRow(matrix, i);

      // Check that now are all zeros
      assertEquals(0, Util.getRowSum(matrix, i), EPSILON);
    }

  }

  @Test
  public void testCheckColZero() throws Exception {
    double[][] matrix = Util.createEmptyMatrix(numTask, numCores);

    // Verify dimensions
    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);

    for (int i = 0; i < numCores; i++) {
      assertTrue(Util.checkColZero(matrix, i));
    }
  }

  @Test
  public void testGetColSum() throws Exception {
    double[][] matrix;

    matrix = Util.createOnesMatrix(numTask, numCores);

    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);

    for (int i = 0; i < numCores; i++) {
      assertEquals(numTask, Util.getColSum(matrix, i), EPSILON);
    }
  }

  @Test
  public void testGetRowSum() throws Exception {
    double[][] matrix;

    matrix = Util.createOnesMatrix(numTask, numCores);

    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);

    for (int i = 0; i < numTask; i++) {
      assertEquals(numCores, Util.getRowSum(matrix, i), EPSILON);
    }
  }

  @Test
  public void testCopyMatrix() throws Exception {
    double[][] matrix = Util.createRandomMatrix(numTask, numCores);
    double[][] matrixCopy = Util.copyMatrix(matrix);

    // Check the size
    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);

    assertEquals(numTask, matrixCopy.length);
    assertEquals(numCores, matrixCopy[0].length);

    // Check the values
    for (int i = 0; i < numTask; i++) {
      assertArrayEquals(matrix[i], matrixCopy[i], EPSILON);
    }

    // Change all values
    for (int i = 0; i < numTask; i++) {
      for (int j = 0; j < numCores; j++) {
        matrixCopy[i][j] = matrixCopy[i][j] + 1;
      }
    }

    // Check that the values are different
    for (int i = 0; i < numTask; i++) {
      for (int j = 0; j < numCores; j++) {
        assertNotEquals(matrix[i][j], matrixCopy[i][j], EPSILON);
      }
    }

  }

  @Test
  public void testCreateRandomMatrix() throws Exception {
    double[][] matrix;

    matrix = Util.createRandomMatrix(numTask, numCores);

    // Check the size
    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);
  }

  @Test
  public void testCreateRandomRow() throws Exception {
    double[] randomRow = Util.createRandomRow(numTask);

    // Check the size
    assertEquals(numTask, randomRow.length);
  }

  @Test
  public void testOfRandomUnitary() throws Exception {
    HeterogeneousComputingEnvTest hceTest = new HeterogeneousComputingEnvTest();

    hceTest.setUp();

    // This test are already implemented by HCE that directly uses this method
    hceTest.testOfRandomUnitary();
  }

  @Test
  public void testOfRandom() throws Exception {
    HeterogeneousComputingEnvTest hceTest = new HeterogeneousComputingEnvTest();

    hceTest.setUp();

    // This test are already implemented by HCE that directly uses this method
    hceTest.testOfRandom();
  }

  @Test
  public void testCheckCycleRandomDependencyMatrix() throws Exception {
    double[][] matrix;
    matrix = Util.createRandomDependencyMatrix(numTask);

    assertEquals(false, Util.checkCycleRandomDependencyMatrix(matrix));

    // Set a dependency in the lower triangular matrix
    int src = 0;
    while (src == 0) {
      src = randomGen.nextInt(numTask);
    }

    int dst = randomGen.nextInt(src);

    matrix[src][dst] = 1;

    assertEquals(true, Util.checkCycleRandomDependencyMatrix(matrix));

  }

}
