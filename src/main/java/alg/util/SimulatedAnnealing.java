package alg.util;

import alg.util.genetics.ScheduleChromosome;
import alg.util.graph.GraphStatsFactory;

import java.util.Random;

/**
 * Simulated Annealing class.
 * 
 * @author Pedro Cuadra
 *
 */
public class SimulatedAnnealing {
  /**
   * The cooling factor used in simulated anealing.
   */
  private double gamma;
  /**
   * The initial temperature used in simulated anealing.
   */
  private double temp;
  /**
   * Graph statistics factory.
   */
  private GraphStatsFactory st;

  /**
   * Constructor.
   * 
   * @param gamma temperature used in simulated annealing.
   * @param temp initial temperature 
   * @param st graph statistics factory
   * 
   */
  public SimulatedAnnealing(double gamma, double temp, GraphStatsFactory st) {
    this.gamma = gamma;
    this.temp = temp;
    this.st = st;
  }

  /**
   * Check the validity based on simulated annealing.
   * 
   * @param parent the schedule sequence of parent chromosome
   * @param child  the schedule sequence of child chromosome
   * @return true if the criteria for simulated annealing is satisfied
   */
  public boolean checkCriteria(ScheduleChromosome parent, ScheduleChromosome child) {
    
    if (st == null) {
      System.out.println("WARNING! No StatsFactory was set to the simmulated anealing");
      return true;
    }
    
    double probFactor;
    Random randomGen = new Random();
    // Random probability factor
    probFactor = randomGen.nextDouble();

    double fitChild = st.getFitness(child);
    double fitParent = st.getFitness(parent);

    if (fitChild > fitParent) {
      return true;
    } else {
      // The max value allowed for both sides of inequality is 1
      if ((Math.min(1, (Math.exp(-(fitParent - fitChild) / this.temp)))) > probFactor) {   
        this.temp = this.gamma * this.temp;
        return true;
      }
    }
    
    return false;
  }    

  /**
   * Get current temperature of the simulated annealing object.
   * 
   * @return current temperature of the simulated annealing object 
   */
  public double getTemp() {
    return temp;
  }

  /**
   * Set the current temperature of the simulated annealing object.
   * 
   * @param temp temperature of the simulated annealing object
   */
  public void setTemp(double temp) {
    this.temp = temp;
  }
  
}
