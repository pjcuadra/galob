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

package de.dortmund.fh.pimes.gitlab.galob.alg.util;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.Graph;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.GraphNode;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleGene;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;
import java.util.Optional;

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
   * Chromosome.
   */
  private ScheduleChromosome chromosome;
  /**
   * Fitness calculator.
   */
  private FitnessCalculator fitnessCalculator;
  /**
   * Array of timespan of all cores.
   */
  private double[] timeSpan;
  /**
   * Graph instance with all stats cookies.
   */
  private Graph myGraph;

  /**
   * Constructor.
   *
   * @param env
   *          heterogeneous computing environment
   * @param chromosome
   *          chromosome
   */
  public Stats(HeterogeneousComputingEnv env, FitnessCalculator fitnessCalculator,
      ScheduleChromosome chromosome) {
    this.env = env;
    this.chromosome = chromosome;
    this.fitnessCalculator = fitnessCalculator;
    this.timeSpan = new double[env.getNumberOfExecutors()];
  }

  /**
   * Calculate the actual finish time of a node.
   *
   * @param graph
   *          graph object
   * @param node
   *          node
   * @return actual finish time of the node
   */
  private double getActualFinishTime(Graph graph, GraphNode node) {
    int executorId = getExecutionUnit(node.getTaskId());
    double finishTime = timeSpan[executorId];

    if (node.getCookie("aft") != null) {
      return (double) node.getCookie("aft");
    }

    // Get earliest start time
    for (DefaultWeightedEdge inEdge : graph.incomingEdgesOf(node)) {
      GraphNode anc = graph.getEdgeSource(inEdge);
      Double tempStartTime = getActualFinishTime(graph, anc);

      // If were allocated in the same core don't add communications
      if (executorId != getExecutionUnit(anc.getTaskId())) {
        // Add communication costs
        tempStartTime += graph.getEdgeWeight(inEdge);
      }

      if (tempStartTime > finishTime) {

        finishTime = tempStartTime;
      }

    }

    // Store start time as well
    node.setCookie("st", new Double(finishTime));
    node.setCookie("core", executorId);
    node.setCookie("etc", node.getExecutionTimeOnUnit(executorId));

    finishTime += node.getExecutionTimeOnUnit(executorId);

    node.setCookie("aft", new Double(finishTime));
    timeSpan[executorId] = finishTime;

    return finishTime;

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
   * Get the chromosome object.
   *
   * @return chromosome object
   */
  public ScheduleChromosome getChromosome() {
    return chromosome;
  }

  /**
   * Get execution unit of a given task.
   *
   * @param task
   *          task ID
   * @return execution unit
   */
  private int getExecutionUnit(int task) {

    assert chromosome.length() > task : "Task index out of bound: " + chromosome.length()
        + " maximum, " + task + " provided.";

    Optional<ScheduleGene> gene =
        chromosome.toSeq().stream().filter(g -> g.getAllele().getTaskId() == task).findFirst();

    if (gene.isPresent()) {
      return gene.get().getAllele().getExecutorId();
    }

    return 0;
  }

  /**
   * Get the fitness of a given chromosome.
   *
   * @param chromosome
   *          chromosome
   * @return fitness value
   */
  public Double getFitness(ScheduleChromosome chromosome) {
    return (double) fitnessCalculator.getFitness(chromosome);
  }

  /**
   * Get the execution time of every node given a chromosome. Get the execution time of every node
   * given a chromosome by simulating the execution of the schedule sequence. According to "Load
   * Balancing Task Scheduling based on Multi-Population Genetic in Cloud Computing" (Wang Bei, LI
   * Jun), equation (1)
   *
   * @return the makespan per node as an array indexed by node index
   *
   */
  public double[] getNodesExecutionTime() {

    if (makespanK != null) {
      return makespanK;
    }

    makespanK = new double[env.getNumberOfExecutors()];

    myGraph = env.getGraphCopy();

    // Check for cycles first
    if (myGraph.checkCycles()) {
      throw new Graph.CycleException();
    }

    GraphNode curr;

    // This iteration is in topological order according to JGraphT
    for (ScheduleGene gene : chromosome) {
      curr = myGraph.getGraphNodeById(gene.getAllele().getTaskId());

      double currAft = getActualFinishTime(myGraph, curr);

      if (currAft > makespanK[getExecutionUnit(curr.getTaskId())]) {
        makespanK[getExecutionUnit(curr.getTaskId())] = currAft;
      }

    }

    return makespanK;

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

    stdDev = Arrays.stream(nodesExecutionTime).map(i -> Math.pow(i - avgTime, 2)).sum()
        / (nodesExecutionTime.length - 1);

    // And finally take the square root
    stdDev = Math.sqrt(stdDev);

    stdDev = new Double(stdDev);

    return stdDev;

  }

  /**
   * Get total execution time given a Chromosome. According to "Load Balancing Task Scheduling based
   * on Multi-Population Genetic in Cloud Computing" (Wang Bei, LI Jun), equation (2)
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
   * Return the graph populated with cookies with stats values.
   *
   * @return graph
   */
  public Graph getStatsGraph() {
    return this.myGraph;
  }

}
