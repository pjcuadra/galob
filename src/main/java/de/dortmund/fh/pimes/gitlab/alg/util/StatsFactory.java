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

import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleChromosome;


/**
 * Graph statistics factory.
 * 
 * @author Pedro Cuadra
 *
 */
public class StatsFactory {
  /**
   * Heterogeneous computing environment.
   */
  private HeterogeneousComputingEnv env;
  /**
   * Fitness calculator.
   */
  private FitnessCalculator fitnessCalculator;

  /**
   * Constructor.
   * 
   * @param env graph object
   */
  public StatsFactory(HeterogeneousComputingEnv env, FitnessCalculator fitnessCalculator) {
    this.env = env;
    this.fitnessCalculator = fitnessCalculator;
  }

  /**
   * Create a graph statistics from a given chromosome.
   * 
   * @param chromosome chromosome
   * @return graph statistics
   */
  public Stats ofChromosome(ScheduleChromosome chromosome) {
    
    if (chromosome.getStats() != null) {
      return chromosome.getStats();
    }
    
    int[][] omega = Util.createOmegaMatrix(chromosome.toSeq(), 
        env.getNumberOfExecutors());
    
    Stats stats = new Stats(env, fitnessCalculator, omega);
    
    chromosome.setStats(stats);
    
    return stats;

  }
}
