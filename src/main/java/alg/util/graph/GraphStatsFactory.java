package alg.util.graph;

import alg.util.genetics.ScheduleChromosome;
import alg.util.genetics.ScheduleUtil;

/**
 * Graph statistics factory.
 * 
 * @author Pedro Cuadra
 *
 */
public class GraphStatsFactory {
  /**
   * Graph object .
   */
  private Graph graph;

  /**
   * Constructor.
   * 
   * @param graph graph object
   */
  public GraphStatsFactory(Graph graph) {
    this.graph = graph;
  }

  /**
   * Create a graph statistics from a given chromosome.
   * 
   * @param chromosome chromosome
   * @return graph statistics
   */
  public GraphStats ofChromosome(ScheduleChromosome chromosome) {
    
    if (chromosome.getStats() != null) {
      return chromosome.getStats();
    }
    
    int[][] omega = ScheduleUtil.createOmegaMatrix(chromosome.toSeq(), 
        graph.env.getNumberOfExecutors());
    
    GraphStats stats = new GraphStats(graph, omega);
    
    chromosome.setStats(stats);
    
    return stats;

  }

  /**
   * Get fitness value of the chromosome.
   * 
   * @param chromosome chromosome
   * @return fitness value
   */
  public Double getFitness(ScheduleChromosome chromosome) {
    return null;
  }

}
