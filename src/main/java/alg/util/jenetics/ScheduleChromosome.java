package alg.util.jenetics;

import alg.util.HeterogeneousComputingEnv;
import alg.util.Stats;
import alg.util.Util;
import alg.util.graph.Graph;
import alg.util.jenetics.ScheduleChromosome;

import org.jenetics.Chromosome;
import org.jenetics.util.ISeq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Genetic algorithm's chromosome.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 * 
 */
public class ScheduleChromosome implements Chromosome<ScheduleGene> {
  /**
   * Scheduling sequence of ScheduleItem's genes.
   */
  public ISeq<ScheduleGene> scheduleSeq;
  /**
   * Heterogeneous computing environment.
   */
  private HeterogeneousComputingEnv env;
  /**
   * The statistics of the chromosome.
   */
  private Stats stats;

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   */
  public ScheduleChromosome(HeterogeneousComputingEnv env) {
    ArrayList<ScheduleAllele> toSchedule = new ArrayList<ScheduleAllele>();
    ArrayList<ScheduleGene> myList = new ArrayList<ScheduleGene>();
    ArrayList<ScheduleAllele> allocable = null;
    ScheduleGene gene =  null;
    this.env = env;
    double[][] deltaTemp =  env.getDependencyMatrix();
    
    // Check for cycles first!
    if (env.checkCycles()) {
      throw new Graph.CycleException();
    }
    
    // Create a gene to use it as factory
    gene =  ScheduleGene.of(env.getNumberOfTasks(), env.getNumberOfExecutors());
    
    // Initialize the alleles list
    for (int i = 0; i < env.getNumberOfTasks(); i++) {
      toSchedule.add(new ScheduleAllele(env.getNumberOfTasks(), env.getNumberOfExecutors(), i));
    }

    // Finish until everything is allocated (assuming no cycles)
    while (!toSchedule.isEmpty()) {
      // New allocable list
      allocable =  new ArrayList<ScheduleAllele>();

      for (ScheduleAllele currItem: toSchedule) {
        // If no dependency pending is allocable
        if (!(Util.checkColZero(deltaTemp, currItem.getTaskId()))) {
          continue;
        }

        // Add to allocable list
        allocable.add(currItem);

      }

      // Randomly shuffle the allocable domain
      Collections.shuffle(allocable);

      // Add first element to the sequence
      myList.add(gene.newInstance(allocable.get(0)));

      // Remove from unallocated list
      toSchedule.remove(allocable.get(0));

      // Set dependency for all other task to zero
      Util.clearRow(deltaTemp, allocable.get(0).getTaskId());
    }


    // Convert to Sequence
    scheduleSeq = ISeq.of(myList);

  }

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   * @param genes sequence of already created genes
   */
  public ScheduleChromosome(HeterogeneousComputingEnv env, ISeq<ScheduleGene> genes) {

    this.env = env;

    scheduleSeq = genes;

  }
  
  /**
   * Constructor.
   * 
   * @param chromosome already existing chromosome
   */
  public ScheduleChromosome(ScheduleChromosome chromosome) {

    this.env = chromosome.env;

    scheduleSeq = chromosome.toSeq().copy().toISeq();

  }
  
  /* (non-Javadoc)
   * @see org.jenetics.util.Verifiable#isValid()
   */
  @Override
  public boolean isValid() {

    int numtasks = env.getNumberOfTasks();
    double[][]tempmat = new double[numtasks][numtasks];
    int numofones = 0;

    tempmat = Util.copyMatrix(env.getDependencyMatrix());

    //only execute as long as order is valid
    for (ScheduleGene gene: scheduleSeq) {
      for (int i = 0; i < numtasks ;i++) {
        /*
         * check if there is a dependency with a successive task
         * check for ones in the column
         */
        numofones += tempmat[i][gene.getAllele().getTaskId()];
      }

      if (numofones != 0) {
        return false;
      }

      //to clear the elements of the row
      Util.clearRow(tempmat, gene.getAllele().getTaskId());

      tempmat[gene.getAllele().getTaskId()][gene.getAllele().getTaskId()] = 1;
    }

    return true;
  }

  

  /* (non-Javadoc)
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<ScheduleGene> iterator() {
    return scheduleSeq.iterator();
  }



  /* (non-Javadoc)
   * @see org.jenetics.util.Factory#newInstance()
   */
  @Override
  public ScheduleChromosome newInstance() {
    return new ScheduleChromosome(env);

  }

  /* (non-Javadoc)
   * @see org.jenetics.Chromosome#newInstance(org.jenetics.util.ISeq)
   */
  @Override
  public ScheduleChromosome newInstance(ISeq<ScheduleGene> genes) {
    return new ScheduleChromosome(env, genes);
  }

  /* (non-Javadoc)
   * @see org.jenetics.Chromosome#getGene(int)
   */
  @Override
  public ScheduleGene getGene(int index) {
    return scheduleSeq.get(index);
  }

  /* (non-Javadoc)
   * @see org.jenetics.Chromosome#length()
   */
  @Override
  public int length() {
    return scheduleSeq.length();
  }

  /* (non-Javadoc)
   * @see org.jenetics.Chromosome#toSeq()
   */
  @Override
  public ISeq<ScheduleGene> toSeq() {
    return scheduleSeq;
  }

  /**
   * Static constructor.
   * 
   * @param env heterogeneous computing environment
   * @return newly created chromosome
   */
  public static ScheduleChromosome of(HeterogeneousComputingEnv env) {
    return new ScheduleChromosome(env);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return scheduleSeq.toString();
  }

  /**
   * Create an exact copy of the chromosome.
   * 
   * @return chromosome copy
   */
  public ScheduleChromosome clone() {
    ArrayList<ScheduleGene> genesList = new ArrayList<ScheduleGene>();

    for  (ScheduleGene gene : scheduleSeq) {
      genesList.add(gene.newInstance(gene.getAllele()));
    }

    return new ScheduleChromosome(env, ISeq.of(genesList));

  }
  
  /**
   * Set statistics object.
   * 
   * @param stats statistics object
   */
  public void setStats(Stats stats) {
    this.stats = stats;
  }
  
  /**
   * Get statistics object.
   * 
   * @return return statistics object
   */
  public Stats getStats() {
    return this.stats;
  }
  
  /**
   * Check if the chromosome has statistics set.
   *  
   * @return true if the chromosome has statistics set false otherwise
   */
  public boolean hasStats() {
    return (this.stats != null);
  }

}