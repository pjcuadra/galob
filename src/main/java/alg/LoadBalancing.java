/* 
 * This class should implement all the necessary methods to implement
 * Load Balancing Task Scheduling based on Multi-Population Genetic in
 * Cloud Computing (Wang Bei, LI Jun). This class is based on 
 * alg.ExecutionTime class but now optimizing load balance. 
 *
 * Author:
 *    Pedro Cuadra
 *    Sudheera Bandi
 */

package alg;

import alg.ExecutionTime;
import alg.util.genetics.ScheduleGene;
import org.jenetics.util.ISeq;


// For now this class is just a placeholder. Remove this comment once
// class is implemented.

public class LoadBalancing extends ExecutionTime {

  public LoadBalancing(double[][] etcmatrix, double[][] delta) {
    super(etcmatrix, delta);
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
   * The fitness function to calculate fitness of a given Chromosome
   * with respect to the work load.
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (7)
   * 
   * @param  scheduleSeq the schedule sequence of the chromosome    
   * @return fitness of a given Chromosome
   */
  public double getFitnessLoad(ISeq<ScheduleGene> scheduleSeq) {
    double fitness = 0;
    int[][] omega = createOmegaMatrix(scheduleSeq);
    fitness = getLoad(omega);

    return fitness;
  }
}
