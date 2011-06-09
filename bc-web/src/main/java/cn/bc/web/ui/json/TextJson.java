package cn.bc.web.ui.json;

/**
 * 
 * @author dragon
 *
 */
public class TextJson extends Json {
	private String text;

	public TextJson(String text) {
		this.text = text;
	}

	public StringBuffer render(StringBuffer main) {
		return main.append(wrapQuota(text));
	}
}
