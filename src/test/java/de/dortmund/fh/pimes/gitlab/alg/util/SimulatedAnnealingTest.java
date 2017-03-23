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

package de.dortmund.fh.pimes.gitlab.alg.util;

import static org.junit.Assert.assertEquals;

import de.dortmund.fh.pimes.gitlab.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.alg.util.SimulatedAnnealing;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

public class SimulatedAnnealingTest {
  /**
   * Random generator.
   */
  private Random randomGen;
  /**
   * Max. temperature.
   */
  private static final int MAX_TEMP = 1000;
  /**
   * Current temperature.
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

  @Ignore("Note yet implemented")
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
