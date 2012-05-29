package cn.bc.report.chart.hightcharts;

import org.json.JSONException;
import org.json.JSONObject;

public class Chart extends JSONObject_ {
	public Chart() {
		super();
	}

	public Chart(JSONObject defaultOption) throws JSONException {
		super(defaultOption);
	}

	public SeriesType getDefaultSeriesType() {
		if (this.has("defaultSeriesType"))
			return Enum.valueOf(SeriesType.class,
					this._getString("defaultSeriesType"));
		else
			return SeriesType.line;
	}

	public void setDefaultSeriesType(SeriesType defaultSeriesType) {
		this._add("defaultSeriesType", defaultSeriesType);
	}

	public boolean isInverted() {
		return this._getBoolean("inverted");
	}

	public void setInverted(boolean inverted) {
		this._add("Inverted", inverted);
	}
}
