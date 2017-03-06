package alg.util;

/**
 * Heterogeneous Computing Environment.
 * 
 * @author Pedro Cuadra
 *
 */
public class HeterogeneousComputingEnv {
  /**
   * Dependencies matrix.
   */
  private double[][] delta;
  /**
   * Expected time to compute matrix.
   */
  private double[][] etc;
  /**
   * Communication costs matrix.
   */
  private double[][] commCost;
  /**
   * Communication costs availability.
   */
  private boolean commCostAvailable;
  /**
   * Simulated annealing.
   */
  private SimulatedAnnealing simAnn;

  /**
   * Constructor.
   * 
   * @param delta dependencies matrix
   * @param etc expected time to compute matrix
   */
  public HeterogeneousComputingEnv(double[][] delta, double[][] etc) {
    this.delta = delta;
    this.etc = etc;
    this.commCost = Util.createEmptyMatrix(delta.length, delta.length);
    commCostAvailable = false;

  }

  /**
   * Constructor.
   * 
   * @param delta dependencies matrix
   * @param etc expected time to compute matrix
   * @param commCost communication costs matrix
   */
  public HeterogeneousComputingEnv(double[][] delta, double[][] etc, double[][] commCost) {
    this.delta = delta;
    this.etc = etc;
    this.commCost = commCost;
    commCostAvailable = true;
  }

  /**
   * Check the availability of communication costs.
   * 
   * @return true if HCE has communication costs and false otherwise
   */
  public boolean hasCommunicationCosts() {
    return commCostAvailable;
  }

  /**
   * Get the number of executors in the HCE.
   * 
   * @return number of executors in the HCE
   */
  public int getNumberOfExecutors() {
    return etc[0].length;
  }

  /**
   * Get the number of tasks in the HCE.
   * 
   * @return number of tasks in the HCE
   */
  public int getNumberOfTasks() {
    return delta[0].length;
  }

  /**
   * Get a copy of the dependency matrix.
   * 
   * @return a copy of the dependency matrix
   */
  public double[][] getDependencyMatrix() {
    return Util.copyMatrix(delta);
  }

  /**
   * Get a copy of the communication costs matrix.
   * 
   * @return a copy of the communication costs matrix
   */
  public double[][] getCommunicationCostsMatrix() {
    return Util.copyMatrix(commCost);
  }

  /**
   * Get a copy of the expected time to compute matrix.
   * 
   * @return a copy of the expected time to compute matrix
   */
  public double[][] getExpectedTimeToComputeMatrix() {
    return Util.copyMatrix(etc);
  }

  /**
   * Get the enable state of simulated annealing.
   * 
   * @return true if simulated annealing is enabled
   */
  public boolean getSimulatedAnnealingEnabled() {
    return simAnn != null;
  }

  /**
   * Set the simulated annealing to be used in the HCE.
   * 
   * @param simAnn simulated annealing object
   */
  public void setSimulatedAnnealing(SimulatedAnnealing simAnn) {
    this.simAnn = simAnn;
  }

  /**
   * Get the simulated annealing object.
   * 
   * @return simulated annealing object
   */
  public SimulatedAnnealing getSimulatedAnnealing() {
    return simAnn;
  }

}
