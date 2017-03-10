package alg.util.jenetics;

import static java.lang.Math.min;

import alg.util.HeterogeneousComputingEnv;
import alg.util.Util;

import org.jenetics.SinglePointCrossover;
import org.jenetics.util.MSeq;

import java.util.ArrayList;
import java.util.Random;

/**
 * Schedule crossover operation class.
 * 
 * @author Pedro Cuadra
 *
 */
public class ScheduleCrossover extends SinglePointCrossover<ScheduleGene, Double> {

  /**
   * Levels partitioning of dependencies.
   */
  private ArrayList<ArrayList<Integer>> levels;
  /**
   * Heterogeneous computing environment.
   */
  private HeterogeneousComputingEnv env;

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   * @param probCrossover crossing over probability
   */
  public ScheduleCrossover(HeterogeneousComputingEnv env, double probCrossover) {
    super(probCrossover);
    this.env = env;

    levels =  Util.getDependenciesLevels(env.getDependencyMatrix()); 
  }


  /**
   * Return the dependency level of a particular taskId.
   * 
   * @param tasknum taskId 
   * @return dependency level of the taskId
   */
  private int getLevel(int tasknum) {
    for (int currLevel = 0; currLevel < levels.size(); currLevel++) {
      if (levels.get(currLevel).contains(new Integer(tasknum))) {
        return currLevel;
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see org.jenetics.SinglePointCrossover#crossover(org.jenetics.util.MSeq,
   * org.jenetics.util.MSeq)
   */
  @Override
  public int crossover(MSeq<ScheduleGene> that, MSeq<ScheduleGene> other) {
    Random randomGen = new Random();
    MSeq<ScheduleGene> tempother = other.copy();
    MSeq<ScheduleGene> tempthat = that.copy();
    int crossoverSiteLocus = randomGen.nextInt(min(that.length(), other.length()));
    ScheduleChromosome chrFac = new ScheduleChromosome(env);
    int modified = 0;


    //cond1: the tasks immediately before the crossover point must be of same level
    if (getLevel(that.get(crossoverSiteLocus).getAllele().getTaskId()) 
        == getLevel(other.get(crossoverSiteLocus).getAllele().getTaskId())) {

      that.swap(crossoverSiteLocus, min(that.length(), other.length()), other, crossoverSiteLocus);

      if ((!that.equals(tempthat)) && (!that.equals(other)) && (!other.equals(tempother))) {
        if (env.getSimulatedAnnealingEnabled()) {
          // temp: parent sequence, that : child sequemce
          if (env.getSimulatedAnnealing().checkCriteria(chrFac.newInstance(tempthat.toISeq()), 
              chrFac.newInstance(that.toISeq()))) {
            modified++;
          } else {
            // unswap: return the original chromosome as the criteria failed.
            that = tempthat.copy();
          }
          
          if (env.getSimulatedAnnealing().checkCriteria(chrFac.newInstance(tempother.toISeq()), 
              chrFac.newInstance(other.toISeq()))) {
            modified++;
          } else {
            // unswap: return the original chromosome as the criteria failed.
            other = tempother.copy();
          }
          
        }
        return modified;
      }
      
    }
    return modified;    
  }

}

