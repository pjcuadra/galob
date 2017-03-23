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

package de.dortmund.fh.pimes.gitlab.alg.util.jenetics;

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
   * Number of tasks.
   */
  private int numTasks;
  /**
   * Number of executing units.
   */
  private int numExecutors;

  /**
   * Constructor.
   * 
   * @param numTasks number of tasks
   * @param numExecutors number of executing units
   */
  public ScheduleGene(int numTasks, int numExecutors) {
    this.numTasks = numTasks;
    this.numExecutors = numExecutors;

    allele = new ScheduleAllele(numTasks, numExecutors);
  }

  /**
   * Constructor.
   * 
   * @param numTasks number of tasks
   * @param numExecutors number of executing units
   * @param allele existing allele
   */
  public ScheduleGene(int numTasks, int numExecutors, ScheduleAllele allele) {
    this.numTasks = numTasks;
    this.numExecutors = numExecutors;

    this.allele = allele;
  }

  /* (non-Javadoc)
   * @see org.jenetics.util.Verifiable#isValid()
   */
  @Override
  public boolean isValid() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.jenetics.Gene#getAllele()
   */
  @Override
  public ScheduleAllele getAllele() {
    return allele;
  }


  /* (non-Javadoc)
   * @see org.jenetics.Gene#newInstance()
   */
  @Override
  public ScheduleGene newInstance() {
    return new ScheduleGene(numTasks, numExecutors);
  }

  /* (non-Javadoc)
   * @see org.jenetics.Gene#newInstance(java.lang.Object)
   */
  @Override
  public ScheduleGene newInstance(ScheduleAllele value) {
    return new ScheduleGene(numTasks, numExecutors, value);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return allele.toString();
  }

  /**
   * Static constructor.
   * 
   * @param numTasks number of tasks
   * @param numExecutors number of executors
   * @return newly created gene
   */
  public static ScheduleGene of(int numTasks, int numExecutors) {
    return new ScheduleGene(numTasks, numExecutors);
  }

  /**
   * Mutate a gene by setting a new allele.
   * 
   * @param value new allele
   */
  public void mutate(ScheduleAllele value) {
    allele = value;
  }
  
  /* (non-Javadoc)
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
