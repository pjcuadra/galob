/**
 * Schedule Crossover unit testing.
 */

package alg.util.genetics;

import static org.junit.Assert.assertEquals;

import alg.util.Util;

import org.jenetics.Chromosome;
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

  private int countAltersOfChromosome(Chromosome<ScheduleGene> originalChromosome, 
      Chromosome<ScheduleGene> childChromosome) {
    int alterationsCount = 0;
    ScheduleGene expected;
    ScheduleGene actual;
    // Count number of alterations
    for (int locus = 0; locus < originalChromosome.toSeq().size(); locus++) {

      expected =  originalChromosome.getGene(locus);
      actual =  childChromosome.getGene(locus);

      if (!expected.equals(actual)) {
        alterationsCount++;
      }

    }

    return alterationsCount;
  }

  @Test
  public void singleCrossover() {
    ScheduleChromosome parentChromosome1 = new ScheduleChromosome(delta, executors);
    ScheduleChromosome parentChromosome2 = new ScheduleChromosome(delta, executors);
    ScheduleChromosome childChromosome = parentChromosome1.clone();
    int alterations = 0;
    int alterationsCount = 0;

    // Crossover the chromosome
    alterations = crossover.crossover(parentChromosome1.toSeq().copy(), 
        parentChromosome2.toSeq().copy());

    alterationsCount = countAltersOfChromosome(parentChromosome1, childChromosome);
    
    assertEquals(alterationsCount, alterations);
    
  }

 
     
}
