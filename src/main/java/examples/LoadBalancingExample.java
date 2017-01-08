package examples;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import alg.LoadBalancing;
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
    double[][] comCost = Util.createEmptyMatrix(numOfTasks, numOfTasks);
    LoadBalancing loadBal = null;
    
    
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

    loadBal = new LoadBalancing(etc, delta, comCost);


    /* The values in this examples are taken form "A fast hybrid genetic 
     * algorithm in heterogeneous computing environment" (Zhiyang Jiang, 
     * Shengzhong Feng) */
    // Configure and build the evolution engine.
    final Engine<alg.util.genetics.ScheduleGene, Double> engine = Engine
        .builder(
            loadBal::getFitnessLoadCommCt,
            loadBal.ofSeq())
        .populationSize(500)
        .optimize(Optimize.MINIMUM)
        .selector(new RouletteWheelSelector<>())
        .alterers(
            new ScheduleMutator(delta, 0.10), // TODO: Implement Individual adaptability
            new ScheduleCrossover(delta,0.45)) // TODO: Implement Individual adaptability
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


