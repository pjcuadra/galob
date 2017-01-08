/**
 * Schedule Crossover unit testing.
 */

package alg.util.genetics;

import static org.junit.Assert.assertEquals;

import alg.util.Util;

import org.jenetics.util.MSeq;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

/**
 * Schedule Crossover unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ScheduleCrossoverUt {

  private Random randomGen;

  private int numTask;
  private int executors;
  static final int maxNumTask = 9 /* Actual max*/;
  static final int maxNumExecutors = 6 /* Actual max*/;
  static final int maxPopulation = 50 /* Actual max*/;
  private ScheduleCrossover crossover;
  private double[][] delta;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * Unit testing set-up.
   * 
   * @throws Exception falure exception
   */
  @Before
  public void setUp() throws Exception {

    randomGen = new Random();

    numTask =  1 + randomGen.nextInt(maxNumTask);
    executors =  1 + randomGen.nextInt(maxNumExecutors);

    // Create new dependencies randomly
    delta = Util.getDeltaMatrix(numTask);

    // Always crossover
    crossover = new ScheduleCrossover(delta, 1);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void singleCrossover() {
    ScheduleChromosome p1Chromosome = new ScheduleChromosome(delta, executors);
    ScheduleChromosome p2Chromosome = new ScheduleChromosome(delta, executors);
    MSeq<ScheduleGene> p1Seq = p1Chromosome.toSeq().copy();
    MSeq<ScheduleGene> p2Seq = p2Chromosome.toSeq().copy();
    int alterations = 0;
    int alterationsCount = 0;

    // Crossover the chromosome
    alterations = crossover.crossover(p1Seq, p2Seq);
    
    // Check that the Gene sequence changed
    if (!p1Seq.equals(p1Chromosome.toSeq().copy())) {
      alterationsCount++;
    }
    
    // Check that the Gene sequence changed
    if (!p2Seq.equals(p2Chromosome.toSeq().copy())) {
      alterationsCount++;
    }
    
    assertEquals(alterationsCount, alterations);
    
  }

 
     
}
