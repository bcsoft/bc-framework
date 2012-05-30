package cn.bc.report.chart.hightcharts;

import org.json.JSONArray;
import org.junit.Test;

public class ChartOptionTest {
	@Test
	public void testGetDefaultChartOption() throws Exception {
		ChartOption o = ChartOption.getDefaultChartOption(null);
		System.out.println(o.toString());
	}

	public void test01() throws Exception {
		ChartOption o = new ChartOption();
		o.getChart().setDefaultSeriesType(SeriesType.line);

		o.getXAxis().setCategories(
				new JSONArray(new String[] { "a", "b", "c", "d", "e" }));
		o.getXAxis().getLabels().setRotation(-45);
		o.getXAxis().getLabels().setAlign("right");

		o.getYAxis().setMin(0);
		o.getYAxis().getTitle().setText("登录帐号数(个/每天)");

		o.getCredits().setEnabled(false);
		o.getLegend().setEnabled(false);

		Series s = new Series();
		s.setName("统计");
		s.setData(new JSONArray(new Integer[] { 1, 2, 3, 4, 5 }));
		o.getSeries().put(s);

		System.out.println(o.toString());
	}
}
