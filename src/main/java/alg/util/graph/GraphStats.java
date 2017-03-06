package alg.util.graph;

import alg.util.genetics.ScheduleChromosome;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Iterator;

/**
 * Graph statistic.
 * 
 * @author Pedro Cuadra
 *
 */
public class GraphStats {
  /**
   * Graph object.
   */
  private Graph graph;
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
   * Constructor.
   * 
   * @param graph graph object
   * @param omega allocation matrix
   */
  public GraphStats(Graph graph, int[][] omega) {
    this.graph = graph;
    this.omega = omega;
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
    
    Graph newGraph = graph.clone();
    
    Iterator<GraphNode> it = newGraph.iterator();
    
    // This iteration is in topological order according to JGraphT
    while (it.hasNext()) {
      GraphNode curr = it.next();
      double currAft = getActualFinishTime(newGraph, curr);
      
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
    
    // Get earliest start time
    for (GraphNode anc : graph.getAncestors(graph, node)) {
      double tempStartTime = anc.getValue();
      DefaultWeightedEdge edge = graph.getEdge(anc, node);
      
      if (edge == null) {
        continue;
      }
      
      // Add communication costs
      tempStartTime += graph.getEdgeWeight(edge);
      
      if (tempStartTime > startTime) {
        startTime = tempStartTime;
      }
          
    }
    
    startTime += getExecutionUnit(node.getTaskId());
    
    node.setValue(startTime);
    
    return startTime;
    
    
  }
  
  /**
   * Get execution unit of a given task.
   * 
   * @param task task ID
   * @return execution unit
   */
  private int getExecutionUnit(int task) {
    
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
    makespan = new Double(0);
    

    for (double time : sumTime) {
      if (time > makespan) {
        makespan = time;
      }
    }

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
    double totalTime = 0;

    for (double time : sumTime) {

      totalTime += time;

    }
    
    avgMakespan = new Double(totalTime / sumTime.length);

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
    
    stdDev = new Double(0);
    
    double[] nodesExecutionTime = getNodesExecutionTime();
    double avgTime = getAverageTime();
    
    // First calculate sum = (sumTime(i) - averageTime)^2
    for (double time: nodesExecutionTime) {
      stdDev += Math.pow((time - avgTime), 2);
    }

    // Now multiply the sum with 1/(M - 1)
    stdDev = stdDev / ((double)(nodesExecutionTime.length - 1));

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
    return (double) 0;
    
  }
  
}
