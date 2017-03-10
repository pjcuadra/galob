package alg.util.graph;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

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
   * Constructor.
   */
  public Graph() {
    super(new GraphEdgeFactory());
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
  }

}
