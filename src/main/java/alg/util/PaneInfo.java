package alg.util;

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
   * @param paneName Chart name
   * @param cookie cookie object
   */
  public PaneInfo(String paneName, Object cookie) {

    this.paneName = paneName;
    this.cookie = cookie;
  }

  /**
   * Get the name of the pane.
   * @return pane name
   */
  public String getPaneName() {
    return paneName;
  }

  /**
   * Get cookie or pane.
   * @return cookie or pane
   */
  public Object getCookie() {
    return cookie;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.paneName;
  }

}
