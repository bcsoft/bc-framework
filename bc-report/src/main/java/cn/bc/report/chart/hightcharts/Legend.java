package cn.bc.report.chart.hightcharts;

import org.json.JSONException;
import org.json.JSONObject;

public class Legend extends JSONObject_ {
	public Legend() {
		super();
	}

	public Legend(JSONObject defaultOption) throws JSONException {
		super(defaultOption);
	}

	public void setEnabled(boolean enabled) {
		this._add("enabled", enabled);
	}
}
