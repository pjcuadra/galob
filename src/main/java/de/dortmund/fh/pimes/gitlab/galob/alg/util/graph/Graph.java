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

package de.dortmund.fh.pimes.gitlab.galob.alg.util.graph;

import org.jgrapht.alg.CycleDetector;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graph representation of a HCE.
 *
 * @author Pedro Cuadra
 *
 */
public class Graph extends DefaultDirectedGraph<GraphNode, DefaultWeightedEdge> {
  /**
   * Cycle found exception.
   */
  public static class CycleException extends RuntimeException {
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1L;

  }

  /**
   * Panel class to handle graphical representation of graph.
   *
   * @author Pedro Cuadra
   *
   */

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * Edges list.
   */
  private ArrayList<DefaultWeightedEdge> edges;
  /**
   * Levels partitioning of dependencies.
   */
  private HashMap<Integer, ArrayList<GraphNode>> levels;
  /**
   * Node to topological level mapping.
   */
  private HashMap<GraphNode, Integer> nodeLevelMap;
  /**
   * Id to nod map.
   */
  private HashMap<Integer, GraphNode> nodeId;
  /**
   * Has cycles flag.
   */
  private boolean hasCycles;

  /**
   * Constructor.
   */
  public Graph() {
    super(new GraphEdgeFactory());

    edges = new ArrayList<DefaultWeightedEdge>();

    nodeLevelMap = new HashMap<GraphNode, Integer>();

    nodeId = new HashMap<Integer, GraphNode>();

  }

  /**
   * Add dependency between nodes with custom communication cost.
   *
   * @param nodeSrc
   *          source node
   * @param nodeDst
   *          destination node
   * @param cost
   *          communication cost
   * @throws CycleFoundException
   *           cycle found
   */
  public void addDependency(GraphNode nodeSrc, GraphNode nodeDst, double cost) {
    addDependencySafe(nodeSrc, nodeDst, cost);

    CycleDetector<GraphNode, DefaultWeightedEdge> cd =
        new CycleDetector<GraphNode, DefaultWeightedEdge>(this);

    hasCycles = cd.detectCycles();

  }

