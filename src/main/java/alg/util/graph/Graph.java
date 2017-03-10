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
   * Constructor.
   */
  public Graph() {
    super(new GraphEdgeFactory());
    
    edges = new ArrayList<DefaultWeightedEdge>();
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
    
    
    return graph;
  }
  
  

}
