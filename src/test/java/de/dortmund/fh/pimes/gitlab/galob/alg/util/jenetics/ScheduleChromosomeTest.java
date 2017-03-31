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

package de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.Stats;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.StatsFactory;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.GraphNode;

import org.jenetics.util.ISeq;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Schedule Chromosome unit testing.
 *
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ScheduleChromosomeTest {
  /**
   * Random generator.
   */
  private Random randomGen;
  /**
   * Number of tasks.
   */
  private int numTask;
  /**
   * Maximum number of tasks.
   */
  static final int MAX_NUM_TASKS = 16 /* Actual max */ /* 0 is not possible so add 1 after */;
  /**
   * Maximum number of executors.
   */
  static final int MAX_NUM_CORES = 16 /* Actual max */ /* 0 is not possible so add 1 after */;
  /**
   * HCE.
   */
  private HeterogeneousComputingEnv env;
  /**
   * Test Chromosome.
   */
  private ScheduleChromosome testChromosome;
  /**
   * Stats factory.
   */
  private StatsFactory sf;

  /**
   * Unit testing set-up.
   *
   * @throws Exception
   *           falure exception
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    numTask = 1 + randomGen.nextInt(MAX_NUM_TASKS);

    env = HeterogeneousComputingEnv.ofRandom(MAX_NUM_TASKS, MAX_NUM_CORES, true);
    testChromosome = new ScheduleChromosome(env);
    LoadBalancingFitnessCalculator fitnessCalc = new LoadBalancingFitnessCalculator(env, 0);

    sf = new StatsFactory(env, fitnessCalc);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void knowChromosome() {
    GraphNode src;
    GraphNode dst;

    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(5, 3);

    /* (1) -> (2) -> (3) -> (4) -> (5) */
    src = env.addUnitExecutionTimeTask();
    dst = env.addUnitExecutionTimeTask();
    env.addDependency(src, dst, 0);

    src = dst;
    dst = env.addUnitExecutionTimeTask();
    env.addDependency(src, dst, 0);

    src = dst;
    dst = env.addUnitExecutionTimeTask();
    env.addDependency(src, dst, 0);

    src = dst;
    dst = env.addUnitExecutionTimeTask();
    env.addDependency(src, dst, 0);

    ScheduleChromosome chromosome = new ScheduleChromosome(env);

    for (int currGene = 0; currGene < chromosome.toSeq().size(); currGene++) {
      assertEquals(chromosome.toSeq().get(currGene).getAllele().getTaskId(), currGene);

    }

  }

  @Test
  public void testIsValid() throws Exception {
    final int numTasks = 4;

    // This shall be true everytime. If not we are creating invalid solutions
    assertEquals(testChromosome.isValid(), true);

    ArrayList<ScheduleGene> alleleList = new ArrayList<ScheduleGene>();
    ScheduleAllele allele = null;
    GraphNode[] tasks = new GraphNode[numTasks];

    // Create the HCE
    HeterogeneousComputingEnv env = new HeterogeneousComputingEnv(numTasks, numTask);

    for (int i = 0; i < numTasks; i++) {
      tasks[i] = env.addUnitExecutionTimeTask();
    }

    // Define a delta matrix
    env.addDependency(tasks[0], tasks[1], 0);
    env.addDependency(tasks[0], tasks[2], 0);
    env.addDependency(tasks[1], tasks[2], 0);
    env.addDependency(tasks[1], tasks[3], 0);
    env.addDependency(tasks[2], tasks[3], 0);

    // Already known valid solution
    int[] chromosomeSeq1 = { 0, 1, 2, 3 };

    // Create first chromosome
    for (int task : chromosomeSeq1) {
      allele = ScheduleAllele.ofTask(env, task);
      alleleList.add(ScheduleGene.ofAllele(env, allele));
    }

    ScheduleChromosome chromosome;
    chromosome = new ScheduleChromosome(env, ISeq.of(alleleList));
    assertEquals(true, chromosome.isValid());

    // Already known invalid solution
    int[] chromosomeSeq2 = { 0, 2, 1, 3 };

    // Create second chromosom
    alleleList = new ArrayList<ScheduleGene>();
    for (int task : chromosomeSeq2) {
      allele = ScheduleAllele.ofTask(env, task);
      alleleList.add(ScheduleGene.ofAllele(env, allele));
    }

    chromosome = new ScheduleChromosome(env, ISeq.of(alleleList));
    assertEquals(false, chromosome.isValid());

    // Already known invalid solution (repeat task)
    int[] chromosomeSeq3 = { 0, 1, 2, 0 };

    // Create second chromosome
    alleleList = new ArrayList<ScheduleGene>();
    for (int task : chromosomeSeq3) {
      allele = ScheduleAllele.ofTask(env, task);
      alleleList.add(ScheduleGene.ofAllele(env, allele));
    }

    chromosome = new ScheduleChromosome(env, ISeq.of(alleleList));
    assertEquals(false, chromosome.isValid());

  }

  @Test
  public void testIterator() throws Exception {
    ScheduleChromosome clone = testChromosome.clone();
    Iterator<ScheduleGene> itChr = testChromosome.iterator();
    Iterator<ScheduleGene> itClone = clone.iterator();

    // Iterate over all elements
    while (itChr.hasNext()) {
      ScheduleGene testGene = itChr.next();
      ScheduleGene cloneGene = itClone.next();

      assertEquals(testGene, cloneGene);
    }
  }

  @Test
  public void testNewInstance() throws Exception {
    ScheduleChromosome chr = testChromosome.newInstance();

    // Assert dimensions
    assertEquals(env.getNumberOfTasks(), chr.length());
    assertEquals(testChromosome.length(), chr.length());

    for (ScheduleGene gene : chr) {
      assertTrue(gene.getAllele().getExecutorId() < env.getNumberOfExecutors());
    }

    chr = testChromosome.newInstance(testChromosome.toSeq());

    // Assert dimensions
    assertEquals(env.getNumberOfTasks(), chr.length());
    assertEquals(testChromosome.length(), chr.length());

    // Check that it's the same sequence
    for (int i = 0; i < testChromosome.length(); i++) {
      assertEquals(testChromosome.getGene(i), chr.getGene(i));
    }

  }

  @Test
  public void testGetGene() throws Exception {

    for (int i = 0; i < testChromosome.length(); i++) {
      assertEquals(testChromosome.toSeq().get(i), testChromosome.getGene(i));
    }

  }

  @Test
  public void testLength() throws Exception {
    assertEquals(testChromosome.length(), testChromosome.toSeq().size());
  }

  @Test
  public void testToSeq() throws Exception {
    assertEquals(testChromosome.length(), testChromosome.toSeq().size());
  }

  @Test
  public void testOf() throws Exception {
    ScheduleChromosome newChr = ScheduleChromosome.of(env);

    assertEquals(env.getNumberOfTasks(), newChr.length());

    for (ScheduleGene gene : newChr) {
      assertTrue(gene.getAllele().getExecutorId() < env.getNumberOfExecutors());
    }

  }

  @Test
  public void testClone() throws Exception {
    HeterogeneousComputingEnv env =
        HeterogeneousComputingEnv.ofRandom(MAX_NUM_TASKS, MAX_NUM_CORES, true);
    ScheduleChromosome chromosomeOrg = new ScheduleChromosome(env);
    ScheduleChromosome chromosomeClone = chromosomeOrg.clone();
    ScheduleGene original;
    ScheduleGene cloned;

    for (int locus = 0; locus < chromosomeOrg.toSeq().size(); locus++) {

      original = chromosomeOrg.getGene(locus);
      cloned = chromosomeClone.getGene(locus);
      assertEquals(original, cloned);
    }
  }

  @Test
  public void testSetStats() throws Exception {
    testGetStats();
  }

  @Test
  public void testGetStats() throws Exception {
    // By default chromosome don't have stats
    assertEquals(null, testChromosome.getStats());

    Stats stats = sf.ofChromosome(testChromosome);

    this.testChromosome.setStats(stats);

    assertEquals(stats, testChromosome.getStats());
  }

  @Test
  public void testHasStats() throws Exception {
    // By default chromosome don't have stats
    assertFalse(testChromosome.hasStats());
    assertEquals(null, testChromosome.getStats());

    this.testChromosome.setStats(sf.ofChromosome(testChromosome));

    assertTrue(testChromosome.hasStats());

  }
}
