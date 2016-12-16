package alg.util.genetics;

import alg.util.Util;
import alg.util.genetics.ScheduleChromosome;

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
   * Number of tasks.
   */
  private int numTasks;
  /**
   * Number of executing units.
   */
  private int numExecutors;
  /**
   * Dependencies matrix, delta.
   */
  private double[][] delta;


  /**
   * Constructor.
   * 
   * @param delta dependency matrix, delta
   * @param numExecutors number of executing units
   */
  public ScheduleChromosome(double[][] delta, int numExecutors) {
    ArrayList<ScheduleAllele> toSchedule = new ArrayList<ScheduleAllele>();
    ArrayList<ScheduleGene> myList = new ArrayList<ScheduleGene>();
    ArrayList<ScheduleAllele> allocable = null;
    ScheduleGene gene =  null;
    boolean clean = false;

    gene =  ScheduleGene.of(delta.length, numExecutors);

    this.delta = Util.copyMatrix(delta);

    this.numTasks = delta.length;
    this.numExecutors = numExecutors;

    // Create a gene to use it as factory


    // Initialize the alleles list
    for (int i = 0; i < numTasks; i++) {
      toSchedule.add(new ScheduleAllele(numTasks, numExecutors, i));
    }

    // Finish until everything is allocated (assuming no cycles)
    while (!toSchedule.isEmpty()) {
      // New allocable list
      allocable =  new ArrayList<ScheduleAllele>();

      for (ScheduleAllele currItem: toSchedule) {
        // If no dependency pending is allocable
        clean = true;
        if (!(Util.checkColZero(this.delta, currItem.getTaskId()))) {
          clean = false;
        }

        if (!clean) {
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
      Util.clearRow(this.delta, allocable.get(0).getTaskId());
    }


    // Convert to Sequence
    scheduleSeq = ISeq.of(myList);

  }

  /**
   * Constructor.
   * 
   * @param delta dependency matrix, delta
   * @param numExecutors number of executing units
   * @param genes sequence of already created genes
   */
  public ScheduleChromosome(double[][] delta, int numExecutors, ISeq<ScheduleGene> genes) {

    this.delta = delta;
    this.numTasks = delta.length;
    this.numExecutors = numExecutors;

    scheduleSeq = genes;
  }

  /* (non-Javadoc)
   * @see org.jenetics.util.Verifiable#isValid()
   */
  @Override
  public boolean isValid() {

    int numtasks = delta.length; //square matrix with rows=cols=num of tasks
    double[][]tempmat = new double[numtasks][numtasks];
    int numofones = 0;

    tempmat = Util.copyMatrix(delta);

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
      for (int j = 0; j < numtasks; j++) {

        tempmat[gene.getAllele().getTaskId()][j] = 0;
      }

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
  public Chromosome<ScheduleGene> newInstance() {
    return new ScheduleChromosome(delta, numExecutors);

  }

  /* (non-Javadoc)
   * @see org.jenetics.Chromosome#newInstance(org.jenetics.util.ISeq)
   */
  @Override
  public Chromosome<ScheduleGene> newInstance(ISeq<ScheduleGene> genes) {
    return new ScheduleChromosome(delta, numExecutors, genes);
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
   * @param delta dependency matrix, delta
   * @param numExecutors number of executing units
   * @return newly created chromosome
   */
  public static Chromosome<ScheduleGene> of(double[][] delta, int numExecutors) {
    return new ScheduleChromosome(delta, numExecutors);
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
   * @return chromosome copy
   */
  public ScheduleChromosome clone() {
    ArrayList<ScheduleGene> genesList = new ArrayList<ScheduleGene>();

    for  (ScheduleGene gene : scheduleSeq) {
      genesList.add(gene.newInstance(gene.getAllele()));
    }

    return new ScheduleChromosome(delta, numExecutors, ISeq.of(genesList));

  }

}