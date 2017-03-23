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

package alg.util.jenetics;

import java.util.Random;

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
   * Constructor.
   * 
   * @param numTasks number of tasks
   * @param numExecutors number of executors
   */
  public ScheduleAllele(int numTasks, int numExecutors) {
    Random randGen = new Random();

    taskId = randGen.nextInt(numTasks);
    executorId = randGen.nextInt(numExecutors);
  }

  /**
   * Constructor.
   * 
   * @param numTask number of tasks
   * @param numExecutors number of executing units
   * @param taskId ID of the task
   */
  public ScheduleAllele(int numTask, int numExecutors, int taskId) {
    Random randGen = new Random();
    this.taskId = taskId;
    this.executorId = randGen.nextInt(numExecutors);
  }

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
}