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

import org.jenetics.Gene;

/**
 * Schedule gene.
 * 
 * @author Pedro Cuadra
 *
 */
public class ScheduleGene implements Gene<ScheduleAllele, ScheduleGene> {
  /**
   * Allele of the gene.
   */
  private ScheduleAllele allele;
  /**
   * Heterogeneous Computing Environment.
   */
  private HeterogeneousComputingEnv env;

  /**
   * Constructor.
   * 
   * @param env
   *          Heterogeneous Computing Environment
   */
  public ScheduleGene(HeterogeneousComputingEnv env) {
    this.env = env;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jenetics.util.Verifiable#isValid()
   */
  @Override
  public boolean isValid() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jenetics.Gene#getAllele()
   */
  @Override
  public ScheduleAllele getAllele() {
    return allele;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jenetics.Gene#newInstance()
   */
  @Override
  public ScheduleGene newInstance() {
    return ScheduleGene.ofRandom(env);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jenetics.Gene#newInstance(java.lang.Object)
   */
  @Override
  public ScheduleGene newInstance(ScheduleAllele allele) {
    return ScheduleGene.ofAllele(env, allele);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return allele.toString();
  }

  /**
   * Random gene builder.
   * 
   * @param env
   *          Heterogeneous Computing Environment
   * @return newly created gene
   */
  public static ScheduleGene ofRandom(HeterogeneousComputingEnv env) {
    ScheduleGene gene = new ScheduleGene(env);
    gene.allele = ScheduleAllele.ofRandom(env);
    return gene;
  }

  /**
   * Gene builder for a given Allele.
   * 
   * @param env
   *          Heterogeneous Computing Environment
   * @param allele
   *          allele to be assigned to the gene
   * @return newly created gene
   */
  public static ScheduleGene ofAllele(HeterogeneousComputingEnv env, ScheduleAllele allele) {
    ScheduleGene gene = new ScheduleGene(env);
    gene.allele = allele;
    return gene;
  }

  /**
   * Mutate a gene by setting a new allele.
   * 
   * @param allele
   *          new allele
   */
  public void mutate(ScheduleAllele allele) {
    this.allele = allele;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object object) {
    ScheduleGene gene;

    if (!(object instanceof ScheduleGene)) {
      return false;
    }

    gene = (ScheduleGene) object;

    return gene.allele.equals(this.allele);

  }

}
