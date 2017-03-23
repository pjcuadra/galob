package alg.util;

import alg.util.graph.Graph;
import alg.util.graph.GraphNode;
import alg.util.jenetics.ScheduleChromosome;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;

/**
 * Graph statistic.
 * 
 * @author Pedro Cuadra
 *
 */
public class Stats {
  /**
   * Graph object.
   */
  private HeterogeneousComputingEnv env;
  /**
   * Makespan.
   */
  private Double makespan = null;
  /**
   * Average makespan between the makespan of all cores.
   */
  private Double avgMakespan;
  /**
   * Standard deviation of the makespan of all cores.
   */
  private Double stdDev;
  /**
   * Array of makespan of all cores.
   */
  private double[] makespanK;
  /**
   * Allocation matrix.
   */
  private int[][] omega;
  /**
   * Fitness calculator.
   */
  private FitnessCalculator fitnessCalculator;

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   * @param omega allocation matrix
   */
  public Stats(HeterogeneousComputingEnv env, FitnessCalculator fitnessCalculator, int[][] omega) {
    this.env = env;
    this.omega = omega;
    this.fitnessCalculator = fitnessCalculator;
  }


  /**
   * Get the execution time of every node given a chromosome.
   * Get the execution time of every node given a chromosome
   * by simulating the execution of the schedule sequence.
   * 
   * <p>According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (1)
   * 
   * @return the makespan per node as an array indexed by node index
   *  
   */
  public double[] getNodesExecutionTime() {
    
    if (makespanK != null) {
      return makespanK;
    }
    
    makespanK = new double[omega.length];
    
    Graph newGraph = env.getGraphCopy();
    
    // This iteration is in topological order according to JGraphT
    for (GraphNode curr: newGraph.vertexSet()) {
      double currAft = 0;
      
      
      if (newGraph.checkCycles()) {
        throw new Graph.CycleException();
      }
      
      currAft = getActualFinishTime(newGraph, curr);
      
      if (currAft > makespanK[getExecutionUnit(curr.getTaskId())]) {
        makespanK[getExecutionUnit(curr.getTaskId())] = currAft;
      }
      
    }
    
    return makespanK;

  }
  
  /**
   * Calculate the actual finish time of a node.
   * 
   * @param graph graph object
   * @param node node
   * @return actual finish time of the node
   */
  private double getActualFinishTime(Graph graph, GraphNode node) {
    
    double startTime = 0;
    
    if (node.getCookie() != null) {
      return (double) node.getCookie();
    }
    
    // Get earliest start time
    for (DefaultWeightedEdge inEdge : graph.incomingEdgesOf(node)) {
      GraphNode anc = graph.getEdgeSource(inEdge);
      Double tempStartTime = (Double) getActualFinishTime(graph, anc);
      
      // If were allocated in the same core don't add communications
      if (getExecutionUnit(node.getTaskId()) != getExecutionUnit(anc.getTaskId())) {
        // Add communication costs
        tempStartTime += graph.getEdgeWeight(inEdge);
      }
      
      if (tempStartTime > startTime) {
        startTime = tempStartTime;
      }
          
    }
    
    startTime += node.getExecutionTimeOnUnit(getExecutionUnit(node.getTaskId()));
    
    node.setCookie(new Double(startTime));
    
    return startTime;
    
  }
  
  /**
   * Get execution unit of a given task.
   * 
   * @param task task ID
   * @return execution unit
   */
  private int getExecutionUnit(int task) {
    
    assert omega[0].length > task : "Task index out of bound: " + omega[0].length 
    + " maximum, " + task + " provided.";
    
    for (int i = 0; i < omega.length; i++) {
      if (omega[i][task] == 1) {
        return i;
      }
    }
    
    return 0;
  }


  /**
   * Get total execution time given a Chromosome.
   * According to "Load Balancing Task Scheduling based on 
   * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
   * LI Jun), equation (2)
   * 
   * @return total execution time of a given Chromosome
   */
  public double getTotalTime() {
    
    if (makespan != null) {
      return makespan;
    }
    
    double[] sumTime = getNodesExecutionTime();
    
    makespan = new Double(Arrays.stream(sumTime).max().getAsDouble());
    
    return makespan;

  }

  /**
   * Get average execution time given a Chromosome.
   * 
   * @return average execution time of a given Chromosome
   */
  public double getAverageTime() {
    if (avgMakespan != null) {
      return avgMakespan;
    }
    
    double[] sumTime = getNodesExecutionTime();
    
    avgMakespan = new Double(Arrays.stream(sumTime).average().getAsDouble());

    return avgMakespan;

  }
  
  /**
   * Get the standard deviation of the makespan of the makespan of all cores.
   * 
   * @return average execution time of a given Chromosome
   */
  public double getStdDev() {
    
    if (stdDev != null) {
      return stdDev;
    }
    
    double[] nodesExecutionTime = getNodesExecutionTime();
    double avgTime = getAverageTime();
    
    // Take the case when only one core is available 
    if (nodesExecutionTime.length == 1) {
      stdDev = new Double(0);
      return stdDev;
    }
    
    stdDev = Arrays.stream(nodesExecutionTime)
      .map(i -> Math.pow(i - avgTime, 2))
      .sum() / (nodesExecutionTime.length - 1);
    

    // And finally take the square root
    stdDev = Math.sqrt(stdDev);
    
    stdDev = new Double(stdDev);

    return stdDev;

  }
  
  /**
   * Get the fitness of a given chromosome.
   * 
   * @param chromosome chromosome
   * @return fitness value
   */
  public Double getFitness(ScheduleChromosome chromosome) {
    return (double) fitnessCalculator.getFitness(chromosome);
  }
  
}
