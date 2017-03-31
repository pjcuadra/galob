package de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics;

import static org.junit.Assert.assertEquals;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;

import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.junit.Before;
import org.junit.Test;

public class ScheduleCodecTest {
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
   * Test Codec.
   */
  private Codec<ScheduleChromosome, ScheduleGene> testCodec;

  /**
   * Unit testing set-up.
   *
   * @throws Exception
   *           falure exception
   */
  @Before
  public void setUp() throws Exception {

    env = HeterogeneousComputingEnv.ofRandom(MAX_NUM_TASKS, MAX_NUM_CORES, true);
    testCodec = (new ScheduleCodec(env)).ofChromosome();

  }

  @Test
  public void testOfChromosome() throws Exception {
    ScheduleChromosome chr = ScheduleChromosome.of(env);
    Genotype<ScheduleGene> gt = Genotype.of(chr);

    assertEquals(chr, testCodec.decode(gt));
  }

}
