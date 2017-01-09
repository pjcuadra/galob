/**
 * Execution Time optimization unit testing.
 */

package alg;

import alg.util.Util;
import alg.util.genetics.ScheduleChromosome;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

/**
 * Execution Time optimization unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ExecutionTimeUt {

  private Random randomGen;

  private double[][] ones;
  private double[][] delta;
  private ScheduleChromosome chromosome;
  private ExecutionTime executionTimeGa;
  private int numTask;
  private int executors;
  static final int maxNumTask = 16 /* Actual max*/;
  static final int maxNumExecutors = 16 /* Actual max*/;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * Test Set-up.
   * 
   * @throws Exception failure exception
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    numTask =  1 + randomGen.nextInt(maxNumTask);
    executors =  1 + randomGen.nextInt(maxNumExecutors);

    ones = Util.getOnesMatrix(executors, numTask);
    delta = Util.getDeltaMatrix(numTask);

    chromosome = new ScheduleChromosome(delta, executors);

    executionTimeGa =  new ExecutionTime(ones, delta);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void calculateFitness() {
    double fitness;

    fitness = executionTimeGa.getFitnessCost(chromosome.toSeq());

    /* 
     * Since our ETC matrix has only ones the fitness value
     * should be 1/number of tasks in one node.
     */

    // assertEquals(fitness, ((double)getMaxTasksAllocToOneNode()), 0.01);
    System.out.println(fitness);
  }

}
