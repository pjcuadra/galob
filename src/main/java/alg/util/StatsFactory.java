package alg.util;

import alg.util.jenetics.ScheduleChromosome;


/**
 * Graph statistics factory.
 * 
 * @author Pedro Cuadra
 *
 */
public class StatsFactory {
  /**
   * Heterogeneous computing environment.
   */
  private HeterogeneousComputingEnv env;
  /**
   * Fitness calculator.
   */
  private FitnessCalculator fitnessCalculator;

  /**
   * Constructor.
   * 
   * @param env graph object
   */
  public StatsFactory(HeterogeneousComputingEnv env, FitnessCalculator fitnessCalculator) {
    this.env = env;
    this.fitnessCalculator = fitnessCalculator;
  }

  /**
   * Create a graph statistics from a given chromosome.
   * 
   * @param chromosome chromosome
   * @return graph statistics
   */
  public Stats ofChromosome(ScheduleChromosome chromosome) {
    
    if (chromosome.getStats() != null) {
      return chromosome.getStats();
    }
    
    int[][] omega = Util.createOmegaMatrix(chromosome.toSeq(), 
        env.getNumberOfExecutors());
    
    Stats stats = new Stats(env, fitnessCalculator, omega);
    
    chromosome.setStats(stats);
    
    return stats;

  }
}
