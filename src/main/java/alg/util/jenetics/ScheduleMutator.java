/**
 * Genetic Mutator of ScheduleChromosomes.
 */

package alg.util.jenetics;

import alg.util.HeterogeneousComputingEnv;
import alg.util.SimulatedAnnealing;
import alg.util.Util;

import org.jenetics.Alterer;
import org.jenetics.Chromosome;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
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
public class ScheduleMutator implements Alterer<ScheduleGene, Double> {
  /**
   * Mutation probability.
   */
  private double probMutator;
  /**
   * Levels partitioning of dependencies.
   */
  private ArrayList<ArrayList<Integer>> levels;
  /**
   * Object of simulated anneling.
   */
  private SimulatedAnnealing simAnne;
  /**
   * Flag to indicate if simulated anealing is required.
   */
  private boolean isSimulated;

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   * @param probMutator mutating probability 
   */
  public ScheduleMutator(HeterogeneousComputingEnv env, double probMutator) {
    this.probMutator = probMutator;
    isSimulated = false;
    levels =  Util.getDependenciesLevels(env.getDependencyMatrix()); 
  }

  /**
   * Mutate a chromosome by swapping two genes of same dependency level.
   * 
   * @param chromosome chromosome to be mutated
   * @return mutated chromosome
   */
  public ScheduleChromosome mutateChromosome(ScheduleChromosome chromosome) {
    ScheduleChromosome childChromosome;
    Random randomGen = new Random();
    int randLevel = randomGen.nextInt(levels.size());
    int firstGeneLocus = 0;
    int secondGeneLocus = 0;

    if (levels.get(randLevel).size() < 2) {
      return null;
    }

    Integer firstGene = 0;
    Integer secondGene = 0;

    // Shuffle and get candidates
    Collections.shuffle(levels.get(randLevel));
    firstGene = levels.get(randLevel).get(0);
    secondGene = levels.get(randLevel).get(1);

    // Get the locus of the genes
    for (int i = 0; i < chromosome.length(); i++) {
      if (chromosome.getGene(i).getAllele().getTaskId() == firstGene) {
        firstGeneLocus = i;
      }

      if (chromosome.getGene(i).getAllele().getTaskId() == secondGene) {
        secondGeneLocus = i;
      }
    }

    final MSeq<ScheduleGene> cSeq = chromosome.toSeq().copy();

    // Swap genes
    if (secondGeneLocus != firstGeneLocus) {

      Collections.swap(cSeq.asList(), secondGeneLocus, firstGeneLocus);

      childChromosome = chromosome.newInstance(cSeq.toISeq());

      //check if simulated anealing is required
      if (isSimulated) {
        // use the fitness functions for loadbalancing if the mutation is on loadbalancing 
        if (simAnne.checkCriteria(chromosome, childChromosome)) {
          return childChromosome;
        }
        
        return null;
      }
      
      return childChromosome;

    }
    return null;


  }

  /* (non-Javadoc)
   * @see org.jenetics.Alterer#alter(org.jenetics.Population, long)
   */
  @Override
  public int alter(Population<ScheduleGene, Double> population, long generation) {
    Random randomGen = new Random();
    Genotype<ScheduleGene> genotype;
    int alteredGenes = 0;
    ScheduleChromosome chromosome;

    for (Phenotype<ScheduleGene, Double> phenoType: population) {
      // Randomly decide if this individual is going to mutate
      if (randomGen.nextInt(100) + 0.001 > probMutator * 100) {
        continue;
      }

      genotype = phenoType.getGenotype();

      // Copy the chromosome sequence
      final MSeq<Chromosome<ScheduleGene>> cSeq = genotype.toSeq().copy();

      // Decide randomly which chromosome to mutate
      final int chromosomeIdxToMutate = randomGen.nextInt(genotype.length());

      // Mutate the chromosome
      chromosome = (ScheduleChromosome) genotype.getChromosome(chromosomeIdxToMutate);
      chromosome = mutateChromosome(chromosome);

      // If no mutation was performed then continue
      if (chromosome == null) {
        continue;
      }

      // Modified genes are done in pairs
      alteredGenes += 2;

      // Replace the chromosome with the mutated chromosome
      cSeq.set(chromosomeIdxToMutate, chromosome);

      // Replace the phenotype with the mutated phenotype
      population.set(
          population.indexOf(phenoType),
          phenoType.newInstance(Genotype.of(cSeq.toISeq()), generation));
    }

    return alteredGenes;
  }

}
