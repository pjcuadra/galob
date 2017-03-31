package de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class ScheduleAlleleTest {
  /**
   * Random generator.
   */
  private Random randomGen;
  /**
   * Max. number of tasks.
   */
  private static final int MAX_NUM_TASKS = 16;
  /**
   * Max. number cores.
   */
  private static final int MAX_NUM_CORES = 16;
  /**
   * Number of tasks.
   */
  private int taskId;
  /**
   * Number of cores.
   */
  private int coreId;
  /**
   * Allele.
   */
  private ScheduleAllele allele;
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
    randomGen = new Random();

    env = HeterogeneousComputingEnv.ofRandom(MAX_NUM_TASKS, MAX_NUM_CORES, true);

    taskId = randomGen.nextInt(env.getNumberOfTasks());
    coreId = randomGen.nextInt(env.getNumberOfExecutors());

    allele = ScheduleAllele.of(env, taskId, coreId);

  }

  @Test
  public void testGetExecutorId() throws Exception {
    assertEquals(coreId, allele.getExecutorId());
  }

  @Test
  public void testGetTaskId() throws Exception {
    assertEquals(taskId, allele.getTaskId());
  }

  @Test
  public void testOfTask() throws Exception {
    int newTaskId = randomGen.nextInt(env.getNumberOfTasks());

    allele = ScheduleAllele.ofTask(env, newTaskId);

    assertEquals(newTaskId, allele.getTaskId());

    assertTrue(allele.getExecutorId() < env.getNumberOfExecutors());

  }

  @Test
  public void testOfRandom() throws Exception {
    allele = ScheduleAllele.ofRandom(env);

    assertTrue(allele.getTaskId() < env.getNumberOfTasks());
    assertTrue(allele.getExecutorId() < env.getNumberOfExecutors());
  }

  @Test
  public void testOfExecutor() throws Exception {
    int newcoreId = randomGen.nextInt(env.getNumberOfExecutors());

    allele = ScheduleAllele.ofExecutor(env, newcoreId);

    assertEquals(newcoreId, allele.getExecutorId());

    assertTrue(allele.getTaskId() < env.getNumberOfTasks());
  }

  @Test
  public void testOf() throws Exception {
    // Summy test. of() is being used for every test
    assertTrue(true);
  }

}
