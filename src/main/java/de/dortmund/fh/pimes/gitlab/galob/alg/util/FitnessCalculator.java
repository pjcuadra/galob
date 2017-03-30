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

import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;

/**
 * Graph statistics factory.
 * 
 * @author Pedro Cuadra
 *
 */
public abstract class FitnessCalculator {
  /**
   * Graph stats factory.
   */
  private StatsFactory statsFactory;

  /**
   * Constructor.
   * 
   * @param env
   *          heterogeneous computing environment
   */
  public FitnessCalculator(HeterogeneousComputingEnv env) {
    setStatsFactory(new StatsFactory(env, this));
  }

  /**
   * Get fitness value of the chromosome.
   * 
   * @param chromosome
   *          chromosome
   * @return fitness value
   */
  public Double getFitness(ScheduleChromosome chromosome) {
    this.getGraphStatsFactory().ofChromosome(chromosome);

    return calcFitness(chromosome);
  }

  /**
   * Calculate fitness function.
   * 
   * @param chromosome
   *          chromosome
   * @return fitness value
   */
  protected abstract Double calcFitness(ScheduleChromosome chromosome);

  /**
   * Get graph stats factory.
   * 
   * @return graph stats factory
   */
  public StatsFactory getGraphStatsFactory() {
    return statsFactory;
  }

  /**
   * Set graph stats factory.
   * 
   * @param gsf
   *          graph stats factory
   */
  private void setStatsFactory(StatsFactory gsf) {
    assert gsf != null;

    this.statsFactory = gsf;
  }

}
