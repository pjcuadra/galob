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

import java.util.Arrays;
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

    matrix = Util.getDeltaMatrix(numTask);

    System.out.println("mat" + Arrays.deepToString(matrix));
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
    System.out.println("mat" + Arrays.deepToString(matrix));
    assertEquals(flag, false);

  }
}
