/**
 * 
 */
package alg.util;


import java.util.Arrays;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pjcuadra
 *
 */
public class UtilUT {
	
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
	public void checkDPNDMatrix()
	{
		double[][] matrix;

		matrix = Util.getDPNDMatrix(numTask);		

		System.out.println("mat"+ Arrays.deepToString(matrix));
	}
}
