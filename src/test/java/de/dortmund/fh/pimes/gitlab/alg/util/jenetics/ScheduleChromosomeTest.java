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

package de.dortmund.fh.pimes.gitlab.alg.util.jenetics;

import static org.junit.Assert.assertEquals;

import de.dortmund.fh.pimes.gitlab.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.alg.util.graph.GraphNode;
import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleAllele;
import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleChromosome;
import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleGene;

import org.jenetics.util.ISeq;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * Schedule Chromosome unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ScheduleChromosomeTest {

  private Random randomGen;

  private int numTask;
  static final int maxNumTask = 16 /* Actual max*/  /* 0 is not possible so add 1 after*/;
  static final int maxNumExecutors = 16 /* Actual max*/  /* 0 is not possible so add 1 after*/;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * Unit testing set-up.
   * 
   * @throws Exception falure exception
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    numTask =  1 + randomGen.nextInt(maxNumTask);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void checkvalidity() {
    final int numTasks = 4;
    ArrayList<ScheduleGene> allelList = new ArrayList<ScheduleGene>();
    ScheduleAllele allel = null;
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
    int[] chromosomeSeq1 = {0, 1, 2, 3};

    // Create first chromosome
    for (int task: chromosomeSeq1) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    ScheduleChromosome chromosome;
    chromosome = new ScheduleChromosome(env, ISeq.of(allelList));
    assertEquals(true, chromosome.isValid());


    // Already known invalid solution
    int[] chromosomeSeq2 = {0, 2, 1, 3};

    // Create second chromosom
    allelList = new ArrayList<ScheduleGene>();
    for (int task: chromosomeSeq2) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    chromosome = new ScheduleChromosome(env, ISeq.of(allelList));
    assertEquals(false, chromosome.isValid());

    // Already known invalid solution (repeat task)
    int[] chromosomeSeq3 = {0,1,2,0};

    // Create second chromosome
    allelList = new ArrayList<ScheduleGene>();
    for (int task: chromosomeSeq3) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    chromosome = new ScheduleChromosome(env, ISeq.of(allelList));
    assertEquals(false, chromosome.isValid());

  }

  @Test
  public void createCheckValid() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(maxNumTask, 
        maxNumExecutors, 
        true);
    ScheduleChromosome chromosome = new ScheduleChromosome(env);

    // This shall be true everytime. If not we are creating invalid solutions
    assertEquals(chromosome.isValid(), true);

  }
  
  @Test
  public void cloneChromosome() {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(maxNumTask, 
        maxNumExecutors, 
        true);
    ScheduleChromosome chromosomeOrg = new ScheduleChromosome(env);
    ScheduleChromosome chromosomeClone = chromosomeOrg.clone();
    ScheduleGene original;
    ScheduleGene cloned;

    for (int locus = 0; locus < chromosomeOrg.toSeq().size(); locus++) {
      
      original =  chromosomeOrg.getGene(locus);
      cloned =  chromosomeClone.getGene(locus);
      assertEquals(original, cloned);
    }
   

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

    for (int currGene = 0; currGene < chromosome.toSeq().size(); currGene ++) {
      assertEquals(chromosome.toSeq().get(currGene).getAllele().getTaskId(), currGene);
      
    }

  }

  @Ignore("Note yet implemented")
  @Test
  public void testIsValid() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testIterator() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testNewInstance() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testGetGene() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testLength() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testToSeq() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testOf() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testClone() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testSetStats() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testGetStats() throws Exception {
    throw new RuntimeException("not yet implemented");
  }

  @Ignore("Note yet implemented")
  @Test
  public void testHasStats() throws Exception {
    throw new RuntimeException("not yet implemented");
  }
}
