package cn.bc.report.chart.hightcharts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Series extends JSONObject_ {
	public Series() {
		super();
	}

	public Series(JSONObject defaultOption) throws JSONException {
		super(defaultOption);
	}
	public String getName() {
		return this._getString("name");
	}

	public void setName(String name) {
		this._add("name", name);
	}

	public void setData(JSONArray data) {
		this._add("data", data);
	}
}
