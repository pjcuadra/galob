/**
 * 
 */
package alg;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import alg.util.Util;

/**
 * @author Pedro Cuadra
 *
 */
public class ExecutionTimeUT {

	private Random randomGen;

	private double[][] ones;
	private int[] chromosome;
	private ExecutionTime executionTimeGA;
	private int numTask;
	private int executors;
	private int[][] convMatrix;
	final static int maxNumTask = 16 /* Actual max*/ - 1 /* 0 is not possible so add 1 after*/;
	final static int maxNumExecutors = 16 /* Actual max*/  - 1 /* 0 is not possible so add 1 after*/;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		randomGen = new Random();

		numTask =  1 + randomGen.nextInt(maxNumTask);
		executors =  1 + randomGen.nextInt(maxNumExecutors);

		ones = Util.getOnesMatrix(executors, numTask);
		chromosome = new int[numTask];

		// Randomly initialize the chromosome allocating to a random node
		for (int currTask = 0; currTask < chromosome.length; currTask++)
		{
			chromosome[currTask] =  randomGen.nextInt(executors);
		}

		executionTimeGA =  new ExecutionTime(ones);

		convMatrix = executionTimeGA.createCONVMatrix(chromosome);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}


	/**
	 * Get the amount of task allocated to a given node
	 * @param node node index
	 * @return amount of task allocated to given node
	 */
	private int getTaskAllocateOnNode(int node)
	{
		int amount = 0;

		for (int currGen: chromosome)
		{
			if (currGen == node)
			{
				amount++;
			}
		}

		return amount;

	}

	/**
	 * Get the maximum number of task allocated to one node
	 * of all the given nodes in the chromosome
	 * @return maximum amount of tasks allocated to a node
	 */
	private int getMaxTasksAllocToOneNode(){
		int maxnumtasks = 0;
		int tempmaxnumtasks = 0;
		for (int i = 0; i < executors; i++)
		{
			tempmaxnumtasks = getTaskAllocateOnNode(i);

			if (tempmaxnumtasks > maxnumtasks)
			{
				maxnumtasks = tempmaxnumtasks;
			}
		}
		return maxnumtasks;
	}

	@Test
	public void getSumTime(){
		double[] sumTime;

		sumTime = executionTimeGA.getSumTime(convMatrix);

		/* 
		 * Since our ETC matrix has only ones the execution time per node
		 * should be the amount of nodes assigned to it.
		 */		
		for (int currNodeTime = 0; currNodeTime < executors; currNodeTime++)
		{
			assertEquals(getTaskAllocateOnNode(currNodeTime), sumTime[currNodeTime], 0.01);
		}

	}

	@Test
	public void calculateTotalTime(){

		double totalTime = 0;
		double expectedTotalTime = 0;

		totalTime = executionTimeGA.getTotalTime(convMatrix);

		expectedTotalTime = getMaxTasksAllocToOneNode();
		/* 
		 * Since our ETC matrix has only ones the total execution time 
		 * should be the amount of tasks.
		 */	

		assertEquals(expectedTotalTime, totalTime,0.01);

	}

	@Test
	public void calculateFitness(){
		double fitness;

		fitness = executionTimeGA.getFitness(convMatrix);

		/* 
		 * Since our ETC matrix has only ones the fitness value
		 * should be 1/number of tasks in one node.
		 */	

		assertEquals(fitness, ((double)1/getMaxTasksAllocToOneNode()), 0.01);
		System.out.println(fitness);
	}

	@Test
	public void checkDPNDMatrix()
	{
		int[][] matrix;

		matrix = Util.getDPNDMatrix(numTask);		

		System.out.println("mat"+ Arrays.deepToString(matrix));
	}

	@Test
	public void checkvalidity()
	{
	    double[][] matrix = new double[4][4];
		int[] chrome1 = {0,1,2,3};
		int[] chrome2 = {2,1,3,0};
	
		
		matrix[0][1] = 1;
		matrix[0][2] = 1;
		matrix[1][2] = 1;
		matrix[1][3] = 1;
		matrix[2][3] = 1;
		
        System.out.println("mat"+ Arrays.deepToString(matrix));
		assertEquals((ExecutionTime.getValidityOfChrm(matrix,chrome1)),true);
		assertEquals((ExecutionTime.getValidityOfChrm(matrix,chrome2)),false);
		
	}

	@Test
	public void loadImbalance(){

		executionTimeGA.getLoad(convMatrix);
	}

}
