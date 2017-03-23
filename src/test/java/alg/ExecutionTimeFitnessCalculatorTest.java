/*
 * Genetic Algorithm for Load Balancing
 * Copyright (c) 2016-2017 Pedro Cuadra & Sudheera Reddy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Pedro Cuadra (pjcuadra@gmail.com)
 *    Sudheera Reddy
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
