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
	private String secondaryIcon;// 第二图标样式

	public String getSecondaryIcon() {
		return secondaryIcon;
	}

	public ToolbarButton setSecondaryIcon(String secondaryIcon) {
		this.secondaryIcon = secondaryIcon;
		return this;
	}

	protected void init() {
		this.addClazz("bc-button ui-button ui-widget ui-state-default ui-corner-all");
		this.setAttr("type", "button");// 添加这个是为了解决ie8在文本框回车的问题
	}

	public ToolbarButton() {
		super();
	}

	public void renderButton(StringBuffer main) {
		if (this.getIcon() != null && this.getText() != null
				&& this.getSecondaryIcon() == null) {// 图标1+文字
			this.addClazz("ui-button-text-icon-primary");
			this.addChild(createPrimaryIconChild());// 图标1
			this.addChild(createTextChild());// 文字
		} else if (this.getIcon() == null && this.getText() != null
				&& this.getSecondaryIcon() == null) {// 仅文字
			this.addClazz("ui-button-text-only");// 文字
			this.addChild(createTextChild());
		} else if (this.getIcon() != null && this.getText() == null
				&& this.getSecondaryIcon() == null) {// 仅图标1
			this.addClazz("ui-button-icon-only");
			this.addChild(createPrimaryIconChild());// 图标1
		} else if (this.getIcon() != null && this.getText() != null
				&& this.getSecondaryIcon() != null) {// 图标1+文字+图标2
			this.addClazz("ui-button-text-icons");
			this.addChild(createPrimaryIconChild());// 图标1
			this.addChild(createTextChild());// 文字
			this.addChild(createSecondaryIconChild());// 图标2
		} else if (this.getIcon() == null && this.getText() != null
				&& this.getSecondaryIcon() != null) {// 文字+图标2
			this.addClazz("ui-button-text-icon-secondary");
			this.addChild(createTextChild());// 文字
			this.addChild(createSecondaryIconChild());// 图标2
		} else {
			throw new CoreException(
					"at least set property 'icon' or 'text' or 'secondaryIcon'.");
		}
	}

	// 文本dom
	protected Component createTextChild() {
		return new Span().addClazz("ui-button-text").addChild(
				new Text(this.getText()));
	}

	// Primary图标dom
	protected Component createPrimaryIconChild() {
		return new Span().addClazz("ui-button-icon-primary ui-icon").addClazz(
				this.getIcon());
	}

	// Secondary图标dom
	protected Component createSecondaryIconChild() {
		return new Span().addClazz("ui-button-icon-secondary ui-icon")
				.addClazz(this.getSecondaryIcon());
	}
}
