package alg.util.genetics;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.jenetics.util.ISeq;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import alg.util.Util;

public class ScheduleChromosomeUT {

	private Random randomGen;

	private int numTask;
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

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void checkvalidity()
	{
		double[][] matrix = new double[4][4];
		int[] chrome1 = {0,1,2,3};
		int[] chrome2 = {2,1,3,0};
		ArrayList<ScheduleGene> allelList = new ArrayList<ScheduleGene>();
		ScheduleChromosome chromosome;
		ScheduleAllele allel = null;
		
		// Define a delta matrix
		matrix[0][1] = 1;
		matrix[0][2] = 1;
		matrix[1][2] = 1;
		matrix[1][3] = 1;
		matrix[2][3] = 1;
		
		
		// Create first chromosome
		for (int task: chrome1)
		{
			allel = new ScheduleAllele(4, 4, task);
			allelList.add(new ScheduleGene(4, 4, allel));
		}
		
		chromosome = new ScheduleChromosome(matrix, 4, ISeq.of(allelList));
		assertEquals(chromosome.isValid(), true);
		
		
		// Create second chromosom
		allelList = new ArrayList<ScheduleGene>();
		for (int task: chrome2)
		{
			allel = new ScheduleAllele(4, 4, task);
			allelList.add(new ScheduleGene(4, 4, allel));
		}
		
		chromosome = new ScheduleChromosome(matrix, 4, ISeq.of(allelList));
		assertEquals(chromosome.isValid(), false);

	}
	
	@Test
	public void createCheckValid()
	{
		double[][] matrix = Util.getDPNDMatrix(numTask);
		ScheduleChromosome chromosome = new ScheduleChromosome(matrix, 4);
		
		// This shall be true everytime. If not we are creating invalid solutions
		assertEquals(chromosome.isValid(), true);
		
	}
}
