
package alg.util.genetics;

import static org.junit.Assert.assertEquals;

import alg.util.Util;

import org.jenetics.util.ISeq;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

public class ScheduleChromosomeUt {

  private Random randomGen;

  private int numTask;
  static final int maxNumTask = 16 /* Actual max*/  /* 0 is not possible so add 1 after*/;
  static final int maxNumExecutors = 16 /* Actual max*/  /* 0 is not possible so add 1 after*/;

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

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void checkvalidity() {
    double[][] matrix = new double[4][4];
    ArrayList<ScheduleGene> allelList = new ArrayList<ScheduleGene>();
    ScheduleAllele allel = null;

    // Define a delta matrix
    matrix[0][1] = 1;
    matrix[0][2] = 1;
    matrix[1][2] = 1;
    matrix[1][3] = 1;
    matrix[2][3] = 1;


    // Already known valid solution
    int[] chromosomeSeq1 = {0, 1, 2, 3};
    
    // Create first chromosome
    for (int task: chromosomeSeq1) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    ScheduleChromosome chromosome;
    chromosome = new ScheduleChromosome(matrix, 4, ISeq.of(allelList));
    assertEquals(chromosome.isValid(), true);


    // Already known invalid solution
    int[] chromosomeSeq2 = {0, 1, 2, 3};
    
    // Create second chromosom
    allelList = new ArrayList<ScheduleGene>();
    for (int task: chromosomeSeq2) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    chromosome = new ScheduleChromosome(matrix, 4, ISeq.of(allelList));
    assertEquals(chromosome.isValid(), false);

    // Already known invalid solution (repeat task)
    int[] chromosomeSeq3 = {0,1,2,0};
    
    // Create second chromosome
    allelList = new ArrayList<ScheduleGene>();
    for (int task: chromosomeSeq3) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    chromosome = new ScheduleChromosome(matrix, 4, ISeq.of(allelList));
    assertEquals(chromosome.isValid(), false);

  }

  @Test
  public void createCheckValid() {
    double[][] matrix = Util.getDPNDMatrix(numTask);
    ScheduleChromosome chromosome = new ScheduleChromosome(matrix, 4);

    // This shall be true everytime. If not we are creating invalid solutions
    assertEquals(chromosome.isValid(), true);

  }
}
