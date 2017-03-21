package alg.util;

import org.knowm.xchart.internal.chartpart.Chart;

/**
 * Chart info class.
 * Took from XCharts example
 * 
 * @author timmolter
 */
public final class ChartInfo {

  private final String chartName;
  private final Chart<?, ?> chart;

  /**
   * Constructor.
   *
   * @param chartName Chart name
   * @param chart chart object
   */
  public ChartInfo(String chartName, Chart<?, ?> chart) {

    this.chartName = chartName;
    this.chart = chart;
  }

  public String getExampleChartName() {

    return chartName;
  }

  public Chart<?, ?> getChart() {

    return chart;
  }

  @Override
  public String toString() {

    return this.chartName;
  }

}
