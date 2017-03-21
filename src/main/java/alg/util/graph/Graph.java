package alg.util.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
      
      Integer nodeLevel = new Integer(0);
      
      // Get maximum level inherited from predecesors
      for (GraphNode depNode: getAncestors(this, currNode)) {
        if (nodeLevel < ((Integer)depNode.getCookie()) + 1) {
          nodeLevel = (Integer) (((Integer)depNode.getCookie()) + 1);
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
      currNode.setCookie(nodeLevel);
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
  
  /**
   * Panel class to handle graphical representation of graph.
   * 
   * @author Pedro Cuadra
   *
   */
  private class GraphDrawer extends JPanel {
    /**
     * Graph class. 
     */
    protected mxGraph mxgraph;
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
     * Constructor.
     */
    public GraphDrawer() {
      super();

      mxgraph = new mxGraph();

      // Begin adding all graph's elements
      mxgraph.getModel().beginUpdate();
      
      try {
        
        // Add nodes per level
        for (int l = 0; l < getMaxTopologicalLevel(); l++) {
          insertLevel(l);
        }
       
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
    }
    
    /**
     * Insert all vertex and edges of a topological level.
     * @param level topological level
     */
    private void insertLevel(int level) {
      ArrayList<GraphNode> nodes = getTologicalLevelNodes(level);
      Object defParent = mxgraph.getDefaultParent();
      
      // Iterate over all node in the level      
      for (int n = 0; n < nodes.size(); n++) {
        
        // Add node as vertex
        Object v1 = mxgraph.insertVertex(defParent, 
            null, 
            nodes.get(n), 
            0, 
            0, 
            NODE_WIDTH,
            NODE_HEIGHT,
            "shape=ellipse");
        
        // Set resulting object as cookie
        nodes.get(n).setCookie(v1);
        
        if (level == 0) {
          continue;
        } 
        
        // Get all parents of the given node
        ArrayList<GraphNode> parents = getParents(nodes.get(n));
        
        Graph graph = getGraph();
        
        // Add all the edges from all this node's parents to this node
        for (GraphNode parent: parents) {
          mxgraph.insertEdge(defParent, 
              null, 
              graph.getEdgeWeight(graph.getEdge(parent, nodes.get(n))), 
              parent.getCookie(), 
              v1);
        }
      }
    }

    /**
     * Get all parents of a given node.
     * 
     * @param node given node
     * @return a list of all parents of the given node
     */
    private ArrayList<GraphNode> getParents(GraphNode node) {
      ArrayList<GraphNode> parents = new ArrayList<GraphNode>();
      Graph graph = getGraph();
      
      // Iterate over all predecessors and find direct links
      for (GraphNode pred: graph.getAncestors(graph, node)) {
        if (graph.getAllEdges(pred, node).size() > 0) {
          parents.add(pred);
        }
      }
      
      return parents;
    }

  }
  
  /**
   * Get the graph object.
   * @return graph object
   */
  protected Graph getGraph() {
    return this;
  }

  /**
   * Draw graph.
   */
  public void drawGraph() {
    JPanel panel = getGraphPanel();
    
    // Create and set up the window.
    JFrame frame = new JFrame("Graph Representation of HCE");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Add content to the window.
    frame.add(panel);

    // Display the window.
    frame.pack();
    frame.setVisible(true);
    frame.setEnabled(false);
  }
  
  /**
   * Get the graph panel.
   *
   * @return graph panel
   */
  public JPanel getGraphPanel() {
    JPanel panel = new GraphDrawer();
    
    panel.setEnabled(false);
    
    return panel;
  }

}
