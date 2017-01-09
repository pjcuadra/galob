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
import alg.util.genetics.ScheduleGene;

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
    double totaltime = getTotalTime(scheduleSeq);
    double totalComCost = getCommCost(scheduleSeq);

    fitness = (costFact * (totaltime + totalComCost));

    return fitness;
  }


}
