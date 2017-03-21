/**
 * Execution Time optimization unit testing.
 */

package alg;

import static org.junit.Assert.assertEquals;

import alg.util.HeterogeneousComputingEnv;
import alg.util.jenetics.ScheduleChromosome;

import org.junit.Before;
import org.junit.Test;

/**
 * Execution Time optimization unit testing.
 * 
 * @author Pedro Cuadra
 *
 */
public class ExecutionTimeFitnessCalculatorTest {
  /**
   * Random Chromosome.
   */
  private ScheduleChromosome chromosome;
  /**
   * Maximum number of tasks.
   */
  private static final int maxNumTask = 16 /* Actual max*/;
  /**
   * Maximum number of executors.
   */
  private static final int maxNumExecutors = 16 /* Actual max*/;
  /**
   * Heterogeneous Computing Environment.
   */
  private HeterogeneousComputingEnv env;
  
  /**
   * Test Set-up.
   * 
   * @throws Exception failure exception
   */
  @Before
  public void setUp() throws Exception {
    
    env = HeterogeneousComputingEnv.ofRandomUnitary(maxNumTask, 
        maxNumExecutors, 
        true);
    
    chromosome = new ScheduleChromosome(env);

  }

  @Test
  public void testCalcFitness() throws Exception {
    LoadBalancingFitnessCalculator lbFitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    ExecutionTimeFitnessCalculator etFitnessCalc = new ExecutionTimeFitnessCalculator(env);
    double expectedFitness = etFitnessCalc.getFitness(chromosome);
    double actualFitness = lbFitnessCalc.getFitness(chromosome);

    assertEquals(expectedFitness, actualFitness, 0.00001);
  }

}
