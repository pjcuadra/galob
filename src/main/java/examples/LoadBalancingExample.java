package examples;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import alg.LoadBalancingStats;
import alg.util.HCE;
import alg.util.SimulatedAnnealing;
import alg.util.Util;
import alg.util.genetics.ScheduleCodec;
import alg.util.genetics.ScheduleCrossover;
import alg.util.genetics.ScheduleGene;
import alg.util.genetics.ScheduleMutator;
import alg.util.graph.Graph;

import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;


/**
 * Example of load balacing optimization.
 *
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class LoadBalancingExample {
  /**
   * Number of tasks
   */
  static final int NUM_TASKS = 10;
  /**
   * Number of executors
   */
  static final int NUM_EXECUTORS = 3;
  /**
   * Gamma value of simulated annealing
   */
  static final double SA_GAMMA_COOLING_FACTOR = 0.85;
  /**
   * Initial temperature of simulated annealing
   */
  static final double SA_INITIAL_TEMPERATURE = 900;
  /**
   * Mutation probability
   */
  static final double MUTATION_PROBABILITY = 0.01;
  /**
   * Crossover probability
   */
  static final double CROSSOVER_PROBABILITY = 0.80;
  /**
   * Fitness function filtering factor
   */
  static final double ALPHA_FILTERING_FACTOR = 0.8;
  /**
   * Generations limit
   */
  static final int GEN_LIMIT = 10000;
  /**
   * Initial population size
   */
  static final int POPULATION_SIZE = 15;
  /** 
   * Taken from MasterESM_DPS_06.pdf page 33 (HEFT scheduling example) 
   */
  static final double[][] ETC = {{14, 16, 9},
                                 {13, 19, 18},
                                 {11, 13, 19},
                                 {13, 8, 17},
                                 {12, 13, 10},
                                 {13, 16, 9},
                                 {7, 15, 11},
                                 {5, 11, 14},
                                 {18, 12, 20},
                                 {21, 7, 16}};

  /**
   * Main function.
   * @param args command line parameters
   */
  public static void main(String[] args) {
    double[][] delta = Util.createEmptyMatrix(NUM_TASKS, NUM_TASKS);
    double[][] comCost = Util.createEmptyMatrix(NUM_TASKS, NUM_TASKS);

    // Initialize dependency matrix
    initDelta(delta);
    
    // Initialize communication costs matrix
    initCommCost(comCost);

    // Create the HCE
    HCE env = new HCE(delta, ETC, comCost);
    // Build a graph from the HCE
    Graph graph = Graph.buildGraph(env);
    
    // Create load balancing statistics calculator
    LoadBalancingStats loadBal = new LoadBalancingStats(graph, ALPHA_FILTERING_FACTOR);
    
    // Add simulated annealing to the environment
    env.setSimulatedAnnealing(new  SimulatedAnnealing(SA_GAMMA_COOLING_FACTOR, 
        SA_INITIAL_TEMPERATURE, 
        loadBal));

    // Configure and build the evolution engine.
    final Engine<alg.util.genetics.ScheduleGene, Double> engine = Engine
        .builder(
            loadBal::getFitness,
            (new ScheduleCodec(env)).ofChromosome())
        .populationSize(POPULATION_SIZE)
        .optimize(Optimize.MINIMUM)
        .selector(new RouletteWheelSelector<>())
        .alterers(
            new ScheduleMutator(env, MUTATION_PROBABILITY),
            new ScheduleCrossover(env, CROSSOVER_PROBABILITY))
        .build();

    // Create evolution statistics consumer.
    final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

    // Run evolution stream
    final Phenotype<ScheduleGene, Double> best = engine.stream()
        .limit(GEN_LIMIT)
        .peek(statistics)
        .collect(toBestPhenotype());

    System.out.println("Finished!");
    System.out.println(statistics);
    System.out.println("Loadbalanced solution:");
    System.out.println(best);
  }
  
  
  /**
   * Initialize the communication costs matrix
   * 
   * @param comCost
   */
  private static void initCommCost(double[][] comCost) {
    /* Taken from MasterESM_DPS_06.pdf page 33 (HEFT scheduling example) */
    comCost[0][1] = 18;
    comCost[0][2] = 12;
    comCost[0][3] = 9;
    comCost[0][4] = 11;
    comCost[0][5] = 14;

    comCost[1][7] = 19;
    comCost[1][8] = 16;

    comCost[2][6] = 23;

    comCost[3][7] = 27;
    comCost[3][8] = 23;

    comCost[4][8] = 13;

    comCost[5][7] = 15;

    comCost[6][9] = 17;
    comCost[7][9] = 11;
    comCost[8][9] = 13;
  }
  
  /**
   * Initialize the dependecies costs matrix
   * 
   * @param comCost
   */
  private static void initDelta(double[][] delta) {
    /* Taken from MasterESM_DPS_06.pdf page 33 (HEFT scheduling example) */
    delta[0][1] = 1;
    delta[0][2] = 1;
    delta[0][3] = 1;
    delta[0][4] = 1;
    delta[0][5] = 1;

    delta[1][7] = 1;
    delta[1][8] = 1;

    delta[2][6] = 1;

    delta[3][7] = 1;
    delta[3][8] = 1;

    delta[4][8] = 1;

    delta[5][7] = 1;

    delta[6][9] = 1;
    delta[7][9] = 1;
    delta[8][9] = 1;
  }

}


