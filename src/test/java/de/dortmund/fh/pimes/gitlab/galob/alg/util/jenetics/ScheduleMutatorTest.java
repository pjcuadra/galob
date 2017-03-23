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

import de.dortmund.fh.pimes.gitlab.galob.alg.ExecutionTimeFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.SimulatedAnnealing;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleCodec;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleGene;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleMutator;

import org.jenetics.Chromosome;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.MSeq;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * Schedule Mutator unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ScheduleMutatorTest {

  private Random randomGen;

  static final int maxNumTask = 16 /* Actual max*/;
  static final int maxNumExecutors = 16 /* Actual max*/;
  static final int maxPopulation = 50 /* Actual max*/;
  private ScheduleMutator mutator;
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
    
    randomGen = new Random();

    env = HeterogeneousComputingEnv.ofRandomUnitary(maxNumTask, 
        maxNumExecutors, 
        false);
    
    LoadBalancingFitnessCalculator loadBal = new LoadBalancingFitnessCalculator(env, 0.6);
    SimulatedAnnealing simAnne = new SimulatedAnnealing(0.8, 900, loadBal);
    
    env.setSimulatedAnnealing(simAnne);
   
    // Always mutate probability 1
    mutator = new ScheduleMutator(env, 0.6);

  }

  @After
  public void tearDown() throws Exception {
  }

  private int countAltersOfChromosome(Chromosome<ScheduleGene> originalChromosome, 
      Chromosome<ScheduleGene> mutatedChromosome) {
    int alterationsCount = 0;
    ScheduleGene expected;
    ScheduleGene actual;
    // Count number of alterations
    for (int locus = 0; locus < originalChromosome.toSeq().size(); locus++) {

      expected =  originalChromosome.getGene(locus);
      actual =  mutatedChromosome.getGene(locus);

      if (!expected.equals(actual)) {
        alterationsCount++;
      }

    }

    return alterationsCount;
  }

  @Test
  public void populationMutattion() {
    ArrayList<Phenotype<ScheduleGene, Double>> phenoList;
    
    ExecutionTimeFitnessCalculator myOpt = new ExecutionTimeFitnessCalculator(env);
    ScheduleCodec codec = new ScheduleCodec(env);
    Phenotype<ScheduleGene, Double> phenoFactory;
    int populationSize;  
    int alterationsCount = 0;

    // Create a phenotype to be used as factory for others
    phenoFactory = Phenotype.of(
        Genotype.of(ScheduleChromosome.of(env)), 
        0, 
        gt -> myOpt.getFitness(codec.ofChromosome().decode(gt))
        );

    // Random population size
    populationSize = 1 + randomGen.nextInt(maxPopulation);

    // Create random population
    phenoList = new ArrayList<Phenotype<ScheduleGene, Double>>();
    for (int individualIdx = 0; individualIdx < populationSize; individualIdx++) {
      phenoList.add(
          phenoFactory.newInstance(Genotype.of(new ScheduleChromosome(env))));
    }

    Population<ScheduleGene, Double> originalPopulation = Population.empty();
    originalPopulation.addAll(phenoList);

    // Copy population
    Population<ScheduleGene, Double> mutatedPopulation = originalPopulation.copy();

    // Call the mutator
    int alterations = mutator.alter(mutatedPopulation, 1);

    // Count alterations of all chromosomes of all individuals
    for (int individualIdx = 0; individualIdx < originalPopulation.size(); individualIdx++) {

      // Remember that a genotype might have more than one chromosome
      int chromosomeSize = originalPopulation.get(individualIdx).getGenotype().length();
      for (int chromosomeIdx = 0; chromosomeIdx < chromosomeSize; chromosomeIdx++) {
        alterationsCount += countAltersOfChromosome(
            originalPopulation.get(individualIdx).getGenotype().getChromosome(chromosomeIdx),
            mutatedPopulation.get(individualIdx).getGenotype().getChromosome(chromosomeIdx)
            );
      }
    }

    // Verify the alterations match
    assertEquals(alterationsCount, alterations);
  }

  @Test
  public void testMutate() throws Exception {
    ScheduleChromosome originalChromosome = new ScheduleChromosome(env);
    MSeq<ScheduleGene> mutationSeq = originalChromosome.toSeq().copy();
    int alterations = 0;
    int alterationsCount = 0;

    // Mutate the chromosome
    alterations = mutator.mutate(mutationSeq, 1);
    
    // Create mutated chromosome
    ScheduleChromosome mutatedChromosome = originalChromosome.newInstance(mutationSeq.toISeq());

    // Count alterations
    alterationsCount = countAltersOfChromosome(originalChromosome, mutatedChromosome);
    
    assertEquals(alterationsCount, alterations);
  }
}
