package alg.util;

import alg.util.jenetics.ScheduleChromosome;

import java.util.Random;

/**
 * Simulated Annealing class.
 * 
 * @author Pedro Cuadra
 *
 */
public class SimulatedAnnealing {
  /**
   * The cooling factor used in simulated annealing.
   */
  private double gamma;
  /**
   * The initial temperature used in simulated annealing.
   */
  private double temp;
  /**
   * Fitness calculator.
   */
  private FitnessCalculator fitnessCalculator;

  /**
   * Constructor.
   * 
   * @param gamma temperature used in simulated annealing.
   * @param temp initial temperature 
   * 
   */
  public SimulatedAnnealing(double gamma, double temp, FitnessCalculator fitnessCalculator) {
    this.gamma = gamma;
    this.temp = temp;
    this.fitnessCalculator = fitnessCalculator;
  }

  /**
   * Check the validity based on simulated annealing.
   * 
   * @param oldChromosome the schedule sequence of parent chromosome
   * @param newChromosom  the schedule sequence of child chromosome
   * @return true if the criteria for simulated annealing is satisfied
   */
  public boolean checkCriteria(ScheduleChromosome oldChromosome, ScheduleChromosome newChromosom) {
    
    double probFactor;
    Random randomGen = new Random();
    // Random probability factor
    probFactor = randomGen.nextDouble();

    double fitNew = fitnessCalculator.getFitness(newChromosom);
    double fitOld = fitnessCalculator.getFitness(oldChromosome);
    
    if (fitNew > fitOld) {
      return true;
    } else {
      // The max value allowed for both sides of inequality is 1
      if ((Math.min(1, (Math.exp(-(fitOld - fitNew) / this.temp)))) > probFactor) {   
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
