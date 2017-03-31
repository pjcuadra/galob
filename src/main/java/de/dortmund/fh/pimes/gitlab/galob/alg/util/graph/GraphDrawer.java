package de.dortmund.fh.pimes.gitlab.galob.alg.util.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GraphDrawer extends JPanel {
  /**
   * Serial UID.
   */
  private static final long serialVersionUID = -2707712944901661771L;
  /**
   * Node's radius.
   */
  private static final int NODE_RADIUS = 40;
  /**
   * Core slot.
   */
  private static final int CORE_SLOT = 60;
  /**
   * Time slot.
   */
  private int timeSlot = 10;
  /**
   * Graph class.
   */
  protected mxGraph mxgraph;
  /**
   * Flag if it's an allocation or just a simple graph.
   */
  private boolean isAllocationVisualization;
  /**
   * JFrame's name.
   */
  private String frameName;
  /**
   * Maximum number of cores.
   */
  private int maxCore;

  /**
   * Constructor.
   */
  public static GraphDrawer ofChromosome(ScheduleChromosome chr) {
    GraphDrawer drawer = new GraphDrawer();

    drawer.isAllocationVisualization = true;
    drawer.frameName = "Simulation of the Allocation sequence";
    drawer.timeSlot = 10;

    drawer.createGraph(chr.getStats().getStatsGraph());

    return drawer;

  }

  /**
   * Constructor.
   */
  public static GraphDrawer ofGraph(HeterogeneousComputingEnv env) {
    GraphDrawer drawer = new GraphDrawer();

    drawer.isAllocationVisualization = false;
    drawer.frameName = "Graph Representation of HCE";

    drawer.createGraph(env);

    return drawer;

  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.JComponent#paint(java.awt.Graphics)
   */
  @Override
  public void paint(Graphics grap) {
    super.paint(grap);

    if (!this.isAllocationVisualization) {
      return;
    }

    Component graphComp = this.getComponents()[0];

    // Draw Cores lines
    grap.drawLine(
        graphComp.getX(),
        CORE_SLOT / 2,
        graphComp.getX() + graphComp.getWidth(),
        CORE_SLOT / 2);
    for (int i = 0; i <= this.maxCore; i++) {
      // Set cores
      grap.drawString("P" + i, graphComp.getX(), CORE_SLOT * (i + 1));

      grap.drawLine(
          graphComp.getX(),
          CORE_SLOT / 2 + CORE_SLOT * (i + 1),
          graphComp.getX() + graphComp.getWidth(),
          CORE_SLOT / 2 + CORE_SLOT * (i + 1));
    }

    grap.drawLine(
        graphComp.getX() + CORE_SLOT / 2,
        CORE_SLOT / 4,
        graphComp.getX() + graphComp.getWidth(),
        CORE_SLOT / 4);

    int time = 0;
    int skip = 10;
    // Draw ruler
    for (int i = graphComp.getX() + CORE_SLOT / 2; i <= graphComp.getX()
        + graphComp.getWidth(); i += skip * this.timeSlot) {
      grap.drawString(((Integer) (time * this.timeSlot)).toString(), i - 7, CORE_SLOT / 4 - 5);
      grap.drawLine(i, CORE_SLOT / 4, i, CORE_SLOT / 4 + 10);
      time++;
    }

  }

  /**
   * Create the graphical graph.
   *
   * @param graph
   *          graph to draw
   */
  private void createGraph(Graph graph) {
    mxgraph = new mxGraph();

    // Begin adding all graph's elements
    mxgraph.getModel().beginUpdate();

    try {

      // Add nodes per level
      buildGraph(graph);

    } finally {

      // Finish adding graph's elements
      mxgraph.getModel().endUpdate();
    }

    // Create graph component and add graph to it
    mxGraphComponent graphComponent = new mxGraphComponent(mxgraph);
    this.add(graphComponent);

    if (!isAllocationVisualization) {
      // Set the hierarchical layout
      mxHierarchicalLayout layout = new mxHierarchicalLayout(mxgraph);
      layout.execute(mxgraph.getDefaultParent());
    } else {
      graphComponent.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));
    }

    mxgraph.setCellsEditable(false);

  }

  /**
   * Insert all vertex and edges of a topological level.
   *
   * @param level
   *          topological level
   */
  private void buildGraph(Graph graph) {
    Object defParent = mxgraph.getDefaultParent();

    // Iterate over all node
    for (GraphNode node : graph.vertexSet()) {
      insertNode(node);

      // Add all incomming edges
      for (DefaultWeightedEdge inEdges : graph.incomingEdgesOf(node)) {
        GraphNode parent = graph.getEdgeSource(inEdges);

        // Add parent in case it wasn't already added
        insertNode(parent);

        // Skip if for allocation the dependency is between tasks allocated to the same core
        if (isAllocationVisualization) {
          if (parent.getCookie("core").equals(node.getCookie("core"))) {
            continue;
          }
        }

        mxgraph.insertEdge(
            defParent,
            null,
            graph.getEdgeWeight(graph.getEdge(parent, node)),
            parent.getCookie(mxgraph),
            node.getCookie(mxgraph));
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
    Object v1;

    if (node.getCookie(mxgraph) != null) {
      return;
    }

    Object defParent = mxgraph.getDefaultParent();

    if (isAllocationVisualization) {

      // Add node as vertex
      v1 = mxgraph.insertVertex(
          defParent,
          null,
          node,
          timeSlot * (Double) node.getCookie("st"),
          CORE_SLOT * (Integer) node.getCookie("core"),
          timeSlot * (Double) node.getCookie("etc"),
          NODE_RADIUS);

      if ((Integer) node.getCookie("core") > this.maxCore) {
        this.maxCore = (Integer) node.getCookie("core");
      }

    } else {
      // Add node as vertex
      v1 = mxgraph
          .insertVertex(defParent, null, node, 0, 0, NODE_RADIUS, NODE_RADIUS, "shape=ellipse");
    }

    // Set resulting object as cookie
    node.setCookie(mxgraph, v1);
  }

  /**
   * Get the graph panel.
   *
   * @return graph panel
   */
  public Component getPanel() {
    return createScrollPane(this);
  }

  /**
   * Create the scrollable panel.
   *
   * @return scrollable panel
   */
  private Component createScrollPane(JPanel panel) {
    // add the panel to a JScrollPane
    JScrollPane scrollPane = new JScrollPane(panel);

    // only a configuration to the jScrollPane...
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    return scrollPane;
  }

  /**
   * Draw graph.
   */
  public void draw() {
    // Create and set up the window.
    JFrame frame = new JFrame(this.frameName);

    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Add content to the window.
    frame.add(getPanel());

    // Display the window.
    frame.pack();
    frame.setVisible(true);
  }

}