package cn.bc.web.ui.html.page;

import cn.bc.web.ui.json.Json;

public class ButtonOption extends Json {
	public ButtonOption() {
	}
	public ButtonOption(String label, String action) {
		this();
		this.put("text", label);
		this.put("action", action);
	}
	public ButtonOption(String label, String action, String click) {
		this();
		this.put("text", label);
		this.put("action", action);
		this.put("click", click);
	}
}
