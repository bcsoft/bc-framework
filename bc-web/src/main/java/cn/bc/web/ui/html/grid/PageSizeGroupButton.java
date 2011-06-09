package cn.bc.web.ui.html.grid;

import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.A;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Text;

/**
 * Grid底部工具条的分页按钮
 * 
 * @author dragon
 * 
 */
public class PageSizeGroupButton extends Button {
	private int[] values;
	private int activeValue;

	public String getTag() {
		return "li";
	}

	protected void init() {
		this.addClazz("pagerIconGroup size ui-state-default ui-corner-all");
	}

	public PageSizeGroupButton() {
		super();
	}

	public int[] getValues() {
		return values;
	}

	public PageSizeGroupButton setValues(int[] values) {
		this.values = values;
		return this;
	}

	public int getActiveValue() {
		return activeValue;
	}

	public PageSizeGroupButton setActiveValue(int activeValue) {
		this.activeValue = activeValue;
		return this;
	}

	public void renderButton(StringBuffer main) {
		if (values != null && values.length > 0) {
			Component a;
			for (int value : values) {
				a = new A()
						.setId("toFirstPage")
						.addClazz("pagerIcon ui-state-default ui-corner-all")
						.addChild(
								new Span().addClazz("pageSize").addChild(
										new Text(value + "")));
				if (value == this.activeValue)
					a.addClazz("ui-state-active");
				this.addChild(a);
			}
		}
	}
}
