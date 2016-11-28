package examples;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import examples.OnesCounting;

/**
 * The test class OnesCountingUT.
 *
 * @author  Pedro Cuadra
 * @version 1.0
 */
public class OnesCountingUT
{
    /**
     * Default constructor for test class OnesCountingUT
     */
    public OnesCountingUT()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }

    /**
     * Draw two flowers with different location in the canvas
     */
    @Test
    public void runMain()
    {
      String[] args = {};
      System.out.println("Ran Tests!");

      OnesCounting testCounter = new OnesCounting();
      testCounter.main(args);

    }


}