  /**
   * Add dependency without checking for cycles.
   *
   * @param nodeSrc
   *          source node
   * @param nodeDst
   *          destination node
   * @param cost
   *          communication cost
   */
  private void addDependencySafe(GraphNode nodeSrc, GraphNode nodeDst, double cost) {
    DefaultWeightedEdge edge = this.getEdgeFactory().createEdge(nodeSrc, nodeDst);
    double prevCommCost = getCommunicationCost(nodeSrc, nodeDst);

    // Multiple edges are merged together summing their costs
    if (!this.containsEdge(nodeSrc, nodeDst)) {
      addEdge(nodeSrc, nodeDst, edge);
      edges.add(edge);
    }

    setEdgeWeight(this.getEdge(nodeSrc, nodeDst), prevCommCost + cost);

  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#addVertex(java.lang.Object)
   */
  @Override
  public boolean addVertex(GraphNode node) {

    nodeId.put(node.getTaskId(), node);

    return super.addVertex(node);

  }

  /**
   * Updates the topological levels lists.
   */
  private void buildTopologicalLevels() {
    nodeLevelMap.clear();
    levels = new HashMap<Integer, ArrayList<GraphNode>>();

    // Check for cycles before
    if (checkCycles()) {
      throw new CycleException();
    }

    // First clean all cookies
    for (GraphNode currNode : this.vertexSet()) {
      currNode.setCookie(this, null);
    }

    // Get topological level of every node
    for (GraphNode currNode : this.vertexSet()) {
      getTopologicalLevel(currNode);
    }

  }

  /**
   * Check if there are cycles in the graph.
   *
   * @return true if the graph has cycles and false otherwise
   */
  public boolean checkCycles() {
    return this.hasCycles;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jgrapht.graph.AbstractBaseGraph#clone()
   */
  @Override
  public Graph clone() {
    Graph graph = new Graph();

    assert !this.hasCycles;

    // Copy all vertex
    for (GraphNode node : this.vertexSet()) {
      graph.addVertex(node.clone());
    }

    // Copy all edges
    for (DefaultWeightedEdge edge : edges) {
      GraphNode src = this.getEdgeSource(edge);
      GraphNode dst = this.getEdgeTarget(edge);

      double weight = this.getEdgeWeight(edge);

      // Get the actual nodes in the current graph
      src = graph.getGraphNodeById(src.getTaskId());
      dst = graph.getGraphNodeById(dst.getTaskId());

      graph.addDependencySafe(src, dst, weight);

    }

    // This shouldn't be created again.
    graph.levels = levels;

    return graph;
  }

  /**
   * Get communication cost between to tasks.
   *
   * @param nodeSrc
   *          source node
   * @param nodeDst
   *          destination node
   * @return communication cost (zero for non-existing edges)
   */
  public double getCommunicationCost(GraphNode nodeSrc, GraphNode nodeDst) {
    if (this.getEdge(nodeSrc, nodeDst) == null) {
      return 0;
    }
    return this.getEdgeWeight(this.getEdge(nodeSrc, nodeDst));
  }

  /**
   * Remove dependency.
   *
   * @param nodeSrc
   *          source node
   * @param nodeDst
   *          destination node
   */
  public void removeDependency(GraphNode nodeSrc, GraphNode nodeDst) {
    // Assert that the vertexes are part of the Graph
    assert this.containsVertex(nodeSrc);
    assert this.containsVertex(nodeDst);

    DefaultWeightedEdge edge = this.getEdge(nodeSrc, nodeDst);

    if (edge == null) {
      return;
    }

    // Remove edge from graph
    this.removeEdge(edge);

    // Check for cycles
    CycleDetector<GraphNode, DefaultWeightedEdge> cd =
        new CycleDetector<GraphNode, DefaultWeightedEdge>(this);

    hasCycles = cd.detectCycles();

    // Force topological level rebuild
    if (levels != null) {
      levels.clear();
      levels = null;
    }

    // Remove edge from list
    this.edges.remove(edge);
  }

  /**
   * Get the graph object.
   *
   * @return graph object
   */
  protected Graph getGraph() {
    return this;
  }

  /**
   * Get graph node by it's internal id. (For testing)
   *
   * @param id
   *          id of the task
   * @return graph node with given id
   */
  public GraphNode getGraphNodeById(int id) {
    return nodeId.get(new Integer(id));
  }

  /**
   * Get the maximum topological level.
   *
   * @return maximum topological level
   */
  public int getMaxTopologicalLevel() {
    if (levels == null) {
      buildTopologicalLevels();
    }

    assert !checkCycles() : "Cycle found!";

    return levels.keySet().size();
  }

  /**
   * Get topological level of a node.
   *
   * @param taskId
   *          task id
   * @return topological level of given node
   */
  public int getNodeTopologicalLevel(int taskId) {
    if (levels == null) {
      buildTopologicalLevels();
    }

    assert !checkCycles() : "Cycle found!";
    assert taskId < nodeLevelMap.keySet().size();

    return nodeLevelMap.get(getGraphNodeById(taskId));
  }

  /**
   * Get the list of nodes in a given topological level.
   *
   * @param topoIndex
   *          topological level index
   * @return list of nodes in the topological level
   */
  public ArrayList<GraphNode> getTologicalLevelNodes(int topoIndex) {
    if (levels == null) {
      buildTopologicalLevels();
    }

    assert !checkCycles() : "Cycle found!";
    assert topoIndex < levels.size() : "Out of bound topological level";

    return levels.get(topoIndex);
  }

  /**
   * Calculate the topological level of a node.
   *
   * @param node
   *          graph node
   * @return topological level
   */
  private Integer getTopologicalLevel(GraphNode node) {
    Integer nodeLevel = new Integer(0);

    assert !checkCycles() : "Cycle found!";

    // If cookie already set
    if (node.getCookie(this) != null) {
      return (Integer) node.getCookie(this);
    }

    // Get maximum level inherited from predecessors
    for (DefaultWeightedEdge inEdge : incomingEdgesOf(node)) {
      GraphNode depNode = this.getEdgeSource(inEdge);

      if (nodeLevel < (getTopologicalLevel(depNode) + 1)) {
        nodeLevel = ((Integer) depNode.getCookie(this)) + 1;
      }
    }

    node.setCookie(this, nodeLevel);

    // If level doesn't exist create it
    if (levels.get(nodeLevel) == null) {
      levels.put(nodeLevel, new ArrayList<GraphNode>());
    }

    // Add node to the level
    levels.get(nodeLevel).add(node);
    nodeLevelMap.put(node, new Integer(nodeLevel));

    return nodeLevel;
  }

}
