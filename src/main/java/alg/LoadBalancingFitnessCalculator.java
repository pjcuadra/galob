package alg;

import alg.util.FitnessCalculator;
import alg.util.HeterogeneousComputingEnv;
import alg.util.Stats;
import alg.util.jenetics.ScheduleChromosome;

/**
 * Load balancing fitness calculator.
 * 
 * @author Pedro Cuadra
 *
 */
public class LoadBalancingFitnessCalculator extends FitnessCalculator {
  /**
   * Alpha value to mix fitness functions.
   */
  private double alpha;
  
  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   */
  public LoadBalancingFitnessCalculator(HeterogeneousComputingEnv env) {
    super(env);
    this.alpha = 0.5;
  }

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   * @param alpha fitness function mixing coefficient
   */
  public LoadBalancingFitnessCalculator(HeterogeneousComputingEnv env, double alpha) {
    super(env);
    this.alpha = alpha;
  }

  /* (non-Javadoc)
   * @see alg.util.graph.GraphStatsFactory#getFitness(alg.util.genetics.ScheduleChromosome)
   */
  @Override
  public Double calcFitness(ScheduleChromosome chromosome) {
    assert chromosome != null;
    
    // Get the chromosome's stats
    Stats stats = chromosome.getStats();
    
    return ((1 - alpha) * stats.getTotalTime() + alpha * stats.getStdDev());
  }
}