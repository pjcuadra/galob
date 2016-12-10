/**
 * 
 */
package alg.util;

/**
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class Scheduler {

	public Scheduler(double[][] ETCMatrix, int[][] delta) {
		ETC = ETCMatrix;
		this.delta = delta;
	}


	/**
	 * Execution Time matrix with ETC[i][j] where i is the
	 * node and j is the task index. 
	 */ 
	protected double[][] ETC;

	/**
	 * Dependency matrix, where i is independent task ID and j 
	 * dependent task ID and delta[i][j] is the communication cost
	 */
	protected int[][] delta;


}
