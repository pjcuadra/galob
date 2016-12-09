/**
 * 
 */
package alg;

import java.util.Random;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import alg.util.ExecutionTimeUtil;

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
		
		ones = ExecutionTimeUtil.getOnesMatrix(executors, numTask);
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
		double tempTotalTime = 0;
		
		totalTime = executionTimeGA.getTotalTime(convMatrix);
		
		for (int i = 0; i < executors; i++)
		{
			tempTotalTime = getTaskAllocateOnNode(i);
			
			if (tempTotalTime > expectedTotalTime)
			{
				expectedTotalTime = tempTotalTime;
			}
		}
		
		/* 
		 * Since our ETC matrix has only ones the total execution time 
		 * should be the amount of tasks.
		 */	

		assertEquals(expectedTotalTime, totalTime,0.01);
		
	}
	
	@Test
	public void calculateFitness(){
		double fitness;
		
		/*becuase we only have execution time as a factor to 
		calculate fitness for now we set the ETfactor to 1
		*/
		
		fitness = executionTimeGA.getFitness(chromosome,1);
		
		/* 
		 * Since our ETC matrix has only ones the fitness value
		 * should be 1/number of tasks.
		 */	
		assertEquals(fitness, ((double)1/numTask), 0.01);
		System.out.println(fitness);
	}
	
	@Test
	public void loadImbalance(){
		
		executionTimeGA.getLoad(convMatrix);
	}

}
