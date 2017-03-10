package alg.util;

import alg.util.jenetics.ScheduleChromosome;

/**
 * Graph statistics factory.
 * 
 * @author Pedro Cuadra
 *
 */
public abstract class FitnessCalculator {
  /**
   * Graph stats factory.
   */
  private StatsFactory graphStatsFact;
  
  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   */
  public FitnessCalculator(HeterogeneousComputingEnv env) {
    setGraphStatsFactory(new StatsFactory(env, this));
  }

  /**
   * Get fitness value of the chromosome.
   * 
   * @param chromosome chromosome
   * @return fitness value
   */
  public Double getFitness(ScheduleChromosome chromosome) {
    this.getGraphStatsFactory().ofChromosome(chromosome);
    
    return calcFitness(chromosome);
  }

  /**
   * Calculate fitness function.
   * 
   * @param chromosome chromosome
   * @return fitness value
   */
  protected abstract Double calcFitness(ScheduleChromosome chromosome);

  /**
   * Get graph stats factory.
   * 
   * @return graph stats factory
   */
  public StatsFactory getGraphStatsFactory() {
    return graphStatsFact;
  }

  /**
   * Set graph stats factory.
   * 
   * @param gsf graph stats factory
   */
  private void setGraphStatsFactory(StatsFactory gsf) {
    this.graphStatsFact = gsf;
  }

}
