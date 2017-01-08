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

/**
 * Scheduler class abstraction.
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class Scheduler {
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
   * Communication costs matrix 
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
    this.comCost = null;
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
  public double[] getNodesExecutionTime(int[][] omega) {
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
    double[] sumTime = getNodesExecutionTime(omega);
    double totalTime = 0;
    
    for (double time : sumTime) {
      if (time > totalTime) {
        totalTime = time;
      }
    }

    return totalTime;
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
