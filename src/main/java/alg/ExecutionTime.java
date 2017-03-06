package alg;

import alg.util.genetics.ScheduleChromosome;
import alg.util.graph.Graph;
import alg.util.graph.GraphStatsFactory;

/**
 * Scheduler representation.
 * 
 * This class should implement all the necessary methods to implement
 * Load Balancing Task Scheduling based on Multi-Population Genetic in
 * Cloud Computing (Wang Bei, LI Jun). Note that this is not actually
 * taking into account the load balancing we try to achieve but the final
 * result will be built on top of this. Also we are limiting the
 * implementation to a single population.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ExecutionTime extends GraphStatsFactory {
  /**
   * Constructor.
   * @param graph graph representation of the HCE
   */
  public ExecutionTime(Graph graph) {
    super(graph);
  }
  
  /* (non-Javadoc)
   * @see alg.util.graph.GraphStatsFactory#getFitness(alg.util.genetics.ScheduleChromosome)
   */
  @Override
  public Double getFitness(ScheduleChromosome chromosome) {
    return (double) 0;
  }

}
