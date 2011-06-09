package cn.bc.web.ui.html.toolbar;

import cn.bc.web.ui.html.Span;

/**
 * 按钮分组
 * @author dragon
 *
 */
public class ButtonGroup extends Span {
	protected void init() {
		this.addClazz("bc-buttonGroup");
	}

	public ButtonGroup addButton(ToolbarButton button) {
		this.addChild(button);
		return this;
	}
}
