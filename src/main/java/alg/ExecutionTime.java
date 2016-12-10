/* 
 * This class should implement all the necessary methods to implement
 * Load Balancing Task Scheduling based on Multi-Population Genetic in
 * Cloud Computing (Wang Bei, LI Jun). Note that this is not actually 
 * taking into account the load balancing we try to achieve but the final 
 * result will be built on top of this. Also we are limiting the 
 * implementation to a single population. 
 *
 * Author:
 *    Pedro Cuadra
 *    Sudheera Bandi
 */

package alg;

import java.util.Arrays;
import alg.util.*;
import org.jenetics.*;
import org.jenetics.engine.Codec;
import org.jenetics.util.IntRange;

public class ExecutionTime extends Scheduler{

	/**
	 * Constructor 
	 * @param ETCMatrix Execution times matrix
	 */
	public ExecutionTime(double[][] ETCMatrix) {
		super(ETCMatrix);
	}

	/**
	 * Create a Jenetics codec for IntegerChromosome/Conv matrix encoding/decoding 
	 * @return Jenetics codec
	 */
	public Codec<int[][], IntegerGene> ofCONV()
	{		
		int numExecutors = ETC.length - 1;
		int numTask = ETC[0].length;

		return Codec.of(
				Genotype.of(IntegerChromosome.of(IntRange.of(0, numExecutors), numTask)), /*Encoder*/ 
				gt->createCONVMatrix(((IntegerChromosome)gt.getChromosome()).toArray()) /*Decoder*/
				);
	}


	/**
	 * Calculate the CONV matrix from a Chromosome
	 * 
	 * According to "Load Balancing Task Scheduling based on 
	 * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
	 * LI Jun) 
	 * 
	 * @param chromosome allocation nodes array indexed by 
	 * 					 task index
	 * 					  
	 * @return CONV matrix
	 */
	public int[][] createCONVMatrix(int[] chromosome)
	{
		int[][] CONV = new int[ETC.length][ETC[0].length];
		int currTask = 0;

		// Just set to one where it is allocated
		for (currTask = 0; currTask < chromosome.length; currTask++)
		{
			CONV[chromosome[currTask]][currTask] = 1;
		}

		return CONV;
	}


	/**
	 * Get the execution time of every node given a chromosome
	 * 
	 * According to "Load Balancing Task Scheduling based on 
	 * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
	 * LI Jun), equation (1)
	 * 
	 * @param convMatrix allocation nodes matrix
	 * 
	 * @return the sum of execution times per node as an array
	 *         indexed by node index 
	 */
	public double[] getSumTime(int[][] convMatrix)
	{
		double[] sumTime = new double[convMatrix.length];
		int i = 0, j = 0;

		// Iterate over the tasks
		for (i = 0; i < convMatrix.length; i++)
		{
			sumTime[i] = 0;

			// Iterate over the nodes
			for (j = 0; j < convMatrix[i].length; j++)
			{
				// Add all execution times
				sumTime[i] += convMatrix[i][j]*ETC[i][j];
			}
		}		

		return sumTime;
	}

	/**
	 * Get total execution time given a Chromosome
	 * 
	 * According to "Load Balancing Task Scheduling based on 
	 * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
	 * LI Jun), equation (2)
	 * 
	 * @param convMatrix allocation nodes matrix
	 * 
	 * @return total execution time of a given Chromosome
	 */
	public double getTotalTime(int[][] convMatrix)
	{
		double[] sumTime = getSumTime(convMatrix);
		double totalTime = 0;

		for (double time : sumTime)
		{
			if (time > totalTime)
			{
				totalTime = time;
			}
		}

		return totalTime;
	}

	/**
	 * The fitness function to calculate fitness of a given Chromosome
	 * 
	 * According to "Load Balancing Task Scheduling based on 
	 * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
	 * LI Jun), equation (7)
	 * 
	 * @param chromosome allocation nodes array indexed by 
	 * 					 task index
	 * @param Etfactor factor to use when adding dependencies to the fitness func; 
	 *          Etfactor can be modified to tweak the fitness function ass reqd;
	 *          set to 1 as of now.
	 * @return fitness of a given Chromosome
	 */
	public double getFitness(int[][] convMatrix)
	{
		double TotalTime = getTotalTime(convMatrix);
		double Fitness = 0;

		Fitness = (1/TotalTime);

		return Fitness;
	}


	/**
	 * Get the load imbalance of given Chromosome
	 * 
	 * According to "Load Balancing Task Scheduling based on 
	 * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
	 * LI Jun), equation (4)
	 * 
	 * @param convMatrix allocation nodes matrix
	 * 
	 * @return load imbalance of a Chromosome
	 */
	public double getLoad(int[][] convMatrix)
	{
		double[] sumTime = getSumTime(convMatrix);
		double load = 0;
		double avgTime = getTotalTime(convMatrix)/convMatrix.length;

		// First calculate sum = (sumTime(i) - averageTime)^2
		for (double time: sumTime)
		{
			load += Math.pow(time - avgTime, 2);
		}

		// Now multiply the sum with 1/(M - 1)
		load = load/((double)(sumTime.length -1));

		// And finally take the square root
		load = Math.sqrt(load);

		return load;
	}

	/**
	 * Check the validity of given Chromosome
	 * 
	 * A fast hybrid genetic algorithm in heterogeneous computing environment
	 * Zhiyang Jiang, Shengzhong Feng
	 * Shenzhen Institute of Advanced Technology, Chinese Academy of Sciences 
	 * 
	 * @param DPNDMatrix dependency of tasks matrix
	 * 
	 * @return Validity  boolean to represent validity of Chromosome acc to 
	 * 				     dependency matrix
	 */

	public static boolean getValidityOfChrm(double[][]DPNDMatrix,int[]TaskArray){	
		
		int numtasks = DPNDMatrix.length; //square matrix with rows=cols=num of tasks
		double[][]tempmat = new double[numtasks][numtasks];
		int numofones = 0;

		tempmat = Util.copyMatrix(DPNDMatrix,tempmat);
		
		//only execute as long as order is valid
		for(int iterator:TaskArray)
		{

			for(int i=0;i<numtasks;i++)
			{
				/*check if there is a dependency with a successive task
				check for ones in the cloumn*/
				numofones += tempmat[i][iterator];
			}
			if (numofones != 0)
			{
				return false;
			}
			//to clear the elements of the row		
			for(int j=0;j<numtasks;j++){
				tempmat[iterator][j] = 0;
			}
			

		}

		return true;

	}


}
