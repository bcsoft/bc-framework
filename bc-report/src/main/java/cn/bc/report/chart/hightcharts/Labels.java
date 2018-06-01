package cn.bc.report.chart.hightcharts;

import org.json.JSONException;
import org.json.JSONObject;

public class Labels extends JSONObject_ {
  public Labels() {
    super();
  }

  public Labels(JSONObject defaultOption) throws JSONException {
    super(defaultOption);
  }

  public void setRotation(int rotation) {
    this._add("rotation", rotation);
  }

  public void setAlign(String text) {
    this._add("align", text);
  }
}
