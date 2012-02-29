package cn.bc.web.ui.html.page;

import cn.bc.web.ui.html.Button;
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

	public PageOption addButton(Button button) {
		return this.addButton(convert2ButtonOption(button));
	}

	/**
	 * 将html按钮转换为对话框按钮
	 * 
	 * @param button
	 *            工具条按钮
	 * @return
	 */
	public static ButtonOption convert2ButtonOption(Button button) {
		ButtonOption json = new ButtonOption();

		// 将'和"字符替换为\'和\"
		json.put("html", button.toString().replaceAll("'|\"", "\\\\\""));

		return json;
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

	public PageOption setMaxWidth(int maxWidth) {
		if (maxWidth > 0)
			put("maxWidth", maxWidth);
		else
			attrs.remove("maxWidth");
		return this;
	}

	public PageOption setMaxHeight(int maxHeight) {
		if (maxHeight > 0)
			put("maxHeight", maxHeight);
		else
			attrs.remove("maxHeight");
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

	public PageOption setMinimizable(boolean minimizable) {
		put("minimizable", minimizable);
		return this;
	}

	public PageOption setMaximizable(boolean maximizable) {
		put("maximizable", maximizable);
		return this;
	}

	public PageOption setHelp(String key) {
		put("help", key);
		return this;
	}

	public PageOption setPrint(String key) {
		put("print", key);
		return this;
	}
}
