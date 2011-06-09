package cn.bc.web.ui.html.grid;

import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Ul;

/**
 * Grid 底部的工具条
 * @author dragon
 *
 */
public class GridFooter extends Ul {
	protected void init() {
		this.addClazz("pager ui-widget-content ui-widget ui-helper-clearfix");
	}

	public GridFooter addButton(Component button) {
		this.addChild(button);
		return this;
	}
}
