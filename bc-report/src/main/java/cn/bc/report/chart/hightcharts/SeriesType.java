package cn.bc.report.chart.hightcharts;

public enum SeriesType {
  /**
   * 折线图
   */
  line,

  /**
   * 曲线图
   */
  spline,

  /**
   * 饼图
   */
  pie,

  /**
   * 柱形图
   */
  bar,

  /**
   * 折线面积图
   */
  area,

  /**
   * 曲线面积图
   */
  areaspline,

  /**
   * 列图
   */
  column,

  /**
   * 点图
   */
  scatter;

  @Override
  public String toString() {
    return this.name();
  }
}
