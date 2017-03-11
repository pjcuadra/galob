package alg.util.jenetics;

import static java.lang.Math.min;

import alg.util.HeterogeneousComputingEnv;

import org.jenetics.SinglePointCrossover;
import org.jenetics.util.MSeq;

import java.util.Random;

/**
 * Schedule crossover operation class.
 * 
 * @author Pedro Cuadra
 *
 */
public class ScheduleCrossover extends SinglePointCrossover<ScheduleGene, Double> {
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
    int thatTopLevel = env.getNodeTopologicalLevel(that
        .get(crossoverSiteLocus)
        .getAllele()
        .getTaskId());
    int otherTopLevel = env.getNodeTopologicalLevel(other
        .get(crossoverSiteLocus)
        .getAllele()
        .getTaskId());


    //cond1: the tasks immediately before the crossover point must be of same level
    if (thatTopLevel != otherTopLevel) {
      return modified;
    }

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
      
    return modified;    
  }

}

