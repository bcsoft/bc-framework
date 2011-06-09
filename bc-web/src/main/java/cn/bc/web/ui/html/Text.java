package cn.bc.web.ui.html;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.web.ui.Render;

/**
 * 纯文本的ui组件
 * 
 * @author dragon
 * 
 */
public class Text implements Render {
	protected Log logger = LogFactory.getLog(getClass());
	private String text;

	public Text(String text) {
		this.text = text != null ? text : "";
	}

	public StringBuffer render(StringBuffer main) {
		main.append(text);
		return main;
	}

	public String toString() {
		return text;
	}
}
