/**
 * Scheduler class abstraction.
 */

package alg.util.genetics;

import alg.util.HeterogeneousComputingEnv;
import alg.util.genetics.ScheduleUtil;

import org.jenetics.Genotype;
import org.jenetics.engine.Codec;
import org.jenetics.util.ISeq;

/**
 * Scheduler class abstraction.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class ScheduleCodec {
  /**
   * Heterogeneous computing environment.
   */
  protected HeterogeneousComputingEnv env;

  /**
   * Constructor.
   * 
   * @param env heterogeneous computing environment
   */
  public ScheduleCodec(HeterogeneousComputingEnv env) {
    this.env = env;
  }

  /**
   * Create a Jenetics codec for allocation matrix encoding/decoding.
   * 
   * @return Jenetics codec
   */
  public Codec<int[][], ScheduleGene> ofOmega() {
    int numExecutors = env.getNumberOfExecutors();

    return Codec.of(
        Genotype.of(ScheduleChromosome.of(env)), /*Encoder*/ 
        gt -> ScheduleUtil.createOmegaMatrix(((ScheduleChromosome)gt.getChromosome()).toSeq(), 
            numExecutors) /*Decoder*/
        );
  }

  /**
   * Create a Jenetics codec for schedule sequence encoding/decoding.
   * 
   * @return Jenetics codec
   */
  public Codec<ISeq<ScheduleGene>, ScheduleGene> ofSeq() {
    return Codec.of(
        Genotype.of(ScheduleChromosome.of(env)), /*Encoder*/ 
        gt -> ((ScheduleChromosome)gt.getChromosome()).toSeq() /*Decoder*/
        );
  }

  /**
   * Create a Jenetics codec for ScheduleChromosome encoding/decoding.
   * 
   * @return Jenetics codec
   */
  public Codec<ScheduleChromosome, ScheduleGene> ofChromosome() {
    return Codec.of(
        Genotype.of(ScheduleChromosome.of(env)), /*Encoder*/ 
        gt -> ((ScheduleChromosome)gt.getChromosome()) /*Decoder*/
        );
  }

}