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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Schedule Chromosome unit testing.
 *
 * @author Pedro Cuadra
 * @author Sudheera Bandi
 *
 */
public class GraphTest {
  /**
   * Number of nodes.
   */
  private static final int NUM_NODES = 3;
  /**
   * ETC row.
   */
  private static final double[] ETC_ROW = { 1, 2, 3 };
  /**
   * Test node.
   */
  private Graph testGraph;
  /**
   * Nodes of graph.
   */
  private GraphNode[] nodes = new GraphNode[NUM_NODES];
  /**
   * Delta for double comparison.
   */
  private static final double EPSILON = 0.00001;

  /**
   * Unit testing set-up.
   *
   * @throws Exception
   *           falure exception
   */
  @Before
  public void setUp() throws Exception {
    testGraph = new Graph();

    for (int i = 0; i < NUM_NODES; i++) {
      nodes[i] = new GraphNode(i, ETC_ROW);
      // Add all tasks
      testGraph.addVertex(nodes[i]);
    }

    // Create dependencies
    testGraph.addDependency(nodes[0], nodes[1], 1);
    testGraph.addDependency(nodes[0], nodes[2], 1);
    testGraph.addDependency(nodes[1], nodes[2], 1);

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCheckCycles() throws Exception {
    // Graph is cycle free
    assertFalse(testGraph.checkCycles());

    // Add a cycle
    testGraph.addDependency(nodes[1], nodes[0], 1);

    // Check for cycles
    assertTrue(testGraph.checkCycles());

    // Destroy the cycle
    testGraph.removeDependency(nodes[0], nodes[1]);

    // Graph is cycle free
    assertFalse(testGraph.checkCycles());

    // Remove an other dependency
    testGraph.removeDependency(nodes[1], nodes[0]);

    // Graph is cycle free
    assertFalse(testGraph.checkCycles());

  }

  @Test
  public void testAddVertex() throws Exception {
    boolean found = false;
    for (GraphNode addedNode : testGraph.vertexSet()) {
      found = false;

      // Look if the node was added
      for (GraphNode node : nodes) {
        if (node.equals(addedNode)) {
          found = true;
        }

      }

      // Assert that was added
      assertTrue(found);
    }

    // Assert the number of nodes
    assertEquals(NUM_NODES, testGraph.vertexSet().size());
  }

  @Test
  public void testAddDependency() throws Exception {
    // Assert the number of edges
    assertEquals(3, testGraph.edgeSet().size());

    // Add an already existing edge
    testGraph.addDependency(nodes[0], nodes[1], 1);

    // Since the edges should be merged
    assertEquals(3, testGraph.edgeSet().size());
    assertEquals(2, testGraph.getEdgeWeight(testGraph.getEdge(nodes[0], nodes[1])), EPSILON);

    // Add new dependency
    testGraph.addDependency(nodes[1], nodes[0], 1);
    assertEquals(4, testGraph.edgeSet().size());

  }

  @Test
  public void testGetCommunicationCost() throws Exception {
    // Already set communication costs
    assertEquals(1, testGraph.getCommunicationCost(nodes[0], nodes[1]), EPSILON);
    assertEquals(1, testGraph.getCommunicationCost(nodes[1], nodes[2]), EPSILON);
    assertEquals(1, testGraph.getCommunicationCost(nodes[0], nodes[2]), EPSILON);

    // Add an already existing edge
    testGraph.addDependency(nodes[0], nodes[1], 1);

    // Assert the communication costs
    assertEquals(2, testGraph.getCommunicationCost(nodes[0], nodes[1]), EPSILON);
    assertEquals(1, testGraph.getCommunicationCost(nodes[1], nodes[2]), EPSILON);
    assertEquals(1, testGraph.getCommunicationCost(nodes[0], nodes[2]), EPSILON);

    // Assert all other possibilities are 0
    assertEquals(0, testGraph.getCommunicationCost(nodes[1], nodes[0]), EPSILON);
    assertEquals(0, testGraph.getCommunicationCost(nodes[2], nodes[1]), EPSILON);
    assertEquals(0, testGraph.getCommunicationCost(nodes[2], nodes[0]), EPSILON);

  }

  @Test
  public void testClone() throws Exception {
    Graph clone = testGraph.clone();

    // Assert all nodes
    for (int i = 0; i < NUM_NODES; i++) {
      // Set the cookie to store the original node
      testGraph.getGraphNodeById(i).setCookie(testGraph.getGraphNodeById(i));

      // Check that nodes were cloned
      assertEquals(testGraph.getGraphNodeById(i), clone.getGraphNodeById(i));

      // Cookies shall not be cloned
      assertEquals(testGraph.getGraphNodeById(i), testGraph.getGraphNodeById(i).getCookie());
      assertEquals(null, clone.getGraphNodeById(i).getCookie());
    }

    // Assert all edges
    assertEquals(testGraph.edgeSet().size(), clone.edgeSet().size());

    // Remove one dependency of clone
    clone.removeDependency(clone.getGraphNodeById(0), clone.getGraphNodeById(2));

    // Assert amount of dependencies
    assertEquals(2, clone.edgeSet().size());
    assertEquals(3, testGraph.edgeSet().size());

    // Remove one dependency of clone
    clone.removeDependency(clone.getGraphNodeById(1), clone.getGraphNodeById(2));

    // Assert amount of dependencies
    assertEquals(1, clone.edgeSet().size());
    assertEquals(3, testGraph.edgeSet().size());
  }

  @Test
  public void testGetTologicalLevelNodes() throws Exception {

    // Node ID should be the level as well
    for (int i = 0; i < NUM_NODES; i++) {
      // Assert that there's only one node per level
      assertEquals("At topological level " + i, 1, testGraph.getTologicalLevelNodes(i).size());

      for (GraphNode levelNode : testGraph.getTologicalLevelNodes(i)) {
        assertEquals(nodes[i], levelNode);
      }
    }

  }

  @Test
  public void testGetMaxTopologicalLevel() throws Exception {
    // Assert the max topological level
    assertEquals(3, testGraph.getMaxTopologicalLevel());

    // Remove one dependency to lower the max level
    testGraph.removeDependency(nodes[0], nodes[1]);

    // Assert the max topological level
    assertEquals(2, testGraph.getMaxTopologicalLevel());

    // Remove one more dependency that doesn't decrease the levels
    testGraph.removeDependency(nodes[0], nodes[2]);

    // Assert the max topological level
    assertEquals(2, testGraph.getMaxTopologicalLevel());

    // Make all nodes independent
    testGraph.removeDependency(nodes[1], nodes[2]);

    // Assert the max topological level
    assertEquals(1, testGraph.getMaxTopologicalLevel());

  }

  @Test
  public void testGetNodeTopologicalLevel() throws Exception {
    // Assert topological levels of nodes
    assertEquals(0, testGraph.getNodeTopologicalLevel(0));
    assertEquals(1, testGraph.getNodeTopologicalLevel(1));
    assertEquals(2, testGraph.getNodeTopologicalLevel(2));

    // Remove one dependency to lower the max level
    testGraph.removeDependency(nodes[0], nodes[1]);

    // Assert topological levels of nodes
    assertEquals(0, testGraph.getNodeTopologicalLevel(0));
    assertEquals(0, testGraph.getNodeTopologicalLevel(1));
    assertEquals(1, testGraph.getNodeTopologicalLevel(2));

    // Remove one more dependency that doesn't decrease the levels
    testGraph.removeDependency(nodes[0], nodes[2]);

    // Assert topological levels of nodes
    assertEquals(0, testGraph.getNodeTopologicalLevel(0));
    assertEquals(0, testGraph.getNodeTopologicalLevel(1));
    assertEquals(1, testGraph.getNodeTopologicalLevel(2));

    // Make all nodes independent
    testGraph.removeDependency(nodes[1], nodes[2]);

    // Assert topological levels of nodes
    assertEquals(0, testGraph.getNodeTopologicalLevel(0));
    assertEquals(0, testGraph.getNodeTopologicalLevel(1));
    assertEquals(0, testGraph.getNodeTopologicalLevel(2));
  }

  @Test
  public void testRemoveDependency() throws Exception {
    // Assert the amount of dependencies
    assertEquals(3, testGraph.edgeSet().size());

    // Remove one dependency
    testGraph.removeDependency(nodes[0], nodes[1]);

    // Assert the amount of dependencies
    assertEquals(2, testGraph.edgeSet().size());

    // Remove one dependency
    testGraph.removeDependency(nodes[0], nodes[2]);

    // Assert the amount of dependencies
    assertEquals(1, testGraph.edgeSet().size());

    // Make all nodes independent
    testGraph.removeDependency(nodes[1], nodes[2]);

    // Assert the amount of dependencies
    assertEquals(0, testGraph.edgeSet().size());
  }

  @Test
  public void testGetGraphNodeById() throws Exception {
    // Check getting every node
    for (int i = 0; i < NUM_NODES; i++) {
      assertEquals(nodes[i], testGraph.getGraphNodeById(i));
    }
  }

}
