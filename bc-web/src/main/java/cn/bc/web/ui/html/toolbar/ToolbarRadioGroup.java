package cn.bc.web.ui.html.toolbar;

import java.util.ArrayList;
import java.util.List;

import cn.bc.web.ui.Render;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.Text;

/**
 * 工具条单选按钮组
 * 
 * @author dragon
 * 
 */
public class ToolbarRadioGroup extends Button {
	private List<String> labels = new ArrayList<String>();
	private List<String> values = new ArrayList<String>();
	private int activeIndex = -1;

	public String getTag() {
		return "div";
	}

	protected void init() {
		this.addClazz("bc-radioGroup ui-buttonset");
	}

	public ToolbarRadioGroup() {
		super();
	}

	public Button setChange(String change) {
		this.setAttr("data-change", change);
		return this;
	}

	public Button setKey(String key) {
		this.setAttr("data-key", key);
		return this;
	}

	public ToolbarRadioGroup addRadio(String mix) {
		if (mix.indexOf(";") != -1) {
			String[] lvs = mix.split(";");
			String[] _lv;
			for (String lv : lvs) {
				if (lv.indexOf(",") != -1) {
					_lv = lv.split(",");
					labels.add(_lv[0].trim());
					values.add(_lv[1].trim());
				} else {
					labels.add(lv.trim());
					values.add(lv.trim());
				}
			}
		} else {
			labels.add(mix.trim());
			values.add(mix.trim());
		}
		return this;
	}

	public ToolbarRadioGroup addRadio(String label, String value) {
		labels.add(label);
		values.add(value);
		return this;
	}

	public ToolbarRadioGroup setActive(int index) {
		this.activeIndex = index;
		return this;
	}

	public void renderButton(StringBuffer main) {
		int len = labels.size();
		if (len > 1) {
			for (int i = 0; i < labels.size(); i++) {
				if (i == 0)
					this.addChild(createRadio("ui-corner-left",
							i == activeIndex, labels.get(i), values.get(i)));
				else if (i == len - 1)
					this.addChild(createRadio("ui-corner-right",
							i == activeIndex, labels.get(i), values.get(i)));
				else
					this.addChild(createRadio(null, i == activeIndex,
							labels.get(i), values.get(i)));
			}
		} else {
			this.addChild(createRadio("ui-corner-all", 0 == activeIndex,
					labels.get(0), values.get(0)));
		}
	}

	protected Render createRadio(String cornerClass, boolean active,
			String label, String value) {
		String tpl = "<div class='ui-button ui-widget ui-state-default ui-button-text-only";
		if (cornerClass != null && cornerClass.length() > 0)
			tpl += " " + cornerClass;
		if (active)
			tpl += " ui-state-active";
		tpl += "'";
		if (value != null)
			tpl += " data-value='" + value + "'";
		tpl += "><span class='ui-button-text'>" + label + "</span>";
		tpl += "</div>";
		return new Text(tpl);
	}
}
