package cn.bc.web.ui.html;

import cn.bc.web.ui.AbstractComponent;

/**
 * 按钮
 * 
 * @author dragon
 * 
 */
public class Button extends AbstractComponent {
	private String icon;
	private String text;

	public String getTag() {
		return "button";
	}

	public Button() {
		super();
	}

	public String getAction() {
		return this.getAttr("data-action");
	}

	public Button setAction(String action) {
		this.setAttr("data-action", action);
		return this;
	}

	public String getCallback() {
		return this.getAttr("data-callback");
	}

	public Button setCallback(String callback) {
		this.setAttr("data-callback", callback);
		return this;
	}

	public String getClick() {
		return this.getAttr("data-click");
	}

	public Button setClick(String click) {
		this.setAttr("data-click", click);
		return this;
	}

	public String getIcon() {
		return icon;
	}

	public Button setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public String getText() {
		return text;
	}

	public Button setText(String text) {
		this.text = text;
		return this;
	}

	public StringBuffer render(StringBuffer main) {
		this.renderButton(main);

		return super.render(main);
	}

	protected void renderButton(StringBuffer main) {
		// 图标
		if (icon != null) {
			this.addClazz(this.getIcon());
		}
		
		// 文字
		if (text != null) {
			this.addChild(new Text(text));
		}
	}
}
