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

package de.dortmund.fh.pimes.gitlab.galob.alg;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.FitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.Stats;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;

/**
 * Execution time fitness calculator.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ExecutionTimeFitnessCalculator extends FitnessCalculator {

  /**
   * Constructor.
   * 
   * @param env
   *          heterogeneous computing environment
   */
  public ExecutionTimeFitnessCalculator(HeterogeneousComputingEnv env) {
    super(env);
  }

  /*
   * (non-Javadoc)
   * 
   * @see alg.util.graph.GraphStatsFactory#getFitness(alg.util.genetics.ScheduleChromosome)
   */
  @Override
  public Double calcFitness(ScheduleChromosome chromosome) {

    assert chromosome != null;

    Stats stats = chromosome.getStats();

    return stats.getTotalTime();
  }

}
