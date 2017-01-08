package alg.util.genetics;

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


public class ScheduleCrossover implements Alterer<ScheduleGene, Double> {

  /**
   * Number of tasks.
   */
  private int numTasks;
  /**
   * Crossover probability.
   */
  private double probCrossover;
  /**
   * Levels partitioning of dependencies.
   */
  private ArrayList<ArrayList<Integer>> levels;
  /**
   * The dependency matrix copy.
   */
  private double[][] delta;
  /**
   * Number of altered genes after crossover.
   */
  public int crosssedOvrGenes;

  /**
   * Constructor.
   * 
   * @param delta dependency matrix
   * @param probCrossover crossing over probability
   */
  public ScheduleCrossover(double[][] delta, double probCrossover) {
    this.numTasks = delta.length;
    this.delta = delta;
    

    levels =  new ArrayList<ArrayList<Integer>>(); 

    getDependenciesLevels(delta);
  }

  /**
   * Create the levels representation of the dependencies.
   * 
   * @param delta dependencies matrix
   */
  private void getDependenciesLevels(double[][] delta) {

    ArrayList<Integer> toDet = new ArrayList<Integer>();
    ArrayList<Integer> thisLevel;
    double[][] myDelta = Util.copyMatrix(delta);
    for (int i = 0; i < numTasks ;i++) {
      toDet.add(new Integer(i));
    }

    while (!toDet.isEmpty()) {
      thisLevel =  new ArrayList<Integer>();

      for (Integer task = 0; task < numTasks; task++) {

        if (!toDet.contains(task)) {
          continue;
        }


        /*
         * check if there is a dependency with a successive task
         * check for ones in the column
         */

        if (!(Util.checkColZero(myDelta, task))) {
          continue;
        }

        toDet.remove(task);

        thisLevel.add(task);

      }
      for (Integer iterator:thisLevel) {
        //to clear the elements of the row
        Util.clearRow(myDelta, iterator);
      }

      levels.add(thisLevel);


    }

  }

  /**
   * Return the dependency level of a particular taskId.
   * 
   * @param tasknum taskId 
   * @return tasklevel dependency level of the taskId
   */
  private int getLevel(int tasknum) {
    int tasklevel = 0;
    for (int i = 0; i < delta.length; i++) {
      tasklevel += delta[i][tasknum]; 
    }
    return tasklevel;

  }

  /**
   * Crossover a chromosome with another chromosome.
   * 
   * @param firstParent parent chromosome
   * @param secondParent parent chromosome
   * @return crossedover chromosome
   */
  public Chromosome<ScheduleGene> crossoverChromosome(Chromosome<ScheduleGene> firstParent, 
      Chromosome<ScheduleGene> secondParent) {
    Random randomGen = new Random();
    int randLevel = randomGen.nextInt(levels.size());
    int firstGeneLocus = 0;

    Integer firstGene = 0;

    // Shuffle and get candidates
    Collections.shuffle(levels.get(randLevel));
    firstGene = levels.get(randLevel).get(0);

    // Get the locus of the genes as the crossover point
    for (int i = 0; i < firstParent.length(); i++) {
      if (firstParent.getGene(i).getAllele().getTaskId() == firstGene) {
        firstGeneLocus = i;
      }
    }

    if (firstGeneLocus == 0 || firstGeneLocus >= firstParent.length() - 1) {
      return null;
    }
    //cond1: the tasks immediately before the crossover point must be of same level
    if (getLevel(firstParent.getGene(firstGeneLocus - 1).getAllele().getTaskId()) 
        == getLevel(secondParent.getGene(firstGeneLocus - 1).getAllele().getTaskId())) {

      //cond2: the tasks just after the crossover point must be of different levels
      if (getLevel(firstParent.getGene(firstGeneLocus + 1).getAllele().getTaskId()) 
          != getLevel(secondParent.getGene(firstGeneLocus + 1).getAllele().getTaskId())) {

        final MSeq<ScheduleGene> firstSeq = firstParent.toSeq().copy();
        final MSeq<ScheduleGene> secondSeq = secondParent.toSeq().copy();

        firstSeq.swap(firstGeneLocus, firstSeq.length(), secondSeq, firstGeneLocus);
        crosssedOvrGenes = firstParent.length() - firstGeneLocus;
        return firstParent.newInstance(firstSeq.toISeq());

      }

    }

    return null;

  }

  @Override
  public int alter(Population<ScheduleGene, Double> population, long generation) {
    Random randomGen = new Random();
    Genotype<ScheduleGene> firstGenotype;
    Genotype<ScheduleGene> secondGenotype = null;
    int childChromosomes = 0;
    Chromosome<ScheduleGene> firstChromosome;
    Chromosome<ScheduleGene> secondChromosome;
    Phenotype<ScheduleGene, Double> phenoType;
    int phenoTypeIndex = 0;

    for (phenoTypeIndex = 0; phenoTypeIndex < population.size(); phenoTypeIndex++) {
    
      phenoType = population.get(phenoTypeIndex);
      
      // Randomly decide if the crossover will occur with this individual as firstparent
      if (randomGen.nextInt(100) <= probCrossover * 100) {
        continue;
      }
      firstGenotype = phenoType.getGenotype();
      for (Phenotype<ScheduleGene, Double> phenoType1: population) {
        if (randomGen.nextInt(100) <= (probCrossover + randomGen.nextInt(50)) * 100) {
          continue;
        }

        secondGenotype = phenoType1.getGenotype();
      }

      if (secondGenotype == null || secondGenotype == firstGenotype) {
        continue;
      }
      // Copy the chromosome sequence of first parent
      final MSeq<Chromosome<ScheduleGene>> parentSeq1 = firstGenotype.toSeq().copy();

      // Decide randomly which chromosome to crossover in both parents
      final int chromosomeIdxToCross1 = randomGen.nextInt(firstGenotype.length());
      final int chromosomeIdxToCross2 = randomGen.nextInt(secondGenotype.length());
      // Crossover the chromosomes
      firstChromosome = firstGenotype.getChromosome(chromosomeIdxToCross1);
      secondChromosome = secondGenotype.getChromosome(chromosomeIdxToCross2);
      Chromosome<ScheduleGene> childchromosome 
           = crossoverChromosome(firstChromosome,secondChromosome);

      // If no crossover was performed then continue
      if (childchromosome == null) {
        continue;
      }

      // Child chromosome is added
      childChromosomes += 1;

      // Replace the chromosome with the child chromosome
      parentSeq1.set(chromosomeIdxToCross1, childchromosome);
     
      // Replace the phenotype with the crossed over phenotype
      population.add(phenoType.newInstance(Genotype.of(parentSeq1.toISeq())));
    }

    return childChromosomes;
  }

}

