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

import de.dortmund.fh.pimes.gitlab.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.alg.util.graph.GraphNode;

import org.jenetics.SwapMutator;
import org.jenetics.util.MSeq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Genetic Mutator of ScheduleChromosomes.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ScheduleMutator extends SwapMutator<ScheduleGene, Double> {
  /**
   * Heterogeneous computing environment.
   */
  private HeterogeneousComputingEnv env;

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   * @param probMutator mutating probability 
   */
  public ScheduleMutator(HeterogeneousComputingEnv env, double probMutator) {
    super(probMutator);
    this.env = env;
  }

  /* (non-Javadoc)
   * @see org.jenetics.SwapMutator#mutate(org.jenetics.util.MSeq, double)
   */
  @Override
  public int mutate(final MSeq<ScheduleGene> genes, final double prob) {
    Random randomGen = new Random();
    
    // If probability not meet return and don't mutate
    if (randomGen.nextDouble() < prob) {
      return 0;
    }
    
    int randLevel = randomGen.nextInt(env.getMaxTopologicalLevel());
    int firstGeneLocus = 0;
    int secondGeneLocus = 0;
    ArrayList<GraphNode> level = env.getTologicalLevelNodes(randLevel);

    if (level.size() < 2) {
      return 0;
    }

    // Shuffle and get candidates
    Collections.shuffle(level);
    final Integer firstGene = level.get(0).getTaskId();
    final Integer secondGene = level.get(1).getTaskId();
    
    // Get the locus of the genes
    for (int i = 0; i < genes.size(); i++) {
      if (genes.get(i).getAllele().getTaskId() == firstGene) {
        firstGeneLocus = i;
      }

      if (genes.get(i).getAllele().getTaskId() == secondGene) {
        secondGeneLocus = i;
      }
    }
    
    // Apply mutation
    if (secondGeneLocus != firstGeneLocus) {
      ScheduleChromosome chrFac = new ScheduleChromosome(env);
      
      ScheduleChromosome oldChr = chrFac.newInstance(genes.toISeq().copy().toISeq());
      //
      genes.swap(firstGeneLocus, secondGeneLocus);
      
      ScheduleChromosome newChr = chrFac.newInstance(genes.toISeq());
      //
      //check if simulated annealing is required
      if (env.getSimulatedAnnealingEnabled()) {
        if (env.getSimulatedAnnealing().checkCriteria(oldChr, newChr)) {
          return 2;
        }
        
        // If criteria not meet unswap genes
        genes.swap(firstGeneLocus, secondGeneLocus);
        return 0;
      }

      return 2;
    }

    return 2;
    
  }


}
