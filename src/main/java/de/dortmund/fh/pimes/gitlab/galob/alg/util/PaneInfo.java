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

/**
 * Pane info.
 * 
 * @author Pedro Cuadra
 */
public final class PaneInfo {
  /**
   * Pane name.
   */
  private final String paneName;
  /**
   * Pane cookie or pane itself.
   */
  private final Object cookie;

  /**
   * Constructor.
   *
   * @param paneName
   *          Chart name
   * @param cookie
   *          cookie object
   */
  public PaneInfo(String paneName, Object cookie) {

    this.paneName = paneName;
    this.cookie = cookie;
  }

  /**
   * Get the name of the pane.
   * 
   * @return pane name
   */
  public String getPaneName() {
    return paneName;
  }

  /**
   * Get cookie or pane.
   * 
   * @return cookie or pane
   */
  public Object getCookie() {
    return cookie;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.paneName;
  }

}
