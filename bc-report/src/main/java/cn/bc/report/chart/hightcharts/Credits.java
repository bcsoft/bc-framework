package cn.bc.report.chart.hightcharts;

import org.json.JSONException;
import org.json.JSONObject;

public class Credits extends JSONObject_ {
	public Credits() {
		super();
	}

	public Credits(JSONObject defaultOption) throws JSONException {
		super(defaultOption);
	}

	public void setEnabled(boolean enabled) {
		this._add("enabled", enabled);
	}
}
