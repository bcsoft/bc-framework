package cn.bc.web.ui.html.toolbar;

import cn.bc.web.ui.html.Button;

/**
 * 工具条按钮分隔符
 * 
 * @author dragon
 * 
 */
public class ToolbarSeparator extends Button {
	public String getTag() {
		return "span";
	}

	protected void init() {
		this.addClazz("bc-separatorButton ui-icon ui-icon-grip-dotted-vertical");
	}

	protected void renderButton(StringBuffer main) {
		// do nothing
	}
}
