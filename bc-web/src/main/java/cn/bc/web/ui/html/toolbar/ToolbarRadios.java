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
public class ToolbarRadios extends Button {
	private List<String> labels = new ArrayList<String>();
	private List<String> values = new ArrayList<String>();
	private int activeIndex = -1;

	public String getTag() {
		return "div";
	}

	protected void init() {
		this.addClazz("bc-radios ui-buttonset");
	}

	public ToolbarRadios() {
		super();
	}

	public ToolbarRadios addRadio(String label, String value) {
		labels.add(label);
		values.add(value);
		return this;
	}

	public ToolbarRadios setActive(int index) {
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
		if (value != null && value.length() > 0)
			tpl += " data-value='" + value + "'";
		tpl += "><span class='ui-button-text'>" + label + "</span>";
		tpl += "</div>";
		return new Text(tpl);
	}
}
