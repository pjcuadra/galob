/**
 * Scheduler class abstraction.
 */

package alg.util;

import alg.util.genetics.ScheduleAllele;
import alg.util.genetics.ScheduleChromosome;
import alg.util.genetics.ScheduleGene;

import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

import java.util.ArrayList;

/**
 * Scheduler class abstraction.
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public abstract class Scheduler {
  /**
   * Execution Time matrix with ETC[i][j] where i is the
   * node and j is the task index. 
   */ 
  protected double[][] etc;

  /**
   * Dependency matrix, where i is independent task ID and j 
   * dependent task ID and delta[i][j] is the communication cost.
   */

  protected double[][] delta;

  /**
   * Communication costs matrix.
   */

  protected double[][] comCost;

  /**
   * Constructor.
   * 
   * @param etc execution time matrix
   * @param delta dependency matrix
   * @param comCost communication cost matrix
   */
  public Scheduler(double[][] etc, double[][] delta, double[][] comCost) {
    this.etc = etc;
    this.delta = delta;
    this.comCost = comCost;
  }

  /**
   * Constructor.
   * 
   * @param etc execution time matrix
   * @param delta dependency matrix
   */
  public Scheduler(double[][] etc, double[][] delta) {
    this.etc = etc;
    this.delta = delta;
    this.comCost = Util.createEmptyMatrix(delta.length, delta.length);
  }

  /**
   * Calculate the omega matrix from a Chromosome.
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun) 
   * 
   * @param scheduleSeq genes sequence of a valid chromosome
   * 
   * @return CONV matrix
   */
  public int[][] createOmegaMatrix(ISeq<ScheduleGene> scheduleSeq) { 
    int[][] omega = new int[etc.length][etc[0].length];
    ScheduleAllele currAllel = null;

    // Just set to one where it is allocated
    for (ScheduleGene gene: scheduleSeq) {
      currAllel = gene.getAllele();
      omega[currAllel.getExecutorId()][currAllel.getTaskId()] = 1;
    }

    return omega;
  }

  /**
   * abstract method for the fitness functions.
   * @param scheduleSeq schedule sequence of the chromosome.
   */
  public abstract double getFitness(ISeq<ScheduleGene> scheduleSeq);

  /**
   * Get the execution time of every node given a chromosome.
   * Get the execution time of every node given a chromosome
   * by simulating the execution of the schedule sequence.
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (1)
   * 
   * @param scheduleSeq scheduling sequence
   * 
   * @return the sum of execution times per node as an array
   *         indexed by node index 
   */
  public double[] getNodesExecutionTime(ISeq<ScheduleGene> scheduleSeq) {
    double[] sumTime = new double[etc.length];
    double currTime = 0;
    MSeq<ScheduleGene> toRun = scheduleSeq.copy();
    double[][] deltaTemp = Util.copyMatrix(delta);
    double[][] comCostTemp = Util.copyMatrix(comCost);
    int currGeneIndx = 0;
    ArrayList<ScheduleGene> doneTasks = new ArrayList<ScheduleGene>(); 
    ArrayList<ScheduleGene> executed = new ArrayList<ScheduleGene>();
    ArrayList<Double> finishTime = new ArrayList<Double>();
    ScheduleChromosome test =  new ScheduleChromosome(delta, etc.length, scheduleSeq);
    ScheduleGene currGene = null;

    // Check if the sequence is correct
    if (!test.isValid()) {
      for (int idx = 0; idx < sumTime.length; idx ++) {
        sumTime[idx] = Integer.MAX_VALUE;      
      }
      return sumTime;
    }


    // Set to zero communication costs because of same node allocation
    Util.allocComCost(comCostTemp, createOmegaMatrix(scheduleSeq));


    // Time passing loop
    while (currGeneIndx < toRun.length()) {

      // Allocating loop
      while (currGeneIndx < toRun.length()) {

        currGene = toRun.get(currGeneIndx);

        // If dependencies aren't met time must pass
        if (!(Util.checkColZero(deltaTemp, currGene.getAllele().getTaskId()))) {
          break;
        }

        // If communication isn't finished time must pass
        if (!(Util.checkColZero(comCostTemp, currGene.getAllele().getTaskId()))) {
          break;
        }

        // Execute task on node
        if (currTime >= sumTime[currGene.getAllele().getExecutorId()]) {

          sumTime[currGene.getAllele().getExecutorId()] = currTime;
          sumTime[currGene.getAllele().getExecutorId()] += etc[currGene.getAllele().getExecutorId()]
              [currGene.getAllele().getTaskId()];

          currGeneIndx++;

          // Add to both list
          executed.add(currGene);
          finishTime.add(new Double(sumTime[currGene.getAllele().getExecutorId()]));

          continue;

        }

        break;

      }


      // Check if any executing task is done
      for (int idx = 0; idx < finishTime.size(); idx++) {

        if (currTime >= finishTime.get(idx)) {
          Util.clearRow(deltaTemp, executed.get(idx).getAllele().getTaskId());

          doneTasks.add(executed.get(idx));

          // Remove from both lists
          executed.remove(idx);
          finishTime.remove(idx);

        }
      }

      // Increase communication time
      for (ScheduleGene gene: doneTasks) {
        Util.decrementRow(comCostTemp, gene.getAllele().getTaskId());
      }

      currTime++;

    }

    return sumTime;
  }


  /**
   * Get total execution time given a Chromosome.
   * According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (2)
   * 
   * @param scheduleSeq scheduling sequence
   * 
   * @return total execution time of a given Chromosome
   */
  public double getTotalTime(ISeq<ScheduleGene> scheduleSeq) {
    double[] sumTime = getNodesExecutionTime(scheduleSeq);
    double totalTime = 0;

    for (double time : sumTime) {
      if (time > totalTime) {
        totalTime = time;
      }
    }

    return totalTime;
  }

  /**
   * Get average execution time given a Chromosome.
   * According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (2)
   * 
   * @param scheduleSeq scheduling sequence
   * 
   * @return average execution time of a given Chromosome
   */
  public double getAverageTime(ISeq<ScheduleGene> scheduleSeq) {
    double[] sumTime = getNodesExecutionTime(scheduleSeq);
    double totalTime = 0;

    for (double time : sumTime) {

      totalTime += time;

    }

    return totalTime / sumTime.length;
  }

  /**
   * Get the communication cost given the chromosome.
   * According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (2)
   * 
   * @param scheduleSeq genes sequence of a valid chromosome  
   * 
   * @return total communication cost of a given Chromosome
   */

  public double getCommCost(ISeq<ScheduleGene> scheduleSeq) {
    int execId2 = 0;
    int execId1 = 0;
    double totalComCost = 0;

    if (comCost == null) {
      return 0;
    }

    for (int i = 0; i < scheduleSeq.length(); i++) {
      for (int j = (i + 1); j < scheduleSeq.length(); j++) {
        //iterate only if there is a dependency between the tasks
        if (comCost[i][j] != 0) {
          for (ScheduleGene gene : scheduleSeq) {
            if (gene.getAllele().getTaskId() == i) {
              execId1 = gene.getAllele().getExecutorId();
            } else if (gene.getAllele().getTaskId() == j) {
              execId2 = gene.getAllele().getExecutorId();
            }
          }
          if (execId1 != execId2) {
            //add the comm cost if the tasks are assigned to diff cores
            totalComCost += comCost[i][j];
          }
        }
      }

    }

    return totalComCost;
  }

  /**
   * Create a Jenetics codec for IntegerChromosome/Conv matrix encoding/decoding.
   * @return Jenetics codec
   */
  public Codec<int[][], ScheduleGene> ofOmega() {
    int numExecutors = etc.length;

    return Codec.of(
        Genotype.of(ScheduleChromosome.of(delta, numExecutors)), /*Encoder*/ 
        gt -> createOmegaMatrix(((ScheduleChromosome)gt.getChromosome()).toSeq()) /*Decoder*/
        );
  }

  /**
   * Create a Jenetics codec for IntegerChromosome/schedule sequence encoding/decoding.
   * @return Jenetics codec
   */
  public Codec<ISeq<ScheduleGene>, ScheduleGene> ofSeq() {
    int numExecutors = etc.length;

    return Codec.of(
        Genotype.of(ScheduleChromosome.of(delta, numExecutors)), /*Encoder*/ 
        gt -> ((ScheduleChromosome)gt.getChromosome()).toSeq() /*Decoder*/
        );
  }


}