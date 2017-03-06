package alg.util.graph;

import alg.util.HeterogeneousComputingEnv;

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
   * Heterogeneous computing environment.
   */
  public HeterogeneousComputingEnv env;

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

  /**
   * Graph builder.
   * 
   * @param env heterogeneous computing environment
   * @return newly created graph
   */
  public static Graph buildGraph(HeterogeneousComputingEnv env) {
    Graph graph = new Graph();
    GraphNode[] nodes = new GraphNode[env.getNumberOfTasks()];
    graph.env = env;
    double[][] etc = env.getExpectedTimeToComputeMatrix();
    double[][] comCost = env.getCommunicationCostsMatrix();

    for (int i = 0; i < nodes.length; i++) {
      nodes[i] = new GraphNode(i, etc[i]);
      graph.addVertex(nodes[i]);
    }

    for (int i = 0; i < comCost.length; i++) {
      for (int j = 0; j < comCost.length; j++) {
        if (env.getDependencyMatrix()[i][j] == 1 || comCost[i][j] != 0) {
          try {
            graph.addDependency(nodes[i], nodes[j], comCost[i][j]);
          } catch (CycleFoundException excep) {
            excep.printStackTrace();
            return null;
          }
        }

      }
    }

    return graph;

  }

  /* (non-Javadoc)
   * @see org.jgrapht.graph.AbstractBaseGraph#clone()
   */
  public Graph clone() {
    return Graph.buildGraph(env);
  }

}
