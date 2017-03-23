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

package de.dortmund.fh.pimes.gitlab.alg.util.graph;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Graph edge factory.
 * 
 * @author Pedro Cuadra
 *
 */
public class GraphEdgeFactory implements EdgeFactory<GraphNode, DefaultWeightedEdge> {
  
  /* (non-Javadoc)
   * @see org.jgrapht.EdgeFactory#createEdge(java.lang.Object, java.lang.Object)
   */
  @Override
  public DefaultWeightedEdge createEdge(GraphNode sourceVertex, GraphNode targetVertex) {
    return new DefaultWeightedEdge();
  }

}
