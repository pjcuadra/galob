/**
 * Scheduler class abstraction.
 */

package alg.util;

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

}
