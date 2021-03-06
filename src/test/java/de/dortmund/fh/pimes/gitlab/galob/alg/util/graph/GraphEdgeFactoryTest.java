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

package de.dortmund.fh.pimes.gitlab.galob.alg.util.graph;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GraphEdgeFactoryTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testCreateEdge() throws Exception {
    GraphEdgeFactory ef = new GraphEdgeFactory();

    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Source vertex is null");

    ef.createEdge(null, null);

    ef.createEdge(new GraphNode(0, new double[2]), null);
  }

  @Test
  public void testCreateEdge2() throws Exception {
    GraphEdgeFactory ef = new GraphEdgeFactory();

    // Catch exception
    thrown.expect(AssertionError.class);
    thrown.expectMessage("Target vertex is null");

    ef.createEdge(new GraphNode(0, new double[2]), null);
  }

}
