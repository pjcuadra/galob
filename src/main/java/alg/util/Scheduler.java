/**
 * Scheduler class abstraction.
 */

package alg.util;

import alg.util.genetics.ScheduleAllele;
import alg.util.genetics.ScheduleGene;

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
   * Constructor.
   * 
   * @param etc execution time matrix
   * @param delta dependency matrix
   */
  public Scheduler(double[][] etc, double[][] delta) {
    this.etc = etc;
    this.delta = delta;
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


}
