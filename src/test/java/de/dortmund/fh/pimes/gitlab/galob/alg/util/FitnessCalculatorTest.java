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

package de.dortmund.fh.pimes.gitlab.galob.alg.util;

import de.dortmund.fh.pimes.gitlab.galob.alg.ExecutionTimeFitnessCalculatorTest;
import de.dortmund.fh.pimes.gitlab.galob.alg.LoadBalancingFitnessCalculatorTest;

import org.junit.Test;

public class FitnessCalculatorTest {

  // Just run the implementation's related tests
  @Test
  public void testGetFitness() throws Exception {
    LoadBalancingFitnessCalculatorTest lbTest = new LoadBalancingFitnessCalculatorTest();

    lbTest.setUp();
    lbTest.testCalcFitness();

    ExecutionTimeFitnessCalculatorTest excTest = new ExecutionTimeFitnessCalculatorTest();

    excTest.setUp();
    excTest.testCalcFitness();
  }
}
