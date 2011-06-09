package cn.bc.web.ui.html.toolbar;

import cn.bc.core.exception.CoreException;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Text;

/**
 * 工具条按钮
 * 
 * @author dragon
 * 
 */
public class ToolbarButton extends Button {
	public String getTag() {
		return "button";
	}

	protected void init() {
		this.addClazz("bc-button ui-button ui-widget ui-state-default ui-corner-all");
		this.setAttr("type", "button");//添加这个是为了解决ie8在文本框回车的问题
	}

	public ToolbarButton() {
		super();
	}

	public void renderButton(StringBuffer main) {
		// 样式控制
		if (this.getIcon() != null) {
			if (this.getText() != null) {// 图标+文字
				this.addClazz("ui-button-text-icon-primary");
				// 图标
				this.addChild(createIconChild());
				// 文字
				this.addChild(createTextChild());
			} else {// 仅图标
				this.addClazz("ui-button-icon-only");
				this.addChild(createIconChild());
			}
		} else {
			if (this.getText() != null) {// 仅文字
				this.addClazz("ui-button-text-only");
				this.addChild(createTextChild());
			} else {
				throw new CoreException(
						"at least set property 'icon' or 'text'.");
			}
		}
	}

	//文本dom
	protected Component createTextChild() {
		return new Span().addClazz("ui-button-text").addChild(
				new Text(this.getText()));
	}

	//图标dom
	protected Component createIconChild() {
		return new Span().addClazz(
				"ui-button-icon-primary ui-icon").addClazz(this.getIcon());
	}
}
