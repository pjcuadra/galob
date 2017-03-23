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
import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleChromosome;
import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleCrossover;
import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleGene;

import org.jenetics.util.MSeq;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Schedule Crossover unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ScheduleCrossoverTest {

  static final int maxNumTask = 9 /* Actual max*/;
  static final int maxNumExecutors = 6 /* Actual max*/;
  static final int maxPopulation = 50 /* Actual max*/;
  private ScheduleCrossover crossover;
  HeterogeneousComputingEnv env;

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

    // Create a random HCE
    env = HeterogeneousComputingEnv.ofRandomUnitary(maxNumTask,
        maxNumExecutors, 
        true);

    // Always crossover
    crossover = new ScheduleCrossover(env, 1);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCrossover() throws Exception {
    ScheduleChromosome p1Chromosome = new ScheduleChromosome(env);
    ScheduleChromosome p2Chromosome = new ScheduleChromosome(env);
    MSeq<ScheduleGene> p1Seq = p1Chromosome.toSeq().copy();
    MSeq<ScheduleGene> p2Seq = p2Chromosome.toSeq().copy();
    int alterations = 0;
    int alterationsCount = 0;

    // Crossover the chromosome
    alterations = crossover.crossover(p1Seq, p2Seq);

    // Check that the Gene sequence changed
    if (!p1Seq.equals(p1Chromosome.toSeq().copy())) {
      alterationsCount++;
    }

    // Check that the Gene sequence changed
    if (!p2Seq.equals(p2Chromosome.toSeq().copy())) {
      alterationsCount++;
    }

    assertEquals(alterationsCount, alterations);
  }



}
