package alg.util.genetics;

import alg.util.Util;

import org.jenetics.SinglePointCrossover;
import org.jenetics.util.MSeq;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Random;


public class ScheduleCrossover extends SinglePointCrossover<ScheduleGene, Double> {

  /**
   * Number of tasks.
   */
  private int numTasks;
  /**
   * Levels partitioning of dependencies.
   */
  private ArrayList<ArrayList<Integer>> levels;
  /**
   * The dependency matrix copy.
   */
  private double[][] delta;
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
    this.numTasks = delta.length;
    this.delta = delta;

    levels =  new ArrayList<ArrayList<Integer>>(); 

    getDependenciesLevels(delta);
  }

  /**
   * Create the levels representation of the dependencies.
   * 
   * @param delta dependencies matrix
   */
  private void getDependenciesLevels(double[][] delta) {

    ArrayList<Integer> toDet = new ArrayList<Integer>();
    ArrayList<Integer> thisLevel;
    double[][] myDelta = Util.copyMatrix(delta);
    for (int i = 0; i < numTasks ;i++) {
      toDet.add(new Integer(i));
    }

    while (!toDet.isEmpty()) {
      thisLevel =  new ArrayList<Integer>();

      for (Integer task = 0; task < numTasks; task++) {

        if (!toDet.contains(task)) {
          continue;
        }


        /*
         * check if there is a dependency with a successive task
         * check for ones in the column
         */

        if (!(Util.checkColZero(myDelta, task))) {
          continue;
        }

        toDet.remove(task);

        thisLevel.add(task);

      }
      for (Integer iterator:thisLevel) {
        //to clear the elements of the row
        Util.clearRow(myDelta, iterator);
      }

      levels.add(thisLevel);


    }

  }

  /**
   * Return the dependency level of a particular taskId.
   * 
   * @param tasknum taskId 
   * @return dependency level of the taskId
   */
  private int getLevel(int tasknum) {
    int tasklevel = 0;
    for (int i = 0; i < delta.length; i++) {
      tasklevel += delta[i][tasknum]; 
    }
    return tasklevel;

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

