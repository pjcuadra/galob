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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class GraphNodeTest {
  /**
   * Node ID.
   */
  private static final int NODE_ID = 3;
  /**
   * ETC row.
   */
  private static final double[] ETC_ROW = { 1, 2, 3 };
  /**
   * Test node.
   */
  private GraphNode testNode;
  /**
   * Delta for double comparison.
   */
  private static final double EPSILON = 0.00001;

  /**
   * Unit testing set-up.
   *
   * @throws Exception
   *           failure exception
   */
  @Before
  public void setUp() throws Exception {
    testNode = new GraphNode(NODE_ID, ETC_ROW);
  }

  @Test
  public void testGetTaskId() throws Exception {
    assertEquals(NODE_ID, testNode.getTaskId());
  }

  @Test
  public void testGetEtcRow() throws Exception {
    assertArrayEquals(ETC_ROW, testNode.getEtcRow(), EPSILON);

    // Try to alter the ETC values
    testNode.getEtcRow()[0] = 5;
    testNode.getEtcRow()[1] = 6;
    testNode.getEtcRow()[2] = 7;

    // Shouldn't be changed
    assertArrayEquals(ETC_ROW, testNode.getEtcRow(), EPSILON);
  }

  @Test
  public void testSetCookie() throws Exception {
    assertEquals(null, testNode.getCookie(this));

    // Add itself as cookie
    testNode.setCookie(this, testNode);

    assertEquals(testNode, testNode.getCookie(this));

    // Update cookie with ETC row
    testNode.setCookie(this, ETC_ROW);

    assertEquals(ETC_ROW, testNode.getCookie(this));

  }

  @Test
  public void testGetCookie() throws Exception {
    assertEquals(null, testNode.getCookie(this));

    // Add itself as cookie
    testNode.setCookie(this, testNode);

    assertEquals(testNode, testNode.getCookie(this));

  }

  @Test
  public void testGetExecutionTimeOnUnit() throws Exception {
    int core = 0;
    for (double i : ETC_ROW) {
      assertEquals(i, testNode.getExecutionTimeOnUnit(core++), EPSILON);
    }
  }

  @Test
  public void testClone() throws Exception {
    assertEquals(null, testNode.getCookie(this));

    // Add itself as cookie
    testNode.setCookie(this, testNode);

    GraphNode clone = testNode.clone();

    assertEquals(testNode, testNode.getCookie(this));
    assertEquals(null, clone.getCookie(this));

    // Update cookie of clone
    clone.setCookie(this, clone);

    assertEquals(testNode, testNode.getCookie(this));
    assertEquals(clone, clone.getCookie(this));
  }

}
