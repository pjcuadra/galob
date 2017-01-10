package examples;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import alg.LoadBalancing;
import alg.util.SimulatedAnneling;
import alg.util.Util;
import alg.util.genetics.ScheduleCrossover;
import alg.util.genetics.ScheduleGene;
import alg.util.genetics.ScheduleMutator;

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

  static final int numOfTasks = 10;
  static final int numOfExecutors = 3;
  static final double gamma = 0.85;

  /* Taken from MasterESM_DPS_06.pdf page 33 (HEFT scheduling example) */
  static final double[][] etc = {{14, 13, 11, 13, 12, 13, 7, 5, 18, 21},
      {16, 19, 13, 8, 13, 16, 15, 11, 12, 7},
      {9, 18, 19, 17, 10, 9, 11, 14, 20, 16}};

  /**
   * Main function.
   * @param args command line parameters
   */

  public static void main(String[] args) {
    double[][] delta = Util.createEmptyMatrix(numOfTasks, numOfTasks);


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


    double[][] comCost = Util.createEmptyMatrix(numOfTasks, numOfTasks);

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

    LoadBalancing loadBal = new LoadBalancing(etc, delta, 0.6, comCost);
    SimulatedAnneling  simAnn = new  SimulatedAnneling(0.8, 900, loadBal);

    /* The values in this examples are taken form "A fast hybrid genetic 
     * algorithm in heterogeneous computing environment" (Zhiyang Jiang, 
     * Shengzhong Feng) */
    // Configure and build the evolution engine.
    final Engine<alg.util.genetics.ScheduleGene, Double> engine = Engine
        .builder(
            loadBal::getFitness,
            loadBal.ofSeq())
        .populationSize(500)
        .optimize(Optimize.MAXIMUM)
        .selector(new RouletteWheelSelector<>())
        .alterers(
            // use simulated aneeling technique
            new ScheduleMutator(delta, 0.1, simAnn),
            new ScheduleCrossover(delta, 0.45, simAnn))
        .build();

    // Create evolution statistics consumer.
    final EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

    final Phenotype<ScheduleGene, Double> best = engine.stream()
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


