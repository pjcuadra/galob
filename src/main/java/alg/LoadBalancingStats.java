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

import alg.util.genetics.ScheduleChromosome;
import alg.util.graph.Graph;
import alg.util.graph.GraphStats;
import alg.util.graph.GraphStatsFactory;



public class LoadBalancingStats extends GraphStatsFactory {
  
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
  public LoadBalancingStats(Graph graph) {
    super(graph);
    
    this.alpha = 0.5;
  }

  /**
   * Constructor.
   * 
   * @param graph graph representation of the HCE
   * @param alpha fitness function mixing coefficient
   */
  public LoadBalancingStats(Graph graph, double alpha) {
    super(graph);
    
    this.alpha = alpha;
  }

  /* (non-Javadoc)
   * @see alg.util.graph.GraphStatsFactory#getFitness(alg.util.genetics.ScheduleChromosome)
   */
  @Override
  public Double getFitness(ScheduleChromosome chromosome) {
    GraphStats stats = ofChromosome(chromosome);
    
    return ((1-alpha)*stats.getTotalTime() + alpha*stats.getStdDev());
  }
}