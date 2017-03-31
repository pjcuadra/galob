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

import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.GraphNode;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleAllele;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;

import org.junit.Before;
import org.junit.Test;

//import java.util.Random;

/**
 * Utils unit testing.
 *
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class StatsTest {
  /**
   * Max. number of tasks.
   */
  static final int MAX_NUM_TASKS = 16;
  /**
   * Max. number cores.
   */
  static final int MAX_NUM_CORES = 16;
  /**
   * Delta for double comparison.
   */
  private static final double EPSILON = 0.00001;
  /**
   * Heterogeneous Computing environment.
   */
  private HeterogeneousComputingEnv env;
  /**
   * Chromomsome.
   */
  private ScheduleChromosome chromosome;
  /**
   * Fitness Calculator.
   */
  private LoadBalancingFitnessCalculator fitnessCalc;
  /**
   * Stats factory.
   */
  private StatsFactory sf;

  private HeterogeneousComputingEnv nestedDependency() throws Exception {
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

    return envTest;
  }

  private HeterogeneousComputingEnv oneRootTwoDepenendantTasks() throws Exception {
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

    return envTest;
  }

  private HeterogeneousComputingEnv chaindedDependency() throws Exception {
    final int numTasks = 3;
    final int numCores = 1;
    HeterogeneousComputingEnv envTest = new HeterogeneousComputingEnv(numTasks, numCores);
    GraphNode[] tasks = new GraphNode[numTasks];

    // Add all tasks
    for (int i = 0; i < numTasks; i++) {
      tasks[i] = envTest.addUnitExecutionTimeTask();
    }

    // Create dependencies
    envTest.addDependency(tasks[0], tasks[1], 1);
    envTest.addDependency(tasks[1], tasks[2], 1);

    return envTest;
  }

  private HeterogeneousComputingEnv independentTasks() throws Exception {
    final int numTasks = 3;
    final int numCores = 2;
    HeterogeneousComputingEnv envTest = new HeterogeneousComputingEnv(numTasks, numCores);
    GraphNode[] tasks = new GraphNode[numTasks];

    // Add all tasks
    for (int i = 0; i < numTasks; i++) {
      tasks[i] = envTest.addUnitExecutionTimeTask();
    }

    return envTest;
  }

  /**
   * Test environment set-up.
   *
   * @throws Exception
   *           failure exception
   */
  @Before
  public void setUp() throws Exception {
    env = HeterogeneousComputingEnv.ofRandomUnitary(MAX_NUM_TASKS, MAX_NUM_CORES, true);
    chromosome = new ScheduleChromosome(env);
    fitnessCalc = new LoadBalancingFitnessCalculator(env, 0);

    // Create the factory
    sf = new StatsFactory(env, fitnessCalc);

  }

  @Test
  public void testGetAverageTime() throws Exception {
    final int numTasks = 2;
    final int numCores = 2;
    HeterogeneousComputingEnv envTest = new HeterogeneousComputingEnv(numTasks, numCores);
    GraphNode[] tasks = new GraphNode[numTasks];
    double[][] etc = { { 1, 1 }, { 0, 1 } };

    // Add all tasks
    tasks[0] = envTest.addTask(etc[0]);
    tasks[1] = envTest.addTask(etc[1]);

    ScheduleChromosome chromosomeTest = new ScheduleChromosome(envTest);

    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));

    // Create the factory
    StatsFactory statsFact = new StatsFactory(envTest, fitnessCalc);
    Stats stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(1, stat.getNodesExecutionTime()[0], EPSILON);
    assertEquals(1, stat.getNodesExecutionTime()[1], EPSILON);

    assertEquals(1, stat.getAverageTime(), EPSILON);

    chromosomeTest = new ScheduleChromosome(envTest);

    // Set both tasks to the same core
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 0));

    // Create factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(1, stat.getNodesExecutionTime()[0], EPSILON);
    assertEquals(0, stat.getNodesExecutionTime()[1], EPSILON);

    assertEquals(0.5, stat.getAverageTime(), EPSILON);
  }

  @Test
  public void testGetChromosome() throws Exception {
    // Create a stat instance
    Stats stat = sf.ofChromosome(chromosome);

    // Check that the chromosome was added to the stats object
    assertEquals(chromosome, stat.getChromosome());
  }

  @Test
  public void testGetFitness() throws Exception {
    // Test case 1 - task in between of dependency chain in another core
    HeterogeneousComputingEnv envTest = nestedDependency();

    ScheduleChromosome chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    StatsFactory statsFact = new StatsFactory(envTest, fitnessCalc);
    Stats stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(stat.getTotalTime(), stat.getFitness(chromosomeTest), EPSILON);

    // Test case 2 - last 2 tasks in parallel
    envTest = oneRootTwoDepenendantTasks();

    chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(stat.getTotalTime(), stat.getFitness(chromosomeTest), EPSILON);

    // Test case 3 - chained tasks in same processor
    envTest = chaindedDependency();

    chromosomeTest = new ScheduleChromosome(envTest);

    // Create the factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(stat.getTotalTime(), stat.getFitness(chromosomeTest), EPSILON);

    // Test case 4 - independent tasks
    envTest = independentTasks();

    chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(stat.getTotalTime(), stat.getFitness(chromosomeTest), EPSILON);
  }

  @Test
  public void testGetNodesExecutionTime() throws Exception {
    // Test case 1 - task in between of dependency chain in another core
    HeterogeneousComputingEnv envTest = nestedDependency();

    ScheduleChromosome chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    StatsFactory statsFact = new StatsFactory(envTest, fitnessCalc);
    Stats stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(5, stat.getNodesExecutionTime()[0], EPSILON);
    assertEquals(3, stat.getNodesExecutionTime()[1], EPSILON);

    // Test case 2 - last 2 tasks in parallel
    envTest = oneRootTwoDepenendantTasks();

    chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(2, stat.getNodesExecutionTime()[0], EPSILON);
    assertEquals(3, stat.getNodesExecutionTime()[1], EPSILON);

    // Test case 4 - independent tasks
    envTest = independentTasks();

    chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(2, stat.getNodesExecutionTime()[0], EPSILON);
    assertEquals(1, stat.getNodesExecutionTime()[1], EPSILON);

  }

  @Test
  public void testGetStdDev() throws Exception {
    final int numTasks = 2;
    final int numCores = 2;
    HeterogeneousComputingEnv envTest = new HeterogeneousComputingEnv(numTasks, numCores);
    GraphNode[] tasks = new GraphNode[numTasks];

    // Add all tasks
    tasks[0] = envTest.addUnitExecutionTimeTask();
    tasks[1] = envTest.addUnitExecutionTimeTask();

    ScheduleChromosome chromosomeTest = new ScheduleChromosome(envTest);

    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));

    // Create the factory
    StatsFactory statsFact = new StatsFactory(envTest, fitnessCalc);
    Stats stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(1, stat.getNodesExecutionTime()[0], EPSILON);
    assertEquals(1, stat.getNodesExecutionTime()[1], EPSILON);

    assertEquals(0, stat.getStdDev(), EPSILON);

    chromosomeTest = new ScheduleChromosome(envTest);

    // Set both tasks to the same core
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 0));

    // Create factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(2, stat.getNodesExecutionTime()[0], EPSILON);
    assertEquals(0, stat.getNodesExecutionTime()[1], EPSILON);

    assertEquals(Math.sqrt(2), stat.getStdDev(), EPSILON);

  }

  @Test
  public void testGetTotalTime() throws Exception {

    // Test case 1 - task in between of dependency chain in another core
    HeterogeneousComputingEnv envTest = nestedDependency();

    ScheduleChromosome chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    StatsFactory statsFact = new StatsFactory(envTest, fitnessCalc);
    Stats stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(5, stat.getTotalTime(), EPSILON);

    // Test case 2 - last 2 tasks in parallel
    envTest = oneRootTwoDepenendantTasks();

    chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(3, stat.getTotalTime(), EPSILON);

    // Test case 3 - chained tasks in same processor
    envTest = chaindedDependency();

    chromosomeTest = new ScheduleChromosome(envTest);

    // Create the factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(3, stat.getTotalTime(), EPSILON);

    // Test case 4 - independent tasks
    envTest = independentTasks();

    chromosomeTest = new ScheduleChromosome(envTest);

    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.toSeq().get(0).mutate(ScheduleAllele.of(envTest, 0, 0));
    chromosomeTest.toSeq().get(1).mutate(ScheduleAllele.of(envTest, 1, 1));
    chromosomeTest.toSeq().get(2).mutate(ScheduleAllele.of(envTest, 2, 0));

    // Create the factory
    statsFact = new StatsFactory(envTest, fitnessCalc);
    stat = statsFact.ofChromosome(chromosomeTest);

    assertEquals(2, stat.getTotalTime(), EPSILON);

  }
}
