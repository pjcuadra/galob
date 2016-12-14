/*
 * This class should implement all the necessary methods to implement
 * Load Balancing Task Scheduling based on Multi-Population Genetic in
 * Cloud Computing (Wang Bei, LI Jun). Note that this is not actually
 * taking into account the load balancing we try to achieve but the final
 * result will be built on top of this. Also we are limiting the
 * implementation to a single population.
 *
 * Author:
 *    Pedro Cuadra
 *    Sudheera Bandi
 */

package alg;

import alg.util.Scheduler;
import alg.util.Util;
import alg.util.genetics.ScheduleAllele;
import alg.util.genetics.ScheduleChromosome;
import alg.util.genetics.ScheduleGene;

import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.ISeq;
/**
 * Scheduler representation.
 * @author Pedro Cuadra
 *
 */

public class ExecutionTime extends Scheduler {
  static final double costFact = 0.5;
  static final double loadFact = 0.5;
  /**
   * Constructor.
   * @param etcmatrix Execution times matrix
   */

  public ExecutionTime(double[][] etcmatrix, double[][] delta) {
    super(etcmatrix, delta);
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
   * Get the execution time of every node given a chromosome.
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (1)
   * 
   * @param omega allocation nodes matrix
   * 
   * @return the sum of execution times per node as an array
   *         indexed by node index 
   */
  public double[] getSumTime(int[][] omega) {
    double[][] costsMatrix = null;
    double[] sumTime = new double[omega.length];
    int row = 0;

    // Get the execution costs for our allocation
    costsMatrix = Util.matrixParallelMultiply(Util.intMatrixtoDouble(omega), etc);

    // Iterate over the tasks
    for (row = 0; row < omega.length; row++) {
      sumTime[row] = Util.getRowSum(costsMatrix, row);
    }

    return sumTime;
  }

  /**
   * Get total execution time given a Chromosome.
   * According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (2)
   * 
   * @param omega allocation nodes matrix
   * 
   * @return total execution time of a given Chromosome
   */
  public double getTotalTime(int[][] omega) {
    double[] sumTime = getSumTime(omega);
    double totalTime = 0;

    for (double time : sumTime) {
      if (time > totalTime) {
        totalTime = time;
      }
    }

    return totalTime;
  }

  /**
   * The fitness function to calculate fitness of a given Chromosome
   * with respect to only computation costs. 
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (7)
   * 
   * @param  scheduleSeq the schedule sequence of the chromosome    
   * @return fitness of a given Chromosome
   */
  public double getFitnessCost(ISeq<ScheduleGene> scheduleSeq) {
    double fitness = 0;
    int[][] omega = createOmegaMatrix(scheduleSeq);
    double totaltime = getTotalTime(omega);
    double[][] comCostMat = Util.getComcostmatrix(delta);
    double totalComCost = getCommCost(comCostMat, scheduleSeq);

    fitness = (costFact * (totaltime + totalComCost));

    return fitness;
  }



  /**
   * Get the load imbalance of given Chromosome.
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (4)
   * 
   * @param omega allocation nodes matrix
   * 
   * @return load imbalance of a Chromosome
   */
  public double getLoad(int[][] omega) {
    double[] sumTime = getSumTime(omega);
    double load = 0;
    double avgTime = getTotalTime(omega) / omega.length;

    // First calculate sum = (sumTime(i) - averageTime)^2
    for (double time: sumTime) {
      load += Math.pow(time - avgTime, 2);
    }

    // Now multiply the sum with 1/(M - 1)
    load = load / ((double)(sumTime.length - 1));

    // And finally take the square root
    load = Math.sqrt(load);

    return load;
  }

  /**
   * Get the communication cost given the chromosome.
   * According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (2)
   * 
   * @param ctmatrix communication cost matrix
   * @param scheduleSeq genes sequence of a valid chromosome  
   * 
   * @return total communication cost of a given Chromosome
   */

  public double getCommCost(double[][] ctmatrix, ISeq<ScheduleGene> scheduleSeq) {
    int execId2 = 0;
    int execId1 = 0;
    double totalComCost = 0;


    for (int i = 0; i < scheduleSeq.length(); i++) {
      for (int j = (i + 1); j < scheduleSeq.length(); j++) {
        //iterate only if there is a dependency between the tasks
        if (ctmatrix[i][j] != 0) {
          for (ScheduleGene gene : scheduleSeq) {
            if (gene.getAllele().getTaskId() == i) {
              execId1 = gene.getAllele().getExecutorId();
            } else if (gene.getAllele().getTaskId() == j) {
              execId2 = gene.getAllele().getExecutorId();
            }
          }
          if (execId1 != execId2) {
            //add the comm cost if the tasks are assigned to diff cores
            totalComCost += ctmatrix[i][j];
          }
        }
      }

    }

    return totalComCost;
  }
}
