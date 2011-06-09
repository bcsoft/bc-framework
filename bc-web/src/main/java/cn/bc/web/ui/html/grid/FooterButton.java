package cn.bc.web.ui.html.grid;

import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.Span;

/**
 * Grid底部工具条按钮
 * 
 * @author dragon
 * 
 */
public class FooterButton extends Button {
	public String getTag() {
		return "li";
	}

	protected void init() {
		this.addClazz("pagerIcon ui-state-default ui-corner-all");
	}

	public FooterButton() {
		super();
	}

	public void renderButton(StringBuffer main) {
		// 图标
		if (this.getIcon() != null) {
			this.addChild(new Span().addClazz("ui-icon").addClazz(
					this.getIcon()));
		}
		// 忽略文字
	}
}
