package alg.util;

import alg.util.genetics.ScheduleGene;
import org.jenetics.util.ISeq;

import java.util.Random;

public class SimulatedAnneling {

  /**
   * The cooling factor used in simulated anealing.
   */

  private double gamma;
  /**
   *  abstract scheduler class.
   */

  private Scheduler sched;
  /**
   * The initial temperature used in simulated anealing.
   */
  
  private double temp;

  /**
   * Constructor
   * 
   * @param gamma temperature used in simulated anealing.
   * @param sched scheduler class instance
   */

  public SimulatedAnneling(double gamma, double temp, Scheduler sched) {
    this.gamma = gamma;
    this.sched = sched;
    this.temp = temp;
  }

  /**
   * Check the validity based on simulated anealing.
   * @param parent the schedule sequence of parent chromosome
   * @param child  the schedule sequence of child chromosome
   * @return true if the criteria for simulated anealing is satisfied
   */
  public boolean checkCriteria(ISeq<ScheduleGene> parent, ISeq<ScheduleGene> child) {
    double probFactor;
    Random randomGen = new Random();
    // Random probability factor
    probFactor = randomGen.nextDouble();

    double fitChild = sched.getFitness(child);
    double fitParent = sched.getFitness(parent);

    if (fitChild > fitParent) {
      return true;
    } else {
      // The max value allowed for both sides of inequality is 1
      if ((Math.min(1, (Math.exp(-(fitParent - fitChild) / this.temp)))) < probFactor) {   
        this.temp = this.gamma * this.temp;
        return true;
      }
    }
    
    return false;
  }    

  public double getTemp() {
    return temp;
  }

  public void setTemp(double temp) {
    this.temp = temp;
  }

}
