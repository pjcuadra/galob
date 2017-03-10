/**
 * Execution Time optimization unit testing.
 */

package alg;

import alg.util.HeterogeneousComputingEnv;
import alg.util.jenetics.ScheduleChromosome;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Execution Time optimization unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ExecutionTimeFitnessCalculatorUt {

  private ScheduleChromosome chromosome;
  private ExecutionTimeFitnessCalculator executionTimeGa;
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
    
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandomUnitary(maxNumTask, 
        maxNumExecutors, 
        true);
    
    chromosome = new ScheduleChromosome(env);

    executionTimeGa =  new ExecutionTimeFitnessCalculator(env);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void calculateFitness() {
    double fitness;

    fitness = executionTimeGa.getFitness(chromosome);

    /* 
     * Since our ETC matrix has only ones the fitness value
     * should be 1/number of tasks in one node.
     */

    //assertEquals(fitness, ((double)getMaxTasksAllocToOneNode()), 0.01);
    System.out.println(fitness);

  }

}
