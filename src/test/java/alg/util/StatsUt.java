/**
 * Utils unit testing.
 */

package alg.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Utils unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class StatsUt {
  /**
   * Random generator.
   */
  private Random randomGen;
  /**
   * Number of tasks.
   */
  private int numTask;
  /**
   * Max. number of tasks.
   */
  static final int MAX_NUM_TASKS = 16;
  /**
   * Max. number cores.
   */
  static final int MAX_NUM_CORES = 16;

  /**
   * Test environment set-up.
   * 
   * @throws Exception falure exception
   */
  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void checkDeltaMatrix() {
    double[][] matrix;
    int count = 0;
    matrix = Util.getRandomDeltaMatrix(numTask);
    
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
  public void checkcol() {
    double[][] ones = Util.createOnesMatrix(3, 5);
    assertEquals(Util.checkColZero(ones, 4), false);
    for (int i = 0; i < 3; i++) {
      ones[i][2] = 0;
    } 
    assertEquals(Util.checkColZero(ones, 2), true);
  }

  @Test
  public void checkComcostmatrix() {
    double[][] matrix;
    boolean flag = false;

    matrix = Util.getRandomComcostmatrix(Util.getRandomDeltaMatrix(numTask)); 

    for (int i = 0; i < numTask; i++) {
      if (matrix[i][i] != 0) {
        flag = true;
        break;
      }
    }
    assertEquals(flag, false);

  }
}
