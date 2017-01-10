/**
 * Utils unit testing.
 */

package alg.util;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

/**
 * Utils unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class UtilUt {

  private Random randomGen;
  private int numTask;
  static final int maxNumTask = 16 /* Actual max*/  /* 0 is not possible so add 1 after*/;
  static final int maxNumExecutors = 16 /* Actual max*/  /* 0 is not possible so add 1 after*/;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * Test environment set-up.
   * 
   * @throws Exception falure exception
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    numTask =  1 + randomGen.nextInt(maxNumTask);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void checkDeltaMatrix() {
    double[][] matrix;
    int count = 0;
    matrix = Util.getDeltaMatrix(numTask);
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
    double[][] ones = Util.getOnesMatrix(3, 5);
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

    matrix = Util.getComcostmatrix(Util.getDeltaMatrix(numTask)); 

    for (int i = 0; i < numTask; i++) {
      if (matrix[i][i] != 0) {
        flag = true;
        break;
      }
    }
    assertEquals(flag, false);

  }
}
