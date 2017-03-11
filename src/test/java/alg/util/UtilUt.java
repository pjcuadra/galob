/**
 * Utils unit testing.
 */

package alg.util;

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
public class UtilUt {
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
   * @throws Exception falure exception
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    numTask =  1 + randomGen.nextInt(MAX_NUM_TASKS);
    numCores =  1 + randomGen.nextInt(MAX_NUM_TASKS);
  }
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void checkDeltaMatrix() {
    double[][] matrix;
    int count = 0;
    matrix = Util.createRandomDependencyMatrix(numTask);
    
    // Lower diagonal has to be zero
    for (int i = numTask; i < numTask; i++) {
      for (int j = i; j < numTask; j--) {
        if (matrix[i][j] != 0) {
          count++;
        }
      }
    }

    assertEquals(count, 0);
  }

  @Test
  public void checkComcostmatrix() {
    double[][] matrix;
    boolean flag = false;

    matrix = Util.createRandomCommunicationCostsMatrix(Util.createRandomDependencyMatrix(numTask)); 

    for (int i = 0; i < numTask; i++) {
      if (matrix[i][i] != 0) {
        flag = true;
        break;
      }
    }
    assertEquals(flag, false);

  }
  
  /**
   * Tests createOnesMatrix.
   */
  @Test
  public void createOnesMatrix() {
    double[][] matrix;

    matrix = Util.createOnesMatrix(numTask, numCores); 
    
    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);
    
    for (int i = 0; i < numTask; i++) {
      assertEquals(numCores, Util.getRowSum(matrix, i), EPSILON);
    }
    
  }
  
  /**
   * Test getRowSum.
   */
  @Test
  public void getRowSum() {
    createOnesMatrix();
  }
  
  /**
   * Tests createEmptyMatrix.
   */
  @Test
  public void createEmptyMatrix() {
    double[][] matrix;

    matrix = Util.createEmptyMatrix(numTask, numCores); 
    
    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);
    
    for (int i = 0; i < numTask; i++) {
      assertEquals(0, Util.getRowSum(matrix, i), EPSILON);
    }
    
  }
  
  /**
   * Tests checkColZero.
   */
  @Test
  public void checkColZero() {
    double[][] matrix = Util.createEmptyMatrix(numTask, numCores);

    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);
    
    for (int i = 0; i < numCores; i++) {
      assertTrue(Util.checkColZero(matrix, i));
    }
    
  }
  
  /**
   * Tests createRandomRow.
   */
  @Test
  public void createRandomRow() {
    double[] randomRow = Util.createRandomRow(numTask);

    // Check the size
    assertEquals(numTask, randomRow.length);
    
  }
  
  /**
   * Tests createRandomMatrix.
   */
  @Test
  public void createRandomMatrix() {
    double[][] matrix;

    matrix = Util.createRandomMatrix(numTask, numCores);
    
    // Check the size
    assertEquals(numTask, matrix.length);
    assertEquals(numCores, matrix[0].length);
    
  }
  
  /**
   * Tests copyMatrix.
   */
  @Test
  public void copyMatrix() {
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
  
  /**
   * Tests clearRow.
   */
  @Test
  public void clearRow() {
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
  
  /**
   * Tests matrixParallelMultiply.
   */
  @Test
  public void matrixParallelMultiply() {
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
  
  /**
   * Tests graphBuilder.
   */
  @Test
  public void graphBuilder() {
    assertTrue(false);
  }
  
  /**
   * Tests ofRandom.
   */
  @Test
  public void ofRandom() {
    assertTrue(false);
  }
  
  /**
   * Tests ofRandomUnitary.
   */
  @Test
  public void ofRandomUnitary() {
    assertTrue(false);
  }
  
  /**
   * Tests createOmegaMatrix.
   */
  @Test
  public void createOmegaMatrix() {
    assertTrue(false);
  }
  
  /**
   * Tests getColSum.
   */
  @Test
  public void getColSum() {
    assertTrue(false);
  }
  
}
