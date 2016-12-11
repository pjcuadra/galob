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

import alg.util.Util;
import alg.util.genetics.ScheduleChromosome;
import alg.util.genetics.ScheduleGene;

/**
 * @author Pedro Cuadra
 *
 */
public class ExecutionTimeUT {

	private Random randomGen;

	private double[][] ones;
	private double[][] delta;
	private ScheduleChromosome chromosome;
	private ExecutionTime executionTimeGA;
	private int numTask;
	private int executors;
	private int[][] convMatrix;
	final static int maxNumTask = 16 /* Actual max*/  /* 0 is not possible so add 1 after*/;
	final static int maxNumExecutors = 16 /* Actual max*/  /* 0 is not possible so add 1 after*/;

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
		delta = Util.getDPNDMatrix(numTask);
		
		chromosome = new ScheduleChromosome(delta, executors);
		
		executionTimeGA =  new ExecutionTime(ones, delta);

		convMatrix = executionTimeGA.createOmegaMatrix(chromosome.toSeq());

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

		for (ScheduleGene currGen: chromosome.toSeq())
		{
			if (currGen.getAllele().getExecutorID() == node)
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
			assertEquals(getTaskAllocateOnNode(currNodeTime), sumTime[currNodeTime], 0.001);
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

		assertEquals(fitness, ((double)getMaxTasksAllocToOneNode()), 0.01);
		System.out.println(fitness);
	}

	

	

	@Test
	public void loadImbalance(){

		executionTimeGA.getLoad(convMatrix);
	}

}
