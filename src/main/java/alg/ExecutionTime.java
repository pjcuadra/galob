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

public class ExecutionTime {
	
	/* 
	 * Execution Time Cost matrix with ETC[i][j] where i is the
	 * node and j is the task index. 
	 */ 
	private double[][] ETC;
	
	// The ETC is need from the beginning of time
	public ExecutionTime(double[][] ETC)
	{
		this.ETC = ETC;
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
	private double[][] createCONVMatrix(int[] chromosome)
	{
		double[][] CONV = new double[ETC.length][ETC[0].length];
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
	 * @param chromosome allocation nodes array indexed by 
	 * 					 task index
	 * @return the sum of execution times per node as an array
	 *         indexed by node index 
	 */
	public double[] getSumTime(int[] chromosome)
	{
		double[][] CONV = createCONVMatrix(chromosome);
		double[] sumTime = new double[CONV.length];
		int i = 0, j = 0;
		
		// Iterate over the tasks
		for (i = 0; i < CONV.length; i++)
		{
			sumTime[i] = 0;
					
			// Iterate over the nodes
			for (j = 0; j < CONV[i].length; j++)
			{
				// Add all execution times
				sumTime[i] += CONV[i][j]*ETC[i][j];
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
	 * @param chromosome allocation nodes array indexed by 
	 * 					 task index
	 * 
	 * @return total execution time of a given Chromosome
	 */
	public double getTotalTime(int[] chromosome)
	{
		double[] sumTime = getSumTime(chromosome);
		double totalTime = 0;
		
		for (double time : sumTime)
		{
			totalTime += time;
		}
		
		return totalTime;
	}
	
	
	/**
	 * Get the load imbalance of given Chromosome
	 * 
	 * According to "Load Balancing Task Scheduling based on 
	 * Multi-Population Genetic in Cloud Computing" (Wang Bei, 
	 * LI Jun), equation (4)
	 * 
	 * @param chromosome allocation nodes array indexed by 
	 * 					 task index
	 * @return load imbalance of a Chromosome
	 */
	public double getLoad(int[] chromosome)
	{
		double[] sumTime = getSumTime(chromosome);
		double load = 0;
		double avgTime = getTotalTime(chromosome)/chromosome.length;
		
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
	
}
