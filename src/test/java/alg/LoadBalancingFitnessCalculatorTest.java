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
 * @author Sudheera Bandi
 *
 */
public class LoadBalancingFitnessCalculatorTest {
  /**
   * Schedule chromosome.
   */
  private ScheduleChromosome chromosome;
  /**
   * HCE.
   */
  private HeterogeneousComputingEnv env;
  /**
   * Max number of tasks.
   */
  static final int MAX_NUM_TASKS = 16 /* Actual max*/;
  /**
   * Max number cores.
   */
  static final int MAX_NUM_CORES = 16 /* Actual max*/;
  
  /**
   * Test Set-up.
   * 
   * @throws Exception failure exception
   */
  @Before
  public void setUp() throws Exception {
    
    env = HeterogeneousComputingEnv.ofRandomUnitary(MAX_NUM_TASKS, 
        MAX_NUM_CORES, 
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
