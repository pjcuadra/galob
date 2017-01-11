package alg.util.genetics;

import static java.lang.Math.min;

import alg.util.SimulatedAnneling;
import alg.util.Util;

import org.jenetics.SinglePointCrossover;
import org.jenetics.util.MSeq;

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
   * Object of simulated anneling.
   */
  private SimulatedAnneling simAnne;
  /**
   * Flag to indicate if simulated anealing is required.
   */
  private boolean isSimulated;

  /**
   * Constructor.
   * 
   * @param delta dependency matrix
   * @param probCrossover crossing over probability
   */
  public ScheduleCrossover(double[][] delta, double probCrossover) {
    super(probCrossover);
    this.simAnne = null;
    isSimulated = false;

    levels =  Util.getDependenciesLevels(delta); 
  }

  /**
   * Constructor.
   * 
   * @param delta dependency matrix
   * @param probCrossover crossing over probability
   * @param simAnne Simulated anealing object
   */
  public ScheduleCrossover(double[][] delta, double probCrossover, SimulatedAnneling simAnne) {
    super(probCrossover);
    this.simAnne = simAnne;
    isSimulated = true;
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
   * @see org.jenetics.SinglePointCrossover#crossover(org.jenetics.util.MSeq,
   * org.jenetics.util.MSeq)
   */
  @Override  
  protected int crossover(MSeq<ScheduleGene> that, MSeq<ScheduleGene> other) {
    Random randomGen = new Random();
    MSeq<ScheduleGene> temp_other = other.copy();
    MSeq<ScheduleGene> temp_that = that.copy();
    int crossoverSiteLocus = randomGen.nextInt(min(that.length(), other.length()));


    //cond1: the tasks immediately before the crossover point must be of same level
    if (getLevel(that.get(crossoverSiteLocus).getAllele().getTaskId()) 
        == getLevel(other.get(crossoverSiteLocus).getAllele().getTaskId())) {

      that.swap(crossoverSiteLocus, min(that.length(), other.length()), other, crossoverSiteLocus);

      if ((!that.equals(temp_that)) && (!that.equals(other)) && (!other.equals(temp_other))) {
        if (isSimulated) {
          // temp: parent sequence, that : child sequemce
          if (simAnne.checkCriteria(temp_that.toISeq(), that.toISeq())) {
            return 2;
          } else {
            // unswap: return the original chromosome as the criteria failed.
            that = temp_that.copy();
            other = temp_other.copy();
            return 0;
          }
          
        }
        return 2;
      }
      
    }
    return 0;    
  }

}

