package examples;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;

import alg.ExecutionTime;
import alg.util.Util;
import alg.util.genetics.ScheduleGene;

import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
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

  static final int numTasks = 16;
  static final int numExecutors = 4;
  static ExecutionTime etOpt;

  /**
   * Main function.
   * @param args command line parameters
   */
  public static void main(String[] args) {
    double[][] myetc = Util.getOnesMatrix(numExecutors, numTasks);
    double[][] delta = Util.getDPNDMatrix(numTasks);
    ExecutionTime etOpt = null;


    for (int o = 0; o < myetc[0].length; o++) {
      myetc[0][o] = 2;
      myetc[1][o] = 3;
      myetc[2][o] = 5;
    }

    etOpt = new ExecutionTime(myetc, delta);


    // Configure and build the evolution engine.
    final Engine<alg.util.genetics.ScheduleGene, Double> engine = Engine
        .builder(
            etOpt::getFitness,
            etOpt.ofCONV())
        .populationSize(500)
        .optimize(Optimize.MINIMUM)
        .selector(new RouletteWheelSelector<>())
        .alterers(
            new Mutator<>(0.55),
            new SinglePointCrossover<>(0.06))
        .build();

    // Create evolution statistics consumer.
    final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

    final Phenotype<ScheduleGene, Double> best = engine.stream()
        // Truncate the evolution stream after 7 "steady"
        // generations.
        .limit(bySteadyFitness(7))
        // The evolution will stop after maximal 100
        // generations.
        .limit(100)
        // Update the evaluation statistics after
        // each generation
        .peek(statistics)
        // Collect (reduce) the evolution stream to
        // its best phenotype.
        .collect(toBestPhenotype());

    System.out.println("Finished!");

    System.out.println(statistics);
    System.out.println(best);

  }

}
