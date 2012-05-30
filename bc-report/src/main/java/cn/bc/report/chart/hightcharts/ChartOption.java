package cn.bc.report.chart.hightcharts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * highcharts图表的配置封装
 * 
 * @author dragon
 * 
 */
public class ChartOption extends JSONObject_ {
	public ChartOption() {
		super();
	}

	public ChartOption(JSONObject defaultOption) throws JSONException {
		super(defaultOption);
	}

	@Override
	protected void convertFrom(JSONObject o) throws JSONException {
		// 处理类型转换
		if (o.has("chart")) {
			this.put("chart", new Chart(this.getJSONObject("chart")));
		}
		if (o.has("title")) {
			this.put("title", new Title(this.getJSONObject("title")));
		}
		if (o.has("subtitle")) {
			this.put("subtitle", new Title(this.getJSONObject("subtitle")));
		}
		if (o.has("xAxis")) {
			this.put("xAxis", new Axis(this.getJSONObject("xAxis")));
		}
		if (o.has("yAxis")) {
			this.put("yAxis", new Axis(this.getJSONObject("yAxis")));
		}
		if (o.has("legend")) {
			this.put("legend", new Legend(this.getJSONObject("legend")));
		}
		if (o.has("credits")) {
			this.put("credits", new Credits(this.getJSONObject("credits")));
		}
	}

	public Chart getChart() {
		if (_get("chart") == null) {
			_add("chart", new Chart());
		}
		return (Chart) _get("chart");
	}

	public Title getTitle() {
		if (_get("title") == null) {
			_add("title", new Title());
		}
		return (Title) _get("title");
	}

	public Title getSubtitle() {
		if (_get("subtitle") == null) {
			_add("subtitle", new Title());
		}
		return (Title) _get("subtitle");
	}

	public Axis getXAxis() {
		if (_get("xAxis") == null) {
			_add("xAxis", new Axis());
		}
		return (Axis) _get("xAxis");
	}

	public Axis getYAxis() {
		if (_get("yAxis") == null) {
			_add("yAxis", new Axis());
		}
		return (Axis) _get("yAxis");
	}

	public Legend getLegend() {
		if (_get("legend") == null) {
			_add("legend", new Legend());
		}
		return (Legend) _get("legend");
	}

	public Credits getCredits() {
		if (_get("credits") == null) {
			_add("credits", new Credits());
		}
		return (Credits) _get("credits");
	}

	public JSONArray getSeries() {
		if (_get("series") == null) {
			_add("series", new JSONArray());
		}
		return (JSONArray) _get("series");
	}

	/**
	 * 初始化默认的图表配置
	 * 
	 * @param defaultOption
	 * 
	 * @return
	 * @throws JSONException
	 */
	public static ChartOption getDefaultChartOption(JSONObject defaultOption)
			throws JSONException {
		ChartOption o;
		if (defaultOption != null)
			o = new ChartOption(defaultOption);
		else
			o = new ChartOption();

		// 图标类型
		if (o.getChart().getDefaultSeriesType() == null)
			o.getChart().setDefaultSeriesType(SeriesType.line);

//		// 横轴
//		if (o.getXAxis().getTitle().getText() == null)
//			o.getXAxis().getTitle().setText("");
//
//		// 纵轴
//		if (o.getYAxis().getTitle().getText() == null)
//			o.getYAxis().getTitle().setText("");

		// 其它
		if (!o.getCredits().has("enabled"))
			o.getCredits().setEnabled(false);
		if (!o.getLegend().has("enabled"))
			o.getLegend().setEnabled(false);

		// 数据

		return o;
	}
}
