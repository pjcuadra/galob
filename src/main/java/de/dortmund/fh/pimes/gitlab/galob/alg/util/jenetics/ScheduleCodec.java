/*
 * Genetic Algorithm for Load Balancing
 * Copyright (c) 2016-2017 Pedro Cuadra & Sudheera Reddy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Pedro Cuadra (pjcuadra@gmail.com)
 *    Sudheera Reddy
 */

package de.dortmund.fh.pimes.gitlab.galob.alg.util.jenetics;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;
import de.dortmund.fh.pimes.gitlab.galob.alg.util.Util;

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
   * @param env
   *          heterogeneous computing environment
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

    return Codec.of(Genotype.of(ScheduleChromosome.of(env)), /* Encoder */
        gt -> Util.createOmegaMatrix(((ScheduleChromosome) gt.getChromosome()).toSeq(),
            numExecutors) /* Decoder */
    );
  }

  /**
   * Create a Jenetics codec for schedule sequence encoding/decoding.
   * 
   * @return Jenetics codec
   */
  public Codec<ISeq<ScheduleGene>, ScheduleGene> ofSeq() {
    return Codec.of(Genotype.of(ScheduleChromosome.of(env)), /* Encoder */
        gt -> ((ScheduleChromosome) gt.getChromosome()).toSeq() /* Decoder */
    );
  }

  /**
   * Create a Jenetics codec for ScheduleChromosome encoding/decoding.
   * 
   * @return Jenetics codec
   */
  public Codec<ScheduleChromosome, ScheduleGene> ofChromosome() {
    return Codec.of(Genotype.of(ScheduleChromosome.of(env)), /* Encoder */
        gt -> ((ScheduleChromosome) gt.getChromosome()) /* Decoder */
    );
  }

}