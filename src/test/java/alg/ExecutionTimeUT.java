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
		
		ones = new double[executors][numTask];
		chromosome = new int[numTask];
		
		// Randomly initialize the chormosome allocating to a random node
		for (int currTask = 0; currTask < chromosome.length; currTask++)
		{
			chromosome[currTask] =  randomGen.nextInt(executors);
		}
		
		// Initialize ones matrix with ones}
		for (int currRow = 0; currRow < ones.length; currRow++)
		{
			for (int currCol = 0; currCol < ones[0].length; currCol++)
			{
				ones[currRow][currCol] = 1;
			}
		}
		
		executionTimeGA =  new ExecutionTime(ones);
		
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
	public void getSumTime()
	{
		double[] sumTime;
		
		sumTime = executionTimeGA.getSumTime(chromosome);
		
		/* 
		 * Since our ETC matrix has only ones the execution time per node
		 * should be the amount of nodes assigned to it.
		 */		
		for (int currNodeTime = 0; currNodeTime < executors; currNodeTime++)
		{
			assertEquals(sumTime[currNodeTime], getTaskAllocateOnNode(currNodeTime), 0.01);
		}

	}
	
	@Test
	public void calculateTotalTime(){
		double totalTime;
		
		totalTime = executionTimeGA.getTotalTime(chromosome);
		
		/* 
		 * Since our ETC matrix has only ones the total execution time 
		 * should be the amount of tasks.
		 */	
		assertEquals(totalTime, numTask, 0.01);
	}
	
	@Test
	public void loadImabalance(){
		executionTimeGA.getLoad(chromosome);
	}

}
