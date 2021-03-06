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
   * Generates a completely random heterogeneous computing environment.
   *
   * @param numTasks
   *          number of tasks
   * @param numCores
   *          number of cores
   * @param maxProvided
   *          numTasks and numCores are maximum values if true or exact values if false
   * @return random heterogeneous computing environment
   */
  public static HeterogeneousComputingEnv ofRandom(
      int numTasks,
      int numCores,
      boolean maxProvided) {
    return Util.ofRandom(numTasks, numCores, maxProvided);
  }

  /**
   * Generates a random heterogeneous computing environment with: unit execution time, unit
   * communication costs and random dependencies.
   *
   * @param numTasks
   *          number of tasks
   * @param numCores
   *          number of cores
   * @return random heterogeneous computing environment
   */
  public static HeterogeneousComputingEnv ofRandomUnitary(
      int numTasks,
      int numCores,
      boolean maxProvided) {
    return Util.ofRandomUnitary(numTasks, numCores, maxProvided);
  }

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
   * @param numTask
   *          number of tasks of the HCE
   * @param numCore
   *          number of cores available in the HCE
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
   * @param etcPerCore
   *          array containing the expected computing time for every core
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

  /*
   * (non-Javadoc)
   *
   * @see alg.util.graph.Graph#addDependency(alg.util.graph.GraphNode, alg.util.graph.GraphNode,
   * double)
   */
  @Override
  public void addDependency(GraphNode nodeSrc, GraphNode nodeDst, double cost) {

    // Verify parameters
    assert containsVertex(nodeSrc) : "Invalid task node";
    assert containsVertex(nodeDst) : "Invalid task node";

    if (nodeSrc.getTaskId() == nodeDst.getTaskId()) {
      return;
    }

    // Add dependency to the graph
    super.addDependency(nodeSrc, nodeDst, cost);

    // Add dependency to the matrices
    this.delta[nodeSrc.getTaskId()][nodeDst.getTaskId()] = 1;
    this.commCost[nodeSrc.getTaskId()][nodeDst.getTaskId()] += cost;

  }

  /**
   * Remove dependency.
   *
   * @param nodeSrc
   *          source node
   * @param nodeDst
   *          destination node
   */
  @Override
  public void removeDependency(GraphNode nodeSrc, GraphNode nodeDst) {
    // Delete dependency from matrices
    this.delta[nodeSrc.getTaskId()][nodeDst.getTaskId()] = 0;
    this.commCost[nodeSrc.getTaskId()][nodeDst.getTaskId()] = 0;

    super.removeDependency(nodeSrc, nodeDst);
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
   * Get a copy of the dependency matrix.
   *
   * @return a copy of the dependency matrix
   */
  public double[][] getDependencyMatrix() {
    assert this.addedTasks > 0 : "No tasks added to the HCE";

    return Util.copyMatrix(delta);
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
   * Get a copy of the graph representation of the HCE.
   *
   * @return copy of graph representation of the HCE
   */
  public Graph getGraphCopy() {
    assert this.addedTasks > 0 : "No tasks added to the HCE";
    return ((Graph) this).clone();
  }

  /**
   * Get graph node by it's internal id. (For testing)
   *
   * @param id
   *          id of the task
   * @return graph node with given id
   */
  @Override
  public GraphNode getGraphNodeById(int id) {
    return super.getGraphNodeById(id);
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
   * Get the simulated annealing object.
   *
   * @return simulated annealing object
   */
  public SimulatedAnnealing getSimulatedAnnealing() {
    return simAnn;
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
   * @param simAnn
   *          simulated annealing object
   */
  public void setSimulatedAnnealing(SimulatedAnnealing simAnn) {
    assert simAnn != null : "Null parameter";
    this.simAnn = simAnn;
  }
}
