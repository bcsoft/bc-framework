package cn.bc.report.chart.hightcharts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Axis extends JSONObject_ {
	public Axis() {
		super();
	}

	public Axis(JSONObject defaultOption) throws JSONException {
		super(defaultOption);
	}

	public Title getTitle() {
		if (_get("title") == null) {
			_add("title", new Title());
		}
		return (Title) _get("title");
	}

	public Labels getLabels() {
		if (_get("labels") == null) {
			_add("labels", new Labels());
		}
		return (Labels) _get("labels");
	}

	public void setCategories(JSONArray categories) {
		this._add("categories", categories);
	}

	public void setMin(int min) {
		this._add("min", min);
	}

	@Override
	protected void convertFrom(JSONObject o) throws JSONException {
		// 处理类型转换
		if (o.has("title")) {
			this.put("title", new Title(this.getJSONObject("title")));
		}
		if (o.has("labels")) {
			this.put("labels", new Labels(this.getJSONObject("labels")));
		}
	}
}
