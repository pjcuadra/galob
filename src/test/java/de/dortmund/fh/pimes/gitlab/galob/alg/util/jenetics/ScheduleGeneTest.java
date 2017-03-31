package de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;

import org.junit.Before;
import org.junit.Test;

public class ScheduleGeneTest {
  /**
   * Maximum number of tasks.
   */
  static final int MAX_NUM_TASKS = 16 /* Actual max */ /* 0 is not possible so add 1 after */;
  /**
   * Maximum number of executors.
   */
  static final int MAX_NUM_CORES = 16 /* Actual max */ /* 0 is not possible so add 1 after */;
  /**
   * HCE.
   */
  private HeterogeneousComputingEnv env;

  /**
   * Unit testing set-up.
   *
   * @throws Exception
   *           falure exception
   */
  @Before
  public void setUp() throws Exception {

    env = HeterogeneousComputingEnv.ofRandom(MAX_NUM_TASKS, MAX_NUM_CORES, true);

  }

  @Test
  public void testOfRandom() throws Exception {
    ScheduleGene gene = ScheduleGene.ofRandom(env);

    assertTrue(gene.getAllele().getTaskId() < env.getNumberOfTasks());
    assertTrue(gene.getAllele().getExecutorId() < env.getNumberOfExecutors());
  }

  @Test
  public void testOfAllele() throws Exception {
    ScheduleAllele allele = ScheduleAllele.ofRandom(env);
    ScheduleGene gene = ScheduleGene.ofAllele(env, allele);

    assertTrue(gene.getAllele().getTaskId() < env.getNumberOfTasks());
    assertTrue(gene.getAllele().getExecutorId() < env.getNumberOfExecutors());

    assertEquals(allele, gene.getAllele());
    assertEquals(allele.getExecutorId(), gene.getAllele().getExecutorId());
    assertEquals(allele.getTaskId(), gene.getAllele().getTaskId());
  }

  @Test
  public void testMutate() throws Exception {
    ScheduleAllele allele = ScheduleAllele.ofRandom(env);
    ScheduleGene gene = ScheduleGene.ofRandom(env);

    assertTrue(gene.getAllele().getTaskId() < env.getNumberOfTasks());
    assertTrue(gene.getAllele().getExecutorId() < env.getNumberOfExecutors());

    if (allele.equals(gene.getAllele())) {
      return;
    }

    // Check that the random allele is different that gene's
    assertNotEquals(allele, gene.getAllele());

    // Mutate
    gene.mutate(allele);

    // Check that now are equals
    assertEquals(allele, gene.getAllele());

  }

}
