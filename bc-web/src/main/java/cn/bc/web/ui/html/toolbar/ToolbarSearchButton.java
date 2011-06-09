package cn.bc.web.ui.html.toolbar;

import cn.bc.web.ui.html.A;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.TextInput;

/**
 * 工具条搜索按钮
 * 
 * @author dragon
 * 
 */
public class ToolbarSearchButton extends Button {
	public String getTag() {
		return "span";
	}

	protected void init() {
		this.addClazz("bc-searchButton");
	}

	public ToolbarSearchButton() {
		super();
	}

	public void renderButton(StringBuffer main) {
		this.addChild(new A().setId("searchBtn")
				.addClazz("ui-icon ui-icon-search").setTitle(this.getTitle()));
		this.addChild(new TextInput().setId("searchText"));
		this.setTitle(null);
	}
}
