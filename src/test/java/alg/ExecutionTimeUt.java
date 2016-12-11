/**
 * Execution Time optimization unit testing.
 */

package alg;

import static org.junit.Assert.assertEquals;

import alg.util.Util;
import alg.util.genetics.ScheduleChromosome;
import alg.util.genetics.ScheduleGene;

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
  private int[][] convMatrix;
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

    convMatrix = executionTimeGa.createOmegaMatrix(chromosome.toSeq());

  }

  @After
  public void tearDown() throws Exception {
  }


  /**
   * Get the amount of task allocated to a given node
   * @param node node index.
   * 
   * @return amount of task allocated to given node
   */
  private int getTaskAllocateOnNode(int node) {
    int amount = 0;

    for (ScheduleGene currGen: chromosome.toSeq()) {
      if (currGen.getAllele().getExecutorId() == node) {
        amount++;
      }
    }

    return amount;

  }

  /**
   * Get the maximum number of task allocated to one node
   * of all the given nodes in the chromosome.
   * 
   * @return maximum amount of tasks allocated to a node
   */
  private int getMaxTasksAllocToOneNode() {
    int maxnumtasks = 0;
    int tempmaxnumtasks = 0;
    for (int i = 0; i < executors; i++) {
      tempmaxnumtasks = getTaskAllocateOnNode(i);

      if (tempmaxnumtasks > maxnumtasks) {
        maxnumtasks = tempmaxnumtasks;
      }
    }
    return maxnumtasks;
  }

  @Test
  public void getSumTime() {
    double[] sumTime;

    sumTime = executionTimeGa.getSumTime(convMatrix);

    /* 
     * Since our ETC matrix has only ones the execution time per node
     * should be the amount of nodes assigned to it.
     */
    for (int currNodeTime = 0; currNodeTime < executors; currNodeTime++) {
      assertEquals(getTaskAllocateOnNode(currNodeTime), sumTime[currNodeTime], 0.001);
    }

  }

  @Test
  public void calculateTotalTime() {

    double totalTime = 0;
    double expectedTotalTime = 0;

    totalTime = executionTimeGa.getTotalTime(convMatrix);

    expectedTotalTime = getMaxTasksAllocToOneNode();
    /* 
     * Since our ETC matrix has only ones the total execution time 
     * should be the amount of tasks.
     */

    assertEquals(expectedTotalTime, totalTime, 0.01);

  }

  @Test
  public void calculateFitness() {
    double fitness;

    fitness = executionTimeGa.getFitness(convMatrix);

    /* 
     * Since our ETC matrix has only ones the fitness value
     * should be 1/number of tasks in one node.
     */

    assertEquals(fitness, ((double)getMaxTasksAllocToOneNode()), 0.01);
    System.out.println(fitness);
  }





  @Test
  public void loadImbalance() {

    executionTimeGa.getLoad(convMatrix);
  }

}
