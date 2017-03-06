package alg.util.graph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Graph edge factory
 * 
 * @author Pedro Cuadra
 *
 */
public class GraphEdgeFactory implements EdgeFactory<GraphNode, DefaultWeightedEdge> {
  
  /* (non-Javadoc)
   * @see org.jgrapht.EdgeFactory#createEdge(java.lang.Object, java.lang.Object)
   */
  @Override
  public DefaultWeightedEdge createEdge(GraphNode sourceVertex, GraphNode targetVertex) {
    return new DefaultWeightedEdge();
  }

}
