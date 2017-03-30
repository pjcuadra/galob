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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;

public class StatsFactoryTest {
  /**
   * Max. number of tasks.
   */
  static final int MAX_NUM_TASKS = 16;
  /**
   * Max. number cores.
   */
  static final int MAX_NUM_CORES = 16;
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testOfChromosome() throws Exception {
    HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandomUnitary(MAX_NUM_TASKS, 
        MAX_NUM_CORES, 
        true);
    ScheduleChromosome chromosome = new ScheduleChromosome(env);
    LoadBalancingFitnessCalculator lbFitnessCalc = new LoadBalancingFitnessCalculator(env, 0);
    
    // Create the factory
    StatsFactory sf = new StatsFactory(env, lbFitnessCalc);
    
    // Create a stat instance
    Stats stat = sf.ofChromosome(chromosome);
    
    // Check that the stats were added to the chromosome
    assertEquals(stat, chromosome.getStats());
    
    // And viceversa
    assertEquals(chromosome, stat.getChromosome());
    
    // Try to create a new stat for the same chromosome
    Stats newStat = sf.ofChromosome(chromosome);
    
    // Stats shall be the same
    assertEquals(stat, newStat);
    
    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Chromosome is null");
    
    // This shall throw an exception
    sf.ofChromosome(null);

  }

}
