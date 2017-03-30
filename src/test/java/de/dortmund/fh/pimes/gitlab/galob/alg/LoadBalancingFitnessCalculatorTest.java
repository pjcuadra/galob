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

package de.dortmund.fh.pimes.gitlab.galob.alg;

import static org.junit.Assert.assertEquals;

import de.dortmund.fh.pimes.gitlab.galob.alg.ExecutionTimeFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;

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
  static final int MAX_NUM_TASKS = 16 /* Actual max */;
  /**
   * Max number cores.
   */
  static final int MAX_NUM_CORES = 16 /* Actual max */;

  /**
   * Test Set-up.
   * 
   * @throws Exception
   *           failure exception
   */
  @Before
  public void setUp() throws Exception {

    env = HeterogeneousComputingEnv.ofRandomUnitary(MAX_NUM_TASKS, MAX_NUM_CORES, true);

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
