package alg;

import alg.util.FitnessCalculator;
import alg.util.HeterogeneousComputingEnv;
import alg.util.Stats;
import alg.util.jenetics.ScheduleChromosome;

/**
 * Execution time fitness calculator.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ExecutionTimeFitnessCalculator extends FitnessCalculator {
  
  /**
   * Constructor.
   * @param env heterogeneous computing environment
   */
  public ExecutionTimeFitnessCalculator(HeterogeneousComputingEnv env) {
    super(env);
  }
  
  /* (non-Javadoc)
   * @see alg.util.graph.GraphStatsFactory#getFitness(alg.util.genetics.ScheduleChromosome)
   */
  @Override
  public Double calcFitness(ScheduleChromosome chromosome) {
    
    assert chromosome != null;
    
    Stats stats = chromosome.getStats();
    
    return stats.getTotalTime();
  }

}
