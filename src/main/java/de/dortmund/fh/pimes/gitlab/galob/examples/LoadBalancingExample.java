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

package de.dortmund.fh.pimes.gitlab.galob.examples;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.SimulatedAnnealing;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.GraphDrawer;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.graph.GraphNode;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleChromosome;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleCodec;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleCrossover;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleGene;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleMutator;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics.ScheduleStatistics;

import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.engine.Engine;

/**
 * Example of load balacing optimization.
 *
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class LoadBalancingExample {
  /**
   * Number of tasks.
   */
  static final int NUM_TASKS = 10;
  /**
   * Number of executors.
   */
  static final int NUM_EXECUTORS = 3;
  /**
   * Gamma value of simulated annealing.
   */
  static final double SA_GAMMA_COOLING_FACTOR = 0.9;
  /**
   * Initial temperature of simulated annealing.
   */
  static final double SA_INITIAL_TEMPERATURE = 900;
  /**
   * Mutation probability.
   */
  static final double MUTATION_PROBABILITY = 0.01;
  /**
   * Crossover probability.
   */
  static final double CROSSOVER_PROBABILITY = 0.50;
  /**
   * Fitness function filtering factor.
   */
  static final double ALPHA_FILTERING_FACTOR = 0.85;
  /**
   * Generations limit.
   */
  static final int GEN_LIMIT = 10000;
  /**
   * Initial population size.
   */
  static final int POPULATION_SIZE = 15;
  /**
   * Taken from MasterESM_DPS_06.pdf page 33 (HEFT scheduling example).
   */
  static final double[][] ETC = { { 14, 16, 9 }, { 13, 19, 18 }, { 11, 13, 19 }, { 13, 8, 17 },
      { 12, 13, 10 }, { 13, 16, 9 }, { 7, 15, 11 }, { 5, 11, 14 }, { 18, 12, 20 }, { 21, 7, 16 } };
  /**
   * Heterogeneous Computing Environment.
   */
  static final HeterogeneousComputingEnv env =
      new HeterogeneousComputingEnv(NUM_TASKS, NUM_EXECUTORS);

  /**
   * Main function.
   *
   * @param args
   *          command line parameters
   */
  public static void main(String[] args) {

    // Initialize dependency matrix
    buildEnvironment();

    // Draw graph to visually verify
    GraphDrawer.ofGraph(env).draw();

    // Create load balancing statistics calculator
    LoadBalancingFitnessCalculator loadBal =
        new LoadBalancingFitnessCalculator(env, ALPHA_FILTERING_FACTOR);

    // Add simulated annealing to the environment
    env.setSimulatedAnnealing(
        new SimulatedAnnealing(SA_GAMMA_COOLING_FACTOR, SA_INITIAL_TEMPERATURE, loadBal,
            Optimize.MINIMUM));

    // Configure and build the evolution engine.
    final Engine<ScheduleGene, Double> engine = Engine
        .builder(loadBal::getFitness, (new ScheduleCodec(env)).ofChromosome())
          .populationSize(POPULATION_SIZE)
          .optimize(Optimize.MINIMUM)
          .selector(new RouletteWheelSelector<>())
          .alterers(
              new ScheduleMutator(env, MUTATION_PROBABILITY),
              new ScheduleCrossover(env, CROSSOVER_PROBABILITY))
          .individualCreationRetries(1)
          .build();

    // Create evolution statistics consumer.
    final ScheduleStatistics statistics = new ScheduleStatistics(env);

    // Run evolution stream
    final Phenotype<ScheduleGene, Double> best =
        engine.stream().peek(statistics).limit(GEN_LIMIT).collect(toBestPhenotype());

    statistics.showStats();
    System.out.println("Loadbalanced solution:");
    System.out.println(best);

    GraphDrawer.ofChromosome((ScheduleChromosome) best.getGenotype().getChromosome());
  }

  /**
   * Initialize the HCE.
   */
  private static void buildEnvironment() {
    GraphNode[] tasks = new GraphNode[NUM_TASKS];

    // Create all tasks
    for (int i = 0; i < ETC.length; i++) {
      tasks[i] = env.addTask(ETC[i]);
    }

    // Create dependencies
    /* Taken from MasterESM_DPS_06.pdf page 33 (HEFT scheduling example) */

    // From task 0
    env.addDependency(tasks[0], tasks[1], 18);
    env.addDependency(tasks[0], tasks[2], 12);
    env.addDependency(tasks[0], tasks[3], 9);
    env.addDependency(tasks[0], tasks[4], 11);
    env.addDependency(tasks[0], tasks[5], 14);

    // From task 1
    env.addDependency(tasks[1], tasks[7], 19);
    env.addDependency(tasks[1], tasks[8], 16);

    // From task 2
    env.addDependency(tasks[2], tasks[6], 23);

    // From task 3
    env.addDependency(tasks[3], tasks[7], 27);
    env.addDependency(tasks[3], tasks[8], 23);

    // From task 4
    env.addDependency(tasks[4], tasks[8], 13);

    // From task 5
    env.addDependency(tasks[5], tasks[7], 15);

    // To task 9
    env.addDependency(tasks[6], tasks[9], 17);
    env.addDependency(tasks[7], tasks[9], 11);
    env.addDependency(tasks[8], tasks[9], 13);
  }

}
