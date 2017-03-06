/**
 * Schedule Chromosome unit testing.
 */

package alg.util.genetics;

import static org.junit.Assert.assertEquals;

import alg.util.HCE;
import alg.util.Util;

import org.jenetics.util.ISeq;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * Schedule Chromosome unit testing.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
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
    
    HCE env = new HCE(matrix, matrix);


    // Already known valid solution
    int[] chromosomeSeq1 = {0, 1, 2, 3};

    // Create first chromosome
    for (int task: chromosomeSeq1) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    ScheduleChromosome chromosome;
    chromosome = new ScheduleChromosome(env, ISeq.of(allelList));
    assertEquals(chromosome.isValid(), true);


    // Already known invalid solution
    int[] chromosomeSeq2 = {0, 2, 1, 3};

    // Create second chromosom
    allelList = new ArrayList<ScheduleGene>();
    for (int task: chromosomeSeq2) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    chromosome = new ScheduleChromosome(env, ISeq.of(allelList));
    assertEquals(chromosome.isValid(), false);

    // Already known invalid solution (repeat task)
    int[] chromosomeSeq3 = {0,1,2,0};

    // Create second chromosome
    allelList = new ArrayList<ScheduleGene>();
    for (int task: chromosomeSeq3) {
      allel = new ScheduleAllele(4, 4, task);
      allelList.add(new ScheduleGene(4, 4, allel));
    }

    chromosome = new ScheduleChromosome(env, ISeq.of(allelList));
    assertEquals(chromosome.isValid(), false);

  }

  @Test
  public void createCheckValid() {
    double[][] matrix = Util.getDeltaMatrix(numTask);
    HCE env = new HCE(matrix, matrix);
    ScheduleChromosome chromosome = new ScheduleChromosome(env);

    // This shall be true everytime. If not we are creating invalid solutions
    assertEquals(chromosome.isValid(), true);

  }
  
  @Test
  public void cloneChromosome() {
    double[][] matrix = Util.getDeltaMatrix(numTask);
    HCE env = new HCE(matrix, matrix);
    ScheduleChromosome chromosomeOrg = new ScheduleChromosome(env);
    ScheduleChromosome chromosomeClone = chromosomeOrg.clone();
    ScheduleGene original;
    ScheduleGene cloned;

    for (int locus = 0; locus < chromosomeOrg.toSeq().size(); locus++) {
      
      original =  chromosomeOrg.getGene(locus);
      cloned =  chromosomeClone.getGene(locus);
      assertEquals(original, cloned);
    }
   

  }
  
  @Test
  public void knowChromosome() {
    double[][] matrix = Util.createEmptyMatrix(5, 5);
    
    HCE env = new HCE(matrix, matrix);
    
    
    /* (1) -> (2) -> (3) -> (4) -> (5) */
    matrix[0][1] = 1;
    matrix[1][2] = 1;
    matrix[2][3] = 1;
    matrix[3][4] = 1;
    
    ScheduleChromosome chromosome = new ScheduleChromosome(env);

    for (int currGene = 0; currGene < chromosome.toSeq().size(); currGene ++) {
      assertEquals(chromosome.toSeq().get(currGene).getAllele().getTaskId(), currGene);
      
    }

  }
}
