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

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import org.jgrapht.alg.CycleDetector;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
  private class GraphDrawer extends JPanel {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -2707712944901661771L;
    /**
     * Node's width.
     */
    private static final int NODE_WIDTH = 40;
    /**
     * Node's height.
     */
    private static final int NODE_HEIGHT = 40;
    /**
     * Graph class.
     */
    protected mxGraph mxgraph;

    /**
     * Constructor.
     */
    public GraphDrawer() {
      super();

      mxgraph = new mxGraph();

      // Begin adding all graph's elements
      mxgraph.getModel().beginUpdate();

      try {

        // Add nodes per level
        buildGraph();

      } finally {

        // Finish adding graph's elements
        mxgraph.getModel().endUpdate();
      }

      // Set the hierarchical layout
      mxHierarchicalLayout layout = new mxHierarchicalLayout(mxgraph);
      layout.execute(mxgraph.getDefaultParent());

      // Create graph component and add graph to it
      mxGraphComponent graphComponent = new mxGraphComponent(mxgraph);
      this.add(graphComponent);

      mxgraph.setCellsEditable(false);
    }

    /**
     * Insert all vertex and edges of a topological level.
     *
     * @param level
     *          topological level
     */
    private void buildGraph() {
      Object defParent = mxgraph.getDefaultParent();

      Graph graph = getGraph().clone();

      // Iterate over all node
      for (GraphNode node : graph.vertexSet()) {
        insertNode(node);

        // Add all incomming edges
        for (DefaultWeightedEdge inEdges : graph.incomingEdgesOf(node)) {
          GraphNode parent = graph.getEdgeSource(inEdges);

          // Add parent in case it wasn't already added
          insertNode(parent);

          mxgraph.insertEdge(
              defParent,
              null,
              graph.getEdgeWeight(graph.getEdge(parent, node)),
              parent.getCookie(),
              node.getCookie());
        }
      }
    }

    /**
     * Insert node if wasn't already added.
     *
     * @param node
     *          node to be inserted
     */
    private void insertNode(GraphNode node) {

      if (node.getCookie() != null) {
        return;
      }

      Object defParent = mxgraph.getDefaultParent();

      // Add node as vertex
      Object v1 = mxgraph
          .insertVertex(defParent, null, node, 0, 0, NODE_WIDTH, NODE_HEIGHT, "shape=ellipse");

      // Set resulting object as cookie
      node.setCookie(v1);
    }

  }

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
    DefaultWeightedEdge edge = this.getEdgeFactory().createEdge(nodeSrc, nodeDst);
    double prevCommCost = getCommunicationCost(nodeSrc, nodeDst);

    // Multiple edges are merged together summing their costs
    if (!this.containsEdge(nodeSrc, nodeDst)) {
      addEdge(nodeSrc, nodeDst, edge);
      edges.add(edge);
    }

    setEdgeWeight(this.getEdge(nodeSrc, nodeDst), prevCommCost + cost);

    CycleDetector<GraphNode, DefaultWeightedEdge> cd =
        new CycleDetector<GraphNode, DefaultWeightedEdge>(this);

    hasCycles = cd.detectCycles();

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

    // Assumes that the nodes are topologically ordered
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

      graph.addDependency(src, dst, weight);

    }

    // This shouldn't be created again.
    graph.levels = levels;

    return graph;
  }

  /**
   * Draw graph.
   */
  public void drawGraph() {
    // Create and set up the window.
    JFrame frame = new JFrame("Graph Representation of HCE");

    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Add content to the window.
    frame.add(getGraphPanel());

    // Display the window.
    frame.pack();
    frame.setVisible(true);
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
    DefaultWeightedEdge edge = this.getEdge(nodeSrc, nodeDst);

    if (edge == null) {
      return;
    }

    // Remove edge from grahp
    this.removeEdge(edge);

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
  protected GraphNode getGraphNodeById(int id) {
    return nodeId.get(new Integer(id));
  }

  /**
   * Get the graph panel.
   *
   * @return graph panel
   */
  public Component getGraphPanel() {
    JPanel panel = new GraphDrawer();

    // add the panel to a JScrollPane
    JScrollPane scrollPane = new JScrollPane(panel);
    // only a configuration to the jScrollPane...
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    return scrollPane;
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
    if (node.getCookie() != null) {
      return (Integer) node.getCookie();
    }

    // Get maximum level inherited from predecessors
    for (DefaultWeightedEdge inEdge : incomingEdgesOf(node)) {
      GraphNode depNode = this.getEdgeSource(inEdge);

      if (nodeLevel < (getTopologicalLevel(depNode) + 1)) {
        nodeLevel = ((Integer) depNode.getCookie()) + 1;
      }
    }

    node.setCookie(nodeLevel);

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
