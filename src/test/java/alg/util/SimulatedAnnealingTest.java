package alg.util;

import static org.junit.Assert.assertEquals;

import alg.LoadBalancingFitnessCalculator;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class SimulatedAnnealingTest {
  /**
   * Random generator.
   */
  private Random randomGen;
  /**
   * Max. temp.
   */
  private static final int MAX_TEMP = 1000;
  /**
   * Current temp.
   */
  private static double randTemp;
  /**
   * Delta for double comparison.
   */
  private static final double EPSILON = 0.00001;
  /**
   * Max. number of tasks.
   */
  private static final int MAX_NUM_TASKS = 16;
  /**
   * Max. number cores.
   */
  private static final int MAX_NUM_CORES = 16;
  /**
   * Heterogeneous Computing environment.
   */
  private HeterogeneousComputingEnv env;
  
  /**
   * Set up test.
   * @throws Exception when fails
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    randTemp =  randomGen.nextDouble() * MAX_TEMP;
    
    env = new HeterogeneousComputingEnv(1 + randomGen.nextInt(MAX_NUM_TASKS), 
        1 + randomGen.nextInt(MAX_NUM_CORES));

  }

  @Test
  public void testCheckCriteria() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Test
  public void testGetTemp() throws Exception {
    LoadBalancingFitnessCalculator lbFitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    double gammaFactor = 0.8;
    SimulatedAnnealing simAnn = new SimulatedAnnealing(gammaFactor, 
        randTemp,
        lbFitnessCalc);
    
    assertEquals(randTemp, simAnn.getTemp(), EPSILON);
    
  }

  @Test
  public void testSetTemp() throws Exception {
    LoadBalancingFitnessCalculator lbFitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    double gammaFactor = 0.8;
    SimulatedAnnealing simAnn = new SimulatedAnnealing(gammaFactor, 
        randTemp,
        lbFitnessCalc);
    
    double newTemp = randomGen.nextDouble() * MAX_TEMP;
    
    simAnn.setTemp(newTemp);
    assertEquals(newTemp, simAnn.getTemp(), EPSILON);
    
  }

}
