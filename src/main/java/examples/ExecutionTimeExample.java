package examples;

import alg.ExecutionTime;
import static org.jenetics.engine.EvolutionResult.toBestPhenotype;
import static org.jenetics.engine.limit.bySteadyFitness;

import org.jenetics.IntegerGene;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;

import alg.util.Util;

public class ExecutionTimeExample {
	
	final static int numTask = 16;
	final static int numExecutors = 3;
	
	public static void main(String[] args)
	{
		double[][] myETC = Util.getOnesMatrix(numExecutors, numTask);
		ExecutionTime etOpt = new ExecutionTime(myETC);
		
		for (int o = 0; o < myETC[0].length; o++)
		{
			myETC[0][o] = 1.5;
		}
		
		// Configure and build the evolution engine.
		final Engine<IntegerGene, Double> engine = Engine
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
		final EvolutionStatistics<Double, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<IntegerGene, Double> best = engine.stream()
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
		
		System.out.println(statistics);
		System.out.println(best);
		
	}

}
