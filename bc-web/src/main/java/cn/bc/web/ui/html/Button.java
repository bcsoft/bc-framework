package cn.bc.web.ui.html;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.web.ui.AbstractComponent;

/**
 * 按钮
 * 
 * @author dragon
 * 
 */
public class Button extends AbstractComponent {
	private static Log logger = LogFactory.getLog(Button.class);
	private String icon;// 首选图标样式
	private String text;// 显示的文字
	private JSONObject extra;// 额外的数据

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

	public Button addParam(String name, String value) {
		if (extra == null)
			extra = new JSONObject();
		try {
			extra.put(name, value);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		this.setAttr("data-extra", extra.toString());
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
