package alg.util;

/**
 * Pane info.
 * 
 * @author Pedro Cuadra
 */
public final class PaneInfo {

  private final String chartName;
  private final Object cookie;

  /**
   * Constructor.
   *
   * @param paneName Chart name
   * @param cookie cookie object
   */
  public PaneInfo(String paneName, Object cookie) {

    this.chartName = paneName;
    this.cookie = cookie;
  }

  public String getExampleChartName() {

    return chartName;
  }

  public Object getCookie() {

    return cookie;
  }

  @Override
  public String toString() {

    return this.chartName;
  }

}
