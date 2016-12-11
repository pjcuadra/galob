/**
 * Genetic Mutator of ScheduleChromosomes.
 */

package alg.util.genetics;

import alg.util.Util;

import org.jenetics.Alterer;
import org.jenetics.Chromosome;
import org.jenetics.Phenotype;
import org.jenetics.Population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



/**
 * Genetic Mutator of ScheduleChromosomes.
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ScheduleMutator implements Alterer<ScheduleGene, Double> {

  /**
   * Number of tasks.
   */
  private int numTasks;
  /**
   * Mutation probability.
   */
  private double probMutator;
  /**
   * Levels partitioning of dependencies.
   */
  private ArrayList<ArrayList<Integer>> levels;

  /**
   * Constructor.
   * 
   * @param delta dependency matrix
   * @param probMutator mutating probability
   */
  public ScheduleMutator(double[][] delta, double probMutator) {
    this.numTasks = delta.length;
    
    levels =  new ArrayList<ArrayList<Integer>>(); 
    
    getDependenciesLevels(delta);
  }

  /**
   * Create the levels representation of the dependencies.
   * 
   * @param delta dependencies matrix
   */
  private void getDependenciesLevels(double[][] delta) {
    double[][] myDelta = Util.copyMatrix(delta);
    ArrayList<Integer> toDet = new ArrayList<Integer>();
    int numOfOnes = 0;
    ArrayList<Integer> thisLevel;

    for (int i = 0; i < numTasks ;i++) {
      toDet.add(new Integer(i));
    }

    while (!toDet.isEmpty()) {
      thisLevel =  new ArrayList<Integer>();
      
      for (Integer task = 0; task < numTasks; task++) {
        
        if (!toDet.contains(task)) {
          continue;
        }
        
        for (int i = 0; i < numTasks ;i++) {
          /*
           * check if there is a dependency with a successive task
           * check for ones in the column
           */
          numOfOnes += myDelta[i][task];
        }

        if (numOfOnes != 0) {
          continue;
        }
        
        toDet.remove(task);
        
        thisLevel.add(task);
        

        //to clear the elements of the row
        for (int j = 0; j < numTasks; j++) {

          myDelta[task][j] = 0;
        }

      }
      
      levels.add(thisLevel);

    }

  }

  /**
   * Mutate a chromosome by swapping two genes of same dependency level.
   * 
   * @param chromosome chromosome to be mutated
   * @return amount of mutated genes
   */
  private int mutateChromosome(Chromosome<ScheduleGene> chromosome) {
    Random randomGen = new Random();
    int randLevel = randomGen.nextInt(levels.size());
    int firstGeneLocus = 0;
    int secondGeneLocus = 0;
    ScheduleAllele firstAllele;
    ScheduleAllele secondAllele;
    
    if (levels.get(randLevel).size() < 2) {
      return 0;
    }
    
    Integer firstGene = 0;
    Integer secondGene = 0;
    
    // Shuffle and get candidates
    Collections.shuffle(levels.get(randLevel));
    firstGene = levels.get(randLevel).get(0);
    secondGene = levels.get(randLevel).get(1);
    
    // Get the locus of the genes
    for (int i = 0; i < chromosome.toSeq().size(); i++) {
      if (chromosome.toSeq().get(i).getAllele().getTaskId() == firstGene) {
        firstGeneLocus = i;
      }
      
      if (chromosome.toSeq().get(i).getAllele().getTaskId() == secondGene) {
        secondGeneLocus = i;
      }
    }
    
    // Swap genes
    if (secondGeneLocus != firstGeneLocus) {
      
      firstAllele = chromosome.toSeq().get(firstGeneLocus).getAllele();
      secondAllele = chromosome.toSeq().get(secondGeneLocus).getAllele();
      chromosome.toSeq().get(firstGeneLocus).mutate(secondAllele);
      chromosome.toSeq().get(secondGeneLocus).mutate(firstAllele);
      
    }
    
    return 2;
  }

  /* (non-Javadoc)
   * @see org.jenetics.Alterer#alter(org.jenetics.Population, long)
   */
  @Override
  public int alter(Population<ScheduleGene, Double> population, long generation) {
    Random randomGen = new Random();
    int alteredGenes = 0;

    for (Phenotype<ScheduleGene, Double> phenoType: population) {

      // Randomly decide if this individual is going to mutate
      if (randomGen.nextInt(100) > probMutator * 100) {
        continue;
      }

      for (Chromosome<ScheduleGene> chromosome: phenoType.getGenotype().toSeq()) {
        // TODO: Only mutate one chromosome not every
        alteredGenes += mutateChromosome(chromosome);
      }
    }


    return alteredGenes;
  }

}
