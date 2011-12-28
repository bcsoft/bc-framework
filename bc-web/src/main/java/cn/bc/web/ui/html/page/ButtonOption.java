package cn.bc.web.ui.html.page;

import cn.bc.web.ui.json.Json;

public class ButtonOption extends Json {
	private String id;

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

	public String getId() {
		return id;
	}

	public ButtonOption setId(String id) {
		this.id = id;
		this.put("id", id);
		return this;
	}
}
