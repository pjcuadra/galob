package alg.util;

import alg.util.graph.Graph;
import alg.util.graph.GraphNode;

import java.util.stream.DoubleStream;

/**
 * Heterogeneous Computing Environment.
 * 
 * @author Pedro Cuadra
 *
 */
public class HeterogeneousComputingEnv extends Graph {
  /**
   * Serial ID.
   */
  private static final long serialVersionUID = 11L;
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
   * Simulated Annealing.
   */
  private SimulatedAnnealing simAnn;
  /**
   * Add tasks counter.
   */
  private int addedTasks;
  
  /**
   * Constructor.
   * 
   * @param numTask number of tasks of the HCE
   * @param numCore number of cores available in the HCE
   */
  public HeterogeneousComputingEnv(int numTask, int numCore) {
    super();
    
    assert numTask > 0 : "Number of tasks shall be greater than 0";
    assert numCore > 0 : "Number of cores shall be greater than 0";
    
    // Initalize empty matrices
    this.delta = Util.createEmptyMatrix(numTask, numTask);
    this.commCost = Util.createEmptyMatrix(numTask, numTask);
    this.etc = Util.createEmptyMatrix(numTask, numCore);
    
    // Set added tasks counter to zero
    this.addedTasks = 0;
    
  }
  
  /**
   * Wrapper of add vertex to allow to add task to the graph.
   * 
   * @param etcPerCore array containing the expected computing time for every core
   */
  public GraphNode addTask(double[] etcPerCore) {
    // Verify parameters
    assert this.addedTasks < getNumberOfTasks() : "Tasks limit has been reached";
    assert etcPerCore.length == getNumberOfExecutors() : "ETC per core array with wrong size";
    
    // Copy the ETC row to matrix
    etc[this.addedTasks] = etcPerCore.clone();
    
    // Create the task node
    GraphNode taskNode = new GraphNode(this.addedTasks, etc[this.addedTasks]);
    
    // Add the task as vertex to the graph
    addVertex(taskNode);
    
    // Increase the task id counter
    this.addedTasks++;
    
    return taskNode;
  }
  
  /**
   * Add a task with expected computing time equals to 1. 
   */
  public GraphNode addUnitExecutionTimeTask() {
    double[] etcRow;
    
    // Create ones row
    etcRow = DoubleStream.generate(() -> 1).limit(getNumberOfExecutors()).toArray();
    
    return addTask(etcRow);
  }
  
  /* (non-Javadoc)
   * @see alg.util.graph.Graph#addDependency(alg.util.graph.GraphNode, 
   *                                         alg.util.graph.GraphNode, 
   *                                         double)
   */
  @Override
  public void addDependency(GraphNode nodeSrc, GraphNode nodeDst, double cost) {
    
    // Verify parameters
    assert containsVertex(nodeSrc) : "Invalid task node";
    assert containsVertex(nodeDst) : "Invalid task node";
    
    try {
      // Add dependency to the graph
      super.addDependency(nodeSrc, nodeDst, cost);
      
      // Add dependency to the matrices
      this.delta[nodeSrc.getTaskId()][nodeDst.getTaskId()] = 1;
      this.commCost[nodeSrc.getTaskId()][nodeDst.getTaskId()] = cost;
      
    } catch (org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException e) {
      e.printStackTrace();
    }
    
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
    return delta.length;
  }

  /**
   * Get a copy of the dependency matrix.
   * 
   * @return a copy of the dependency matrix
   */
  public double[][] getDependencyMatrix() {
    assert this.addedTasks > 0 : "No tasks added to the HCE";
    return Util.copyMatrix(delta);
  }

  /**
   * Get a copy of the communication costs matrix.
   * 
   * @return a copy of the communication costs matrix
   */
  public double[][] getCommunicationCostsMatrix() {
    assert this.addedTasks > 0 : "No tasks added to the HCE";
    return Util.copyMatrix(commCost);
  }

  /**
   * Get a copy of the expected time to compute matrix.
   * 
   * @return a copy of the expected time to compute matrix
   */
  public double[][] getExpectedTimeToComputeMatrix() {
    assert this.addedTasks > 0 : "No tasks added to the HCE";
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
    assert simAnn != null : "Null parameter";
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
  
  /**
   * Get a copy of the graph representation of the HCE.
   * 
   * @return copy of graph representation of the HCE
   */
  public Graph getGraphCopy() {
    assert this.addedTasks > 0 : "No tasks added to the HCE";
    return (Graph) ((Graph) this).clone();
  }
  
  
  /**
   * Get graph node by it's internal id. (For testing)
   * 
   * @param id id of the task
   * @return graph node with given id
   */
  protected GraphNode getGraphNodeById(int id) {
    return super.getGraphNodeById(id);
  }
  
  /**
   * Generates a completely random heterogeneous computing environment.
   * 
   * @param numTasks number of tasks
   * @param numCores number of cores
   * @param maxProvided numTasks and numCores are maximum values if 
   *      true or exact values if false
   * @return random heterogeneous computing environment
   */
  public static HeterogeneousComputingEnv ofRandom(int numTasks, 
      int numCores, 
      boolean maxProvided) {
    return Util.ofRandom(numTasks, numCores, maxProvided);
  }
  
  /**
   * Generates a random heterogeneous computing environment with: unit execution time, 
   * unit communication costs and random dependencies.
   * 
   * @param numTasks number of tasks
   * @param numCores number of cores
   * @return random heterogeneous computing environment
   */
  public static HeterogeneousComputingEnv ofRandomUnitary(int numTasks, 
      int numCores, 
      boolean maxProvided) {
    return Util.ofRandomUnitary(numTasks, numCores, maxProvided);
  }
}
