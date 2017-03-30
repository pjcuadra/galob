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

import java.util.Random;

import de.dortmund.fh.pimes.gitlab.galob.alg.util.HeterogeneousComputingEnv;

/**
 * Genetics algorithm's gene.
 * 
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 * 
 */
public class ScheduleAllele {
  /**
   * ID of the task.
   */
  private int taskId;
  /**
   * ID of the executor.
   */
  private int executorId;
  
  /**
   * Get task ID.
   * 
   * @return task ID
   */
  public int getTaskId() {
    return taskId;
  }

  /**
   * Get executor ID.
   * 
   * @return executor ID
   */
  public int getExecutorId() {
    return executorId;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "{ " + taskId + " : " + executorId + " }" ;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object object) {
    ScheduleAllele allele;
    
    if (!(object instanceof ScheduleAllele)) {
      return false;
    }
    
    allele = (ScheduleAllele) object;
    
    if (allele.executorId != this.executorId) {
      return false;
    }
    
    if (allele.taskId != this.taskId) {
      return false;
    }
    
    return true;
    
  }
  
  /**
   * Allele builder for a given task id.
   * 
   * @param env heterogeneous computing environment
   * @param taskId task ID
   * @return allele with the provided task ID
   */
  static public ScheduleAllele ofTask(HeterogeneousComputingEnv env, int taskId) {
    ScheduleAllele allele = new ScheduleAllele();
    Random randGen = new Random();
    
    // Set values
    allele.executorId = randGen.nextInt(env.getNumberOfExecutors());
    allele.taskId = taskId;
    
    return allele;
  }
  
  /**
   * Allele builder for a given executor id.
   * 
   * @param env heterogeneous computing environment
   * @param executorId executor ID
   * @return allele with the provided executor ID
   */
  static public ScheduleAllele ofExecutor(HeterogeneousComputingEnv env, int executorId) {
    ScheduleAllele allele = new ScheduleAllele();
    Random randGen = new Random();
    
    // Set values
    allele.executorId = executorId;
    allele.taskId = randGen.nextInt(env.getNumberOfTasks());
    
    return allele;
  }
  
  /**
   * Allele builder.
   * 
   * @param env heterogeneous computing environment
   * @param taskId task ID
   * @param executorId executor ID
   * @return allele with the provided executor ID
   */
  static public ScheduleAllele of(HeterogeneousComputingEnv env, int taskId, int executorId) {
    ScheduleAllele allele = new ScheduleAllele();
    
    // Set values
    allele.executorId = executorId;
    allele.taskId = taskId;
    
    return allele;
  }
  
  /**
   * Random allele builder.
   * 
   * @param env heterogeneous computing environment
   * @param taskId task ID
   * @param executorId executor ID
   * @return allele with the provided executor ID
   */
  static public ScheduleAllele ofRandom(HeterogeneousComputingEnv env) {
    ScheduleAllele allele = new ScheduleAllele();
    Random randGen = new Random();
    
    // Set values
    allele.executorId = randGen.nextInt(env.getNumberOfExecutors());
    allele.taskId = randGen.nextInt(env.getNumberOfTasks());
    
    return allele;
  }
}
