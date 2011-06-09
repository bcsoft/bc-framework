package cn.bc.web.ui.html.toolbar;

import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Div;

/**
 * 工具条
 * @author dragon
 *
 */
public class Toolbar extends Div {
	protected void init() {
		this.addClazz("bc-toolbar ui-widget-content");
	}
	
	public String getTag() {
		return "div";
	}

	public Toolbar addButton(Component button) {
		this.addChild(button);
		return this;
	}
}
