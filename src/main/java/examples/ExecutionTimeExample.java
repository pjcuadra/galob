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

package examples;

import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import alg.ExecutionTimeFitnessCalculator;
import alg.util.HeterogeneousComputingEnv;
import alg.util.SimulatedAnnealing;
import alg.util.jenetics.ScheduleCodec;
import alg.util.jenetics.ScheduleCrossover;
import alg.util.jenetics.ScheduleGene;
import alg.util.jenetics.ScheduleMutator;

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
  static final double SA_GAMMA_COOLING_FACTOR = 0.85;
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
  static final double CROSSOVER_PROBABILITY = 0.80;
  /**
   * Fitness function filtering factor.
   */
  static final double ALPHA_FILTERING_FACTOR = 0.8;
  /**
   * Generations limit.
   */
  static final int GEN_LIMIT = 10000;
  /**
   * Initial population size.
   */
  static final int POPULATION_SIZE = 15;
  /**
   * Heterogeneous Computing Environment.
   */
  static final HeterogeneousComputingEnv env = HeterogeneousComputingEnv.ofRandom(NUM_TASKS, 
      NUM_EXECUTORS, 
      false);

  /**
   * Main function.
   * @param args command line parameters
   */
  public static void main(String[] args) {
    
    // Create a code
    ExecutionTimeFitnessCalculator fitnessCalc = new ExecutionTimeFitnessCalculator(env);

    // Set the simulated annealing to the environment
    env.setSimulatedAnnealing(new  SimulatedAnnealing(SA_GAMMA_COOLING_FACTOR, 
        SA_INITIAL_TEMPERATURE,
        fitnessCalc));

    // Configure and build the evolution engine.
    final Engine<alg.util.jenetics.ScheduleGene, Double> engine = Engine
        .builder(
            fitnessCalc::getFitness,
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

    System.out.println(statistics);
    System.out.println("Solution:");
    System.out.println(best);

  }

}
