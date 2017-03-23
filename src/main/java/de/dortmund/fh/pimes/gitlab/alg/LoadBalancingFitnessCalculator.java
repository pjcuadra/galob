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

package de.dortmund.fh.pimes.gitlab.alg;

import de.dortmund.fh.pimes.gitlab.alg.util.FitnessCalculator;
import de.dortmund.fh.pimes.gitlab.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.alg.util.Stats;
import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleChromosome;

/**
 * Load balancing fitness calculator.
 * 
 * @author Pedro Cuadra
 *
 */
public class LoadBalancingFitnessCalculator extends FitnessCalculator {
  /**
   * Alpha value to mix fitness functions.
   */
  private double alpha;
  
  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   */
  public LoadBalancingFitnessCalculator(HeterogeneousComputingEnv env) {
    super(env);
    this.alpha = 0.5;
  }

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   * @param alpha fitness function mixing coefficient
   */
  public LoadBalancingFitnessCalculator(HeterogeneousComputingEnv env, double alpha) {
    super(env);
    this.alpha = alpha;
  }

  /* (non-Javadoc)
   * @see alg.util.graph.GraphStatsFactory#getFitness(alg.util.genetics.ScheduleChromosome)
   */
  @Override
  public Double calcFitness(ScheduleChromosome chromosome) {
    assert chromosome != null;
    
    // Get the chromosome's stats
    Stats stats = chromosome.getStats();
    
    return ((1 - alpha) * stats.getTotalTime() + alpha * stats.getStdDev());
  }
}
