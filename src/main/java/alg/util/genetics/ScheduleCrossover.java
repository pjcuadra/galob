package alg.util.genetics;

import alg.util.Util;

import org.jenetics.SinglePointCrossover;
import org.jenetics.util.MSeq;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Random;


public class ScheduleCrossover extends SinglePointCrossover<ScheduleGene, Double> {

  /**
   * Levels partitioning of dependencies.
   */
  private ArrayList<ArrayList<Integer>> levels;
  /**
   * Number of altered genes after crossover.
   */
  public int crosssedOvrGenes;

  /**
   * Constructor.
   * 
   * @param delta dependency matrix
   * @param probCrossover crossing over probability
   */
  public ScheduleCrossover(double[][] delta, double probCrossover) {
    super(probCrossover);

    levels =  Util.getDependenciesLevels(delta); 
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
   * @see org.jenetics.SinglePointCrossover#crossover(org.jenetics.util.MSeq, org.jenetics.util.MSeq)
   */
  @Override  
  protected int crossover(MSeq<ScheduleGene> that, MSeq<ScheduleGene> other) {
    Random randomGen = new Random();
    int crossoverSiteLocus = randomGen.nextInt(min(that.length(), other.length()));
    
    
    //cond1: the tasks immediately before the crossover point must be of same level
    if (getLevel(that.get(crossoverSiteLocus).getAllele().getTaskId()) 
        == getLevel(other.get(crossoverSiteLocus).getAllele().getTaskId())) {
      
      that.swap(crossoverSiteLocus, min(that.length(), other.length()), other, crossoverSiteLocus);
      
      return 2;
    
    }
    
    return 0;    
  }

}

