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

package de.dortmund.fh.pimes.gitlab.alg.util.jenetics;

import de.dortmund.fh.pimes.gitlab.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.alg.util.PaneInfo;
import de.dortmund.fh.pimes.gitlab.alg.util.jenetics.ScheduleStatistics;

import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.EvolutionStatistics;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.PieStyler.AnnotationType;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * Genetic algorithm's chromosome.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 * 
 */
public class ScheduleStatistics extends JPanel implements TreeSelectionListener, 
    Consumer<EvolutionResult<ScheduleGene, Double>> {
  /**
   * Default Serial ID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * Delegated stream statistics.
   */
  private final EvolutionStatistics<Double, ?> statistics;
  /**
   * Heterogeneous computing environment.
   */
  private HeterogeneousComputingEnv env;
  /**
   * Simulated Annealing temperature history.
   */
  private ArrayList<Double> saTemp;
  /**
   * Generations indexes.
   */
  private ArrayList<Integer> generations;
  /**
   * Makespan per execution nodes.
   */
  private ArrayList<ArrayList<Double>> makespanK;
  /**
   * Makespan of schedule.
   */
  private ArrayList<Double> makespan;
  /**
   * Currently best phenotype.
   */
  private Phenotype<ScheduleGene, Double> bestPt;
  /**
   * Best Generation Fitness history.
   */
  private ArrayList<Double> actualFitnessHistory;
  /**
   * Best Fitness history.
   */
  private ArrayList<Double> bestFitnessHistory;
  /**
   * Best fitness value.
   */
  private double bestFitness;
  /**
   * The main split frame.
   */
  private JSplitPane splitPane;
  /**
   * The tree.
   */
  private JTree tree;
  /**
   * The panel for chart.
   */
  private XChartPanel<Chart<?, ?>> chartPanel;
  /**
   * Legend font.
   */
  private final Font legendFont = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
  
  
  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   */
  public ScheduleStatistics(HeterogeneousComputingEnv env) {
    super(new GridLayout(1, 0));
        
    // Initialize statistics related variables
    this.statistics = EvolutionStatistics.ofNumber();
    this.env = env;
    
    this.saTemp = new ArrayList<Double>();
    this.generations = new ArrayList<Integer>();
    
    // Initialize the makespan per execution node lists
    this.makespanK = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < env.getNumberOfExecutors(); i++) {
      this.makespanK.add(new ArrayList<Double>());
    }
    
    // Initialize makespan list
    this.makespan = new ArrayList<Double>();
    
    // Initialize fitness list
    this.bestFitnessHistory = new ArrayList<Double>();
    // Initialize actaul fitness list
    this.actualFitnessHistory = new ArrayList<Double>();
  }
  
  /* (non-Javadoc)
   * @see java.util.function.Consumer#accept(java.lang.Object)
   */
  @Override
  public void accept(EvolutionResult<ScheduleGene, Double> result) {
    boolean bestOption = false;
    
    if (bestPt == null) {
      bestPt = result.getBestPhenotype();
      bestFitness = result.getBestFitness();
    }

    switch (result.getOptimize()) {
      case MINIMUM:
        bestOption = bestPt.compareTo(result.getBestPhenotype()) > 0;
        break;
      case MAXIMUM:
        bestOption = bestPt.compareTo(result.getBestPhenotype()) < 0;
        break;
      default:
        bestOption = bestPt.compareTo(result.getBestPhenotype()) > 0;
        break;
    }

    if (bestOption) {
      bestPt = result.getBestPhenotype();
      bestFitness = result.getBestFitness();
    }
    
    // All fitness values
    for (Phenotype<ScheduleGene, Double> currPt: result.getPopulation()) {
      actualFitnessHistory.add(currPt.getFitness());
    }
    
    // Perform statistics accept first 
    this.statistics.accept(result);
    
    generations.add(new Integer((int) result.getGeneration()));
    
    // Perform custom accept
    if (env.getSimulatedAnnealingEnabled()) {
      saTemp.add(env.getSimulatedAnnealing().getTemp());
    }
    
    Genotype<ScheduleGene> gt = bestPt.getGenotype();
    ScheduleChromosome chr = (ScheduleChromosome) gt.getChromosome();
    
    for (int i = 0; i < env.getNumberOfExecutors(); i++) {
      this.makespanK.get(i).add(chr.getStats().getNodesExecutionTime()[i]);
    }
    
    this.makespan.add(chr.getStats().getTotalTime());
    
    this.bestFitnessHistory.add(bestFitness);
    
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return this.statistics.toString();
    
  }
  
  /**
   * Show plots of simulated annealing evolution.
   */
  public void createSimulatedAnnealingItems(DefaultMutableTreeNode top) {
    
    // categories
    DefaultMutableTreeNode category = null;
    // General
    category = new DefaultMutableTreeNode("Simulated Annealing");
    top.add(category);
    
    XYChart chart = new XYChartBuilder()
        .title("Simulated Annealing Temperature Evolution")
        .xAxisTitle("Generations")
        .yAxisTitle("Temperature")
        .build();
    
    chart.getStyler().setLegendFont(legendFont);
    
    chart.addSeries("Temperature(n)", generations, saTemp)
      .setMarker(SeriesMarkers.NONE);
    
    DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(
        new PaneInfo("Temperature Evolution", 
        chart));
    category.add(defaultMutableTreeNode);
  
  }
  
  /**
   * Show plots of makespan evolution.
   */
  public void createMakespanItems(DefaultMutableTreeNode top) {
    // categories
    DefaultMutableTreeNode category = null;
    // General
    category = new DefaultMutableTreeNode("Makespan");
    top.add(category);
    
    XYChart chart = new XYChartBuilder()
        .title("Best schedule makespan Evolution")
        .xAxisTitle("Generations")
        .yAxisTitle("Time")
        .build();
    
    chart.getStyler().setLegendFont(legendFont);
    
    for (int i = 0; i < env.getNumberOfExecutors(); i++) {
      chart.addSeries("Core - " + i, generations, this.makespanK.get(i))
        .setMarker(SeriesMarkers.NONE);
    }
    
    chart.addSeries("Total", generations, this.makespan)
      .setMarker(SeriesMarkers.NONE);
 
    
    DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(
        new PaneInfo("Best schedule evolution", 
        chart));
    category.add(defaultMutableTreeNode);

    // Create Chart
    CategoryChart catChart = new CategoryChartBuilder()
        .title("Makespan per Computing Resources")
        .xAxisTitle("Computing Resource")
        .yAxisTitle("Time")
        .build();

    // Customize Chart
    catChart.getStyler().setLegendVisible(false);
    catChart.getStyler().setHasAnnotations(true);
    catChart.getStyler().setLegendFont(legendFont);
    
    
    String[] cats = new String[env.getNumberOfExecutors()];
    Double[] makespan = new Double[env.getNumberOfExecutors()];
    
    for (int i = 0; i < env.getNumberOfExecutors(); i++) {
      cats[i] = new String("Core - " + i);
      makespan[i] = this.makespanK.get(i).get(this.makespanK.get(i).size() - 1);
    }

    // Series
    catChart.addSeries("Makespan", 
        new ArrayList<String>(Arrays.asList(cats)), 
        new ArrayList<Double>(Arrays.asList(makespan)));

    defaultMutableTreeNode = new DefaultMutableTreeNode(
        new PaneInfo("Per Computing Resources (Bars)", 
        catChart));
    category.add(defaultMutableTreeNode);
  }
  
  /**
   * Show plot of fitness evolution.
   */
  private void createGeneralItems(DefaultMutableTreeNode top) {
    // categories
    DefaultMutableTreeNode category = null;
    // General
    category = new DefaultMutableTreeNode("General");
    top.add(category);
    
    // Best fitness evolution
    XYChart chart = new XYChartBuilder() 
        .title("Best fitness Evolution")
        .xAxisTitle("Generations")
        .yAxisTitle("Fitness Value")
        .build();
    
    chart.getStyler().setLegendFont(legendFont);
    
    chart.addSeries("Fitness", generations, this.bestFitnessHistory)
      .setMarker(SeriesMarkers.NONE);
    
    DefaultMutableTreeNode defaultMutableTreeNode = new DefaultMutableTreeNode(
        new PaneInfo("Best fitness Evolution", 
        chart));
    category.add(defaultMutableTreeNode);
    
    
    // Fitness distribution
    chart = new XYChartBuilder()
        .title("Fitness distribution")
        .xAxisTitle("Fitness")
        .yAxisTitle("Frequency")
        .build();
    
    chart.getStyler().setLegendVisible(true);
    chart.getStyler().setLegendFont(legendFont);
    
    Collections.sort(this.actualFitnessHistory);
    ArrayList<Double> fitnessAxis = new ArrayList<Double>();
    ArrayList<Integer> frequencyAxis = new ArrayList<Integer>();
    
    for (Double currFitness: this.actualFitnessHistory) {
      if (fitnessAxis.contains(currFitness)) {
        int index = fitnessAxis.indexOf(currFitness);
        frequencyAxis.set(index, frequencyAxis.get(index) + 1);
        continue;
      }
      
      fitnessAxis.add(currFitness);
      frequencyAxis.add(1);
      
    }
    
    // Calculate the mean
    final double mean = this.actualFitnessHistory
        .stream()
        .mapToDouble(i -> i)
        .average()
        .getAsDouble();
    
    // Calculate the variance
    final double var = this.actualFitnessHistory
        .stream()
        .mapToDouble(i -> Math.pow((i - mean), 2))
        .sum() / (this.actualFitnessHistory.size() - 1);
    
    chart.addSeries("Fitness distribution\n"
        + "min = " + fitnessAxis.get(0) + "\n"
        + "max = " + fitnessAxis.get(fitnessAxis.size() - 1) + "\n"
        + "mean = " + mean + "\n"
        + "var = " + var + "\n"
        + "std = " + Math.sqrt(var),
        fitnessAxis, frequencyAxis)
      .setMarker(SeriesMarkers.NONE);
    
    defaultMutableTreeNode = new DefaultMutableTreeNode(
        new PaneInfo("Fitness distribution", 
        chart));
    category.add(defaultMutableTreeNode);
    
    // Time distribution
    PieChart pieChart = new PieChartBuilder()
        .title("Time Distribution")
        .build();
    
    pieChart.getStyler().setLegendVisible(true);
    pieChart.getStyler().setDefaultSeriesRenderStyle(PieSeriesRenderStyle.Donut);
    pieChart.getStyler().setAnnotationType(AnnotationType.Percentage);
    pieChart.getStyler().setLegendFont(legendFont);
    
    pieChart.addSeries("Selection:\n" + statistics.getSelectionDuration().getSum() + "s", 
        statistics.getSelectionDuration().getSum());
    pieChart.addSeries("Altering:\n" + statistics.getAlterDuration().getSum() + "s", 
        statistics.getAlterDuration().getSum());
    pieChart.addSeries("Fitness Calculation:\n" 
        + statistics.getEvaluationDuration().getSum() + "s", 
        statistics.getEvaluationDuration().getSum());
    
    defaultMutableTreeNode = new DefaultMutableTreeNode(
        new PaneInfo("Time Distribution", pieChart));
    category.add(defaultMutableTreeNode);
    
    defaultMutableTreeNode = new DefaultMutableTreeNode(
        new PaneInfo("Graph", env.getGraphPanel()));
    category.add(defaultMutableTreeNode);
    
  }
  
  /**
   * Create the GUI and show it. For thread safety, this method 
   * should be invoked from the event dispatch thread.
   */
  private void createAndShowGui() {

    // Create and set up the window.
    JFrame frame = new JFrame("Evolution stream statistics");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Add content to the window.
    frame.add(this);

    // Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  
  /**
   * Show all the statistics of the evolution stream.
   */
  public void showStats() {
    
    // Print standard stats
    System.out.println(this.toString());
        
    // Create the nodes.
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("Evolution Stream Statistics");
    
    // Create general plots
    createGeneralItems(top);
    
    // Makespan plots
    createMakespanItems(top);
    
    // Simulated Annealing plots
    if (env.getSimulatedAnnealingEnabled()) {
      createSimulatedAnnealingItems(top);
    }
    
    // Create a tree that allows one selection at a time.
    tree = new JTree(top);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    // Listen for when the selection changes.
    tree.addTreeSelectionListener(this);

    // Create the scroll pane and add the tree to it.
    JScrollPane treeView = new JScrollPane(tree);
    
    Chart<?, ?> firstChart = (Chart<?, ?>) ((PaneInfo)((DefaultMutableTreeNode)top
        .getChildAt(0)
        .getChildAt(0))
        .getUserObject())
        .getCookie();

    // Create Chart Panel
    chartPanel = new XChartPanel<Chart<?, ?>>(firstChart);

    // Add the scroll panes to a split pane.
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setTopComponent(treeView);
    splitPane.setBottomComponent(chartPanel);

    Dimension minimumSize = new Dimension(130, 160);
    treeView.setMinimumSize(minimumSize);
    splitPane.setPreferredSize(new Dimension(700, 700));

    // Add the split pane to this panel.
    add(splitPane);
    
    // Schedule a job for the event dispatch thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {

        createAndShowGui();
      }
    });
    
  }

  /* (non-Javadoc)
   * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
   */
  @Override
  public void valueChanged(TreeSelectionEvent event) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

    if (node == null) {
      return;
    }

    Object nodeInfo = node.getUserObject();
    // tree leaf
    if (node.isLeaf()) {
      PaneInfo paneInfo = (PaneInfo) nodeInfo;
      
      Object pane = paneInfo.getCookie();
      
      // If it's a chart
      if (pane instanceof Chart<?, ?>) {
        chartPanel = new XChartPanel<Chart<?, ?>>((Chart<?, ?>) paneInfo.getCookie());
        splitPane.setBottomComponent(chartPanel);
        return;
      }
      
      // Get the plain JPanel
      JPanel panel = (JPanel) pane;
      
      // Other kind of panels
      splitPane.setBottomComponent(panel);
      
    }
    
  }

}
