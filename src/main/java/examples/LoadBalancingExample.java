package examples;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;

import alg.LoadBalancing;
import alg.util.Util;
import alg.util.genetics.ScheduleGene;
import alg.util.genetics.ScheduleMutator;

import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
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

  static final int numOfTasks = 16;
  static final int numOfExecutors = 4;
  static LoadBalancing loadBal;
  /**
   * Main function.
   * @param args command line parameters
   */
  
  public static void main(String[] args) {
    double[][] myetc = Util.getOnesMatrix(numOfExecutors, numOfTasks);
    double[][] delta = Util.getDeltaMatrix(numOfTasks);
    LoadBalancing loadBal = null;


    for (int o = 0; o < myetc[0].length; o++) {
      myetc[0][o] = 2;
      myetc[1][o] = 3;
      myetc[2][o] = 5;
    }

    loadBal = new LoadBalancing(myetc, delta);


    // Configure and build the evolution engine.
    final Engine<alg.util.genetics.ScheduleGene, Double> engine = Engine
        .builder(
            loadBal::getFitnessLoad,
            loadBal.ofSeq())
        .populationSize(500)
        .optimize(Optimize.MINIMUM)
        .selector(new RouletteWheelSelector<>())
        .alterers(
            new ScheduleMutator(delta, 0.45),
            new SinglePointCrossover<>(0.06))
        .build();

    // Create evolution statistics consumer.
    final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

    final Phenotype<ScheduleGene, Double> best = engine.stream()
        // Truncate the evolution stream after 7 "steady"
        // generations.
        .limit(bySteadyFitness(3))
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
    System.out.println("Loadbalanced solution:");
    System.out.println(best);

  }

}

