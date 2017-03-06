package examples;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import alg.ExecutionTime;
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
 * Example of execution time optimization.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ExecutionTimeExample {
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
  static final int POPULATION_SIZE = 500;

  /**
   * Main function.
   * @param args command line parameters
   */
  public static void main(String[] args) {
    double[][] myetc = Util.getOnesMatrix(NUM_EXECUTORS, NUM_TASKS);
    double[][] delta = Util.getDeltaMatrix(NUM_TASKS);
    
    // Initialize expected time to compute matrix
    for (int o = 0; o < myetc[0].length; o++) {
      myetc[0][o] = 2;
      myetc[1][o] = 3;
      myetc[2][o] = 5;
    }
    
    // Create the HCE
    HCE env = new HCE(delta, myetc);
    // Build a graph from the HCE
    Graph graph = Graph.buildGraph(env);

    // Create a code
    ExecutionTime stats = new ExecutionTime(graph);

    // Set the simulated annealing to the environment
    env.setSimulatedAnnealing(new  SimulatedAnnealing(SA_GAMMA_COOLING_FACTOR, 
        SA_INITIAL_TEMPERATURE, 
        stats));

    // Configure and build the evolution engine.
    final Engine<alg.util.genetics.ScheduleGene, Double> engine = Engine
        .builder(
            stats::getFitness,
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

    // Run the evolution stream
    final Phenotype<ScheduleGene, Double> best = engine.stream()
        .limit(GEN_LIMIT)
        .peek(statistics)
        .collect(toBestPhenotype());

    System.out.println("Finished!");
    System.out.println(statistics);
    System.out.println("Solution:");
    System.out.println(best);

  }

}
