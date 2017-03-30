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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.GraphNode;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleAllele;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleGene;

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
  /**
   * Delta for double comparison.
   */
  private static final double EPSILON = 0.00001;
  
  /**
   * Test environment set-up.
   * 
   * @throws Exception
   *           failure exception
   */
  @Before
  public void setUp() throws Exception {
    env = HeterogeneousComputingEnv.ofRandomUnitary(MAX_NUM_TASKS, 
        MAX_NUM_CORES, 
        true);
    chromosome = new ScheduleChromosome(env);
    fitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    
    // Create the factory
    sf = new StatsFactory(env, fitnessCalc);

  }

  @Ignore("Note yet implemented")
  @Test
  public void testGetNodesExecutionTime() throws Exception {
    int numCores = env.getNumberOfExecutors();
    int numTasks = env.getNumberOfTasks();
    double [] nodesExecTime = new double[numCores];
    
    
    
    
  }
  
  @Ignore("Note yet implemented")
  @Test
  public void testGetExecutionUnit() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  
  /*
   *  +-+           +-+
   *  |0+----------->2|
   *  +++           +++
   *   |             ^
   *   |    +-+      |
   *   +---->1+------+
   *        +-+
   */
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

 
  @Test
  public void testGetTotalTime() throws Exception {
    
    // Test case 1
    HeterogeneousComputingEnv envTest = nestedDependency();
    
    ScheduleChromosome chromosomeTest = new ScheduleChromosome(envTest);
    
    // Guarantee that task 0 and 2 are in the same core and 1 in the other
    chromosomeTest.scheduleSeq.get(0).mutate(new ScheduleAllele(0, 0, 0, 0));
    chromosomeTest.scheduleSeq.get(1).mutate(new ScheduleAllele(0, 0, 1, 1));
    chromosomeTest.scheduleSeq.get(2).mutate(new ScheduleAllele(0, 0, 2, 0));
    
    for (ScheduleGene gene: chromosomeTest.scheduleSeq) {
      System.out.println("Task ID: " + gene.getAllele().getTaskId());
      System.out.println("Core ID: " + gene.getAllele().getExecutorId());
    }
    
    Stats stat = sf.ofChromosome(chromosomeTest);
    
    assertEquals(5, stat.getTotalTime(), EPSILON);
  }

  @Ignore("Note yet implemented")
  @Test
  public void testGetAverageTime() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testGetStdDev() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testGetFitness() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Test
  public void testGetChromosome() throws Exception {
    // Create a stat instance
    Stats stat = sf.ofChromosome(chromosome);
    
    // Check that the chromosome was added to the stats object
    assertEquals(chromosome, stat.getChromosome());
  }
}
