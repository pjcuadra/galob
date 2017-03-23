/*
 * Genetic Algorithm for Load Balancing
 * Copyright (c) 2016-2017 Pedro Cuadra & Sudheera Reddy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Pedro Cuadra (pjcuadra@gmail.com)
 *    Sudheera Reddy
 */

package de.dortmund.fh.pimes.gitlab.alg.util;

import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleChromosome;

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
