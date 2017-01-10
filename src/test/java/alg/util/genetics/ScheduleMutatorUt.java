/**
 * Schedule Mutator unit testing.
 */

package alg.util.genetics;

import static org.junit.Assert.assertEquals;

import alg.ExecutionTime;
import alg.LoadBalancing;
import alg.util.SimulatedAnneling;
import alg.util.Util;

import org.jenetics.Chromosome;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
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
public class ScheduleMutatorUt {

  private Random randomGen;

  private int numTask;
  private int executors;
  static final int maxNumTask = 16 /* Actual max*/;
  static final int maxNumExecutors = 16 /* Actual max*/;
  static final int maxPopulation = 50 /* Actual max*/;
  private ScheduleMutator mutator;
  private double[][] delta;
  
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

    numTask =  1 + randomGen.nextInt(maxNumTask);
    executors =  1 + randomGen.nextInt(maxNumExecutors);
    double[][] etc = Util.getOnesMatrix(executors, numTask);
   
    // Create new dependencies randomly
    delta = Util.getDeltaMatrix(numTask);
    double[][] comCost = Util.getComcostmatrix(delta);
    LoadBalancing loadBal = new LoadBalancing(etc, delta, 0.6, comCost);
    SimulatedAnneling simAnne = new SimulatedAnneling(0.8, 900, loadBal);
   
    // Always mutate probability 1
    mutator = new ScheduleMutator(delta, 0.6, simAnne);

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
  public void singleMutation() {
    ScheduleChromosome originalChromosome = new ScheduleChromosome(delta, executors);
    ScheduleChromosome mutatedChromosome = originalChromosome.clone();
    int alterations = 0;
    int alterationsCount = 0;

    // Mutate the chromosome
    mutatedChromosome = (ScheduleChromosome) mutator.mutateChromosome(originalChromosome);

    if (mutatedChromosome !=  null) {
      alterations = 2;


      alterationsCount = countAltersOfChromosome(originalChromosome, mutatedChromosome);
    }
    assertEquals(alterationsCount, alterations);
    
  }

  @Test
  public void populationMutattion() {
    double[][] myetc = Util.getOnesMatrix(executors, numTask);
    ArrayList<Phenotype<ScheduleGene, Double>> phenoList;
    ExecutionTime myOpt = new ExecutionTime(myetc, delta);
    Phenotype<ScheduleGene, Double> phenoFactory;
    int populationSize;  
    int alterationsCount = 0;

    // Create a phenotype to be used as factory for others
    phenoFactory = Phenotype.of(
        Genotype.of(ScheduleChromosome.of(delta, executors)), 
        0, 
        gt -> myOpt.getFitness(myOpt.ofSeq().decode(gt))
        );

    // Random population size
    populationSize = 1 + randomGen.nextInt(maxPopulation);

    // Create random population
    phenoList = new ArrayList<Phenotype<ScheduleGene, Double>>();
    for (int individualIdx = 0; individualIdx < populationSize; individualIdx++) {
      phenoList.add(
          phenoFactory.newInstance(Genotype.of(new ScheduleChromosome(delta, executors))));
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
}
