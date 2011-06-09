package cn.bc.web.ui.html.page;

import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 页面data-option属性的配置，主要用于设置对话框的初始化参数
 * 
 * @author dragon
 * 
 */
public class PageOption extends Json {

	public PageOption setButtons(ButtonOption... buttons) {
		JsonArray jsons = (JsonArray) get("buttons");
		if (jsons == null)
			jsons = new JsonArray();
		else
			jsons.clear();
		for (ButtonOption button : buttons)
			jsons.add(button);
		put("buttons", jsons);
		return this;
	}

	public PageOption addButton(ButtonOption button) {
		JsonArray jsons = (JsonArray) get("buttons");
		if (jsons == null)
			jsons = new JsonArray();
		jsons.add(button);
		put("buttons", jsons);
		return this;
	}

	public PageOption setMinWidth(int minWidth) {
		if (minWidth > 0)
			put("minWidth", minWidth);
		else
			attrs.remove("minWidth");
		return this;
	}

	public PageOption setMinHeight(int minHeight) {
		if (minHeight > 0)
			put("minHeight", minHeight);
		else
			attrs.remove("minHeight");
		return this;
	}

	public PageOption setHeight(int height) {
		put("height", height);
		return this;
	}

	public PageOption setWidth(int width) {
		put("width", width);
		return this;
	}

	public PageOption setModal(boolean modal) {
		put("modal", modal);
		return this;
	}
}
