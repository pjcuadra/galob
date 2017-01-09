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

import alg.util.Scheduler;
import alg.util.genetics.ScheduleGene;
import org.jenetics.util.ISeq;


// For now this class is just a placeholder. Remove this comment once
// class is implemented.

public class LoadBalancing extends Scheduler {
  
  /**
   * Alpha value to mix fitness functions.
   */
  private double alpha;
  
  
  /**
   * Constructor.
   * 
   * @param etcmatrix execution time matrix
   * @param delta dependencies matrix
   */
  public LoadBalancing(double[][] etcmatrix, double[][] delta) {
    super(etcmatrix, delta);
    
    this.alpha = 0.5;
  }

  /**
   * Constructor.
   * 
   * @param etcmatrix execution time matrix
   * @param delta dependencies matrix
   * @param alpha fitness function mixing coefficient
   */
  public LoadBalancing(double[][] etcmatrix, double[][] delta, double alpha) {
    super(etcmatrix, delta);
    
    this.alpha = alpha;
  }

  /**
   * Constructor.
   * @param etc execution time matrix
   * @param delta dependencies matrix
   * @param alpha fitness function mixing coefficient
   * @param comCost communication costs matrix
   */
  public LoadBalancing(double[][] etc, double[][] delta, double alpha, double[][] comCost) {
    super(etc, delta, comCost);
    
    this.alpha = alpha;
  }
  
  /**
   * Constructor.
   * @param etc execution time matrix
   * @param delta dependencies matrix
   * @param comCost communication costs matrix
   */
  public LoadBalancing(double[][] etc, double[][] delta, double[][] comCost) {
    super(etc, delta, comCost);
    
    this.alpha = 0.5;
  }

  /**
   * Get the load imbalance of given Chromosome.
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (4)
   * 
   * @param scheduleSeq scheduling sequence
   * 
   * @return load imbalance of a Chromosome
   */
  public double getLoad(ISeq<ScheduleGene> scheduleSeq) {
    double[] nodesExecutionTime = getNodesExecutionTime(scheduleSeq);
    double load = 0;
    double avgTime = getTotalTime(scheduleSeq) / etc.length;

    // First calculate sum = (sumTime(i) - averageTime)^2
    for (double time: nodesExecutionTime) {
      load += Math.pow(time - avgTime, 2);
    }

    // Now multiply the sum with 1/(M - 1)
    load = load / ((double)(nodesExecutionTime.length - 1));

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
    return 1 / getLoad(scheduleSeq);
  }
  
  /**
   * The fitness function to calculate fitness of a given Chromosome
   * with respect to the work load and communication costs.
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (7)
   * 
   * @param  scheduleSeq the schedule sequence of the chromosome    
   * @return fitness of a given Chromosome
   */
  public double getFitnessLoadCommCt(ISeq<ScheduleGene> scheduleSeq) {
    return alpha * getFitnessLoad(scheduleSeq) + (1 - alpha) * (1 / getTotalTime(scheduleSeq));
  }
}
