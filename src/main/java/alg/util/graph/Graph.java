package alg.util.graph;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Graph representation of a HCE.
 * 
 * @author Pedro Cuadra
 *
 */
public class Graph extends DirectedAcyclicGraph<GraphNode, DefaultWeightedEdge> {
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
  private ArrayList<ArrayList<GraphNode>> levels;
  /**
   * Node to topological level mapping.
   */
  private ArrayList<Integer> nodeLevelMap;

  /**
   * Constructor.
   */
  public Graph() {
    super(new GraphEdgeFactory());
    
    edges = new ArrayList<DefaultWeightedEdge>();
    
    nodeLevelMap = new ArrayList<Integer>();
    
  }
  
  /**
   * Add dependency between nodes with custom communication cost.
   * 
   * @param nodeSrc source node
   * @param nodeDst destination node
   * @param cost communication cost
   * @throws CycleFoundException cycle found
   */
  public void addDependency(GraphNode nodeSrc, GraphNode nodeDst, double cost) 
      throws CycleFoundException {
    DefaultWeightedEdge edge = this.getEdgeFactory().createEdge(nodeSrc, nodeDst);

    addDagEdge(nodeSrc, nodeDst, edge);
    setEdgeWeight(edge, cost);
    
    edges.add(edge);
    
  }
  
  /**
   * Get graph node by it's internal id. (For testing)
   * 
   * @param id id of the task
   * @return graph node with given id
   */
  protected GraphNode getGraphNodeById(int id) {
    Iterator<GraphNode> it = this.iterator();
    
    // TODO: This can be improved by a hashmap
    while (it.hasNext()) {
      GraphNode node = it.next();
      
      if (node.getTaskId() == id) {
        return node;
      }
    }
    
    return null;
  }
  
  /* (non-Javadoc)
   * @see org.jgrapht.graph.AbstractBaseGraph#clone()
   */
  @Override
  public Graph clone() {
    Graph graph = new Graph();
    Iterator<GraphNode> currNode = this.iterator();
    
    // Copy all vertex
    while (currNode.hasNext()) {
      GraphNode node = currNode.next();
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
      
      try {
        graph.addDependency(src, dst, weight);
      } catch (org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException e) {
        e.printStackTrace();
        return null;
      }
            
    }
    
    // This shouldn't be created again.
    graph.levels = levels;    
    
    return graph;
  }
  
  /**
   * Updates the topological levels lists.
   */
  private void buildTopologicalLevels() {
    levels = new ArrayList<ArrayList<GraphNode>>();
    levels.add(new ArrayList<GraphNode>());
    nodeLevelMap.clear();
    
    Iterator<GraphNode> it = this.iterator();
    
    // Assumes that the nodes are topologically ordered
    while (it.hasNext()) {
      GraphNode currNode = it.next();
      
      int nodeLevel = 0;
      
      // Get maximum level inherited from predecesors
      for (GraphNode depNode: getAncestors(this, currNode)) {
        if (nodeLevel > depNode.getValue() + 1) {
          nodeLevel = (int) (depNode.getValue() + 1);
        }
      }
      
      // If level is bigger add new level (works because of the iteration order)
      while (nodeLevel + 1 > levels.size()) {
        levels.add(new ArrayList<GraphNode>());
      }
      
      // If nodeLevelMap size isn't big enough
      while (currNode.getTaskId() + 1 > nodeLevelMap.size()) {
        nodeLevelMap.add(new Integer(0));
      }
      
      // Add node to the level
      levels.get(nodeLevel).add(currNode);
      currNode.setValue(nodeLevel);
      nodeLevelMap.add(currNode.getTaskId(), new Integer(nodeLevel));
    }
    
  }
  
  /**
   * Get the list of nodes in a given topological level.
   * 
   * @param topoIndex topological level index
   * @return list of nodes in the topological level
   */
  public ArrayList<GraphNode> getTologicalLevelNodes(int topoIndex) {
    if (levels == null) {
      buildTopologicalLevels();
    }
    
    assert topoIndex < levels.size() : "Out of bound topological level";
    
    return levels.get(topoIndex);
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
    
    return levels.size();
  }
  
  /**
   * Get topological level of a node.
   * 
   * @param taskId task id
   * @return topological level of given node
   */
  public int getNodeTopologicalLevel(int taskId) {
    if (levels == null) {
      buildTopologicalLevels();
    }
    
    assert taskId < nodeLevelMap.size();
    
    return (int) nodeLevelMap.get(taskId);
  }
  

}
