package cn.bc.report.chart.hightcharts;

import org.json.JSONException;
import org.json.JSONObject;

public class Title extends JSONObject_ {
  public Title() {
    super();
  }

  public Title(JSONObject defaultOption) throws JSONException {
    super(defaultOption);
  }

  public String getText() {
    return this._getString("text");
  }

  public void setText(String text) {
    this._add("text", text);
  }
}
