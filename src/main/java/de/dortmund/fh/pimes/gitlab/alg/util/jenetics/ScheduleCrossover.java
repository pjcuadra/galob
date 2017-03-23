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

import static java.lang.Math.min;

import de.dortmund.fh.pimes.gitlab.alg.util.HeterogeneousComputingEnv;

import org.jenetics.SinglePointCrossover;
import org.jenetics.util.MSeq;

import java.util.Random;

/**
 * Schedule crossover operation class.
 * 
 * @author Pedro Cuadra
 *
 */
public class ScheduleCrossover extends SinglePointCrossover<ScheduleGene, Double> {
  /**
   * Heterogeneous computing environment.
   */
  private HeterogeneousComputingEnv env;

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   * @param probCrossover crossing over probability
   */
  public ScheduleCrossover(HeterogeneousComputingEnv env, double probCrossover) {
    super(probCrossover);
    this.env = env;
  }

  /* (non-Javadoc)
   * @see org.jenetics.SinglePointCrossover#crossover(org.jenetics.util.MSeq,
   * org.jenetics.util.MSeq)
   */
  @Override
  public int crossover(MSeq<ScheduleGene> that, MSeq<ScheduleGene> other) {
    Random randomGen = new Random();
    MSeq<ScheduleGene> tempother = other.copy();
    MSeq<ScheduleGene> tempthat = that.copy();
    int crossoverSiteLocus = randomGen.nextInt(min(that.length(), other.length()));
    ScheduleChromosome chrFac = new ScheduleChromosome(env);
    int modified = 0;
    int thatTopLevel = env.getNodeTopologicalLevel(that
        .get(crossoverSiteLocus)
        .getAllele()
        .getTaskId());
    int otherTopLevel = env.getNodeTopologicalLevel(other
        .get(crossoverSiteLocus)
        .getAllele()
        .getTaskId());


    //cond1: the tasks immediately before the crossover point must be of same level
    if (thatTopLevel != otherTopLevel) {
      return modified;
    }

    that.swap(crossoverSiteLocus, min(that.length(), other.length()), other, crossoverSiteLocus);

    if ((!that.equals(tempthat)) && (!that.equals(other)) && (!other.equals(tempother))) {
      if (env.getSimulatedAnnealingEnabled()) {
        // temp: parent sequence, that : child sequemce
        if (env.getSimulatedAnnealing().checkCriteria(chrFac.newInstance(tempthat.toISeq()), 
            chrFac.newInstance(that.toISeq()))) {
          modified++;
        } else {
          // unswap: return the original chromosome as the criteria failed.
          that = tempthat.copy();
        }

        if (env.getSimulatedAnnealing().checkCriteria(chrFac.newInstance(tempother.toISeq()), 
            chrFac.newInstance(other.toISeq()))) {
          modified++;
        } else {
          // unswap: return the original chromosome as the criteria failed.
          other = tempother.copy();
        }
      } else {
        modified = 2;
      }
      
    }
      
    return modified;    
  }

}

