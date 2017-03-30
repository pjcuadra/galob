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

package de.dortmund.fh.pimes.gitlab.galob.alg.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.dortmund.fh.pimes.gitlab.galob.alg.ExecutionTimeFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.GraphNode;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleAllele;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;

import org.jenetics.Optimize;
import org.junit.Before;
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
   *
   * @throws Exception
   *           when fails
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    randTemp = randomGen.nextDouble() * MAX_TEMP;

    env = new HeterogeneousComputingEnv(1 + randomGen.nextInt(MAX_NUM_TASKS),
        1 + randomGen.nextInt(MAX_NUM_CORES));

  }

  @Test
  public void testCheckCriteria() throws Exception {
    final int numTasks = 3;
    final int numCores = 2;
    HeterogeneousComputingEnv envTest = new HeterogeneousComputingEnv(numTasks, numCores);
    GraphNode[] tasks = new GraphNode[numTasks];

    // Add all tasks
    for (int i = 0; i < numTasks; i++) {
      tasks[i] = envTest.addUnitExecutionTimeTask();
    }

    // Create dependencies
    envTest.addDependency(tasks[0], tasks[1], 1);
    envTest.addDependency(tasks[0], tasks[2], 1);
    envTest.addDependency(tasks[1], tasks[2], 1);

    ScheduleChromosome normal = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    normal.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    normal.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 0));
    normal.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    ScheduleChromosome greater = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    greater.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    greater.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    greater.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    ExecutionTimeFitnessCalculator fitnessCalc = new ExecutionTimeFitnessCalculator(envTest);

    // Verify the fitness
    assertEquals(3, fitnessCalc.getFitness(normal), EPSILON);
    assertEquals(5, fitnessCalc.getFitness(greater), EPSILON);

    // Create simulated annealing with Minimum technique
    double gammaFactor = 0.8;
    SimulatedAnnealing simAnn =
        new SimulatedAnnealing(gammaFactor, 0, fitnessCalc, Optimize.MINIMUM);

    assertTrue(simAnn.checkCriteria(greater, normal));
    // Since temperature is zero
    assertFalse(simAnn.checkCriteria(normal, greater));

    // Create new simulated annealing with Maximim technique
    simAnn = new SimulatedAnnealing(gammaFactor, 0, fitnessCalc, Optimize.MAXIMUM);

    assertTrue(simAnn.checkCriteria(normal, greater));
    // Since temperature is zero
    assertFalse(simAnn.checkCriteria(greater, normal));

  }

  @Test
  public void testGetTemp() throws Exception {
    LoadBalancingFitnessCalculator lbFitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    double gammaFactor = 0.8;
    SimulatedAnnealing simAnn =
        new SimulatedAnnealing(gammaFactor, randTemp, lbFitnessCalc, Optimize.MAXIMUM);

    assertEquals(randTemp, simAnn.getTemp(), EPSILON);

  }

  @Test
  public void testSetTemp() throws Exception {
    LoadBalancingFitnessCalculator lbFitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    double gammaFactor = 0.8;
    SimulatedAnnealing simAnn =
        new SimulatedAnnealing(gammaFactor, randTemp, lbFitnessCalc, Optimize.MAXIMUM);

    double newTemp = randomGen.nextDouble() * MAX_TEMP;

    simAnn.setTemp(newTemp);
    assertEquals(newTemp, simAnn.getTemp(), EPSILON);

  }

}
