package cn.bc.web.ui.html.page;

import cn.bc.web.ui.html.Div;

/**
 * 页面
 * 
 * @author dragon
 * 
 */
public class HtmlPage extends Div {
	public String getNamespace() {
		return getAttr("namespace");
	}
	public HtmlPage setNamespace(String namespace) {
		setAttr("data-namespace", namespace);
		return this;
	}
	public String getTitle() {
		return getAttr("title");
	}
	public HtmlPage setTitle(String title) {
		setAttr("title", title);
		return this;
	}
	public String getJs() {
		return getAttr("data-js");
	}
	public HtmlPage addJs(String js) {
		String cur = getAttr("data-js");
		if (cur != null)
			setAttr("data-js", cur + "," + js);
		else
			setAttr("data-js", js);

		return this;
	}
	public String getCss() {
		return getAttr("data-css");
	}
	public HtmlPage addCss(String css) {
		String cur = getAttr("data-css");
		if (cur != null)
			setAttr("data-css", cur + "," + css);
		else
			setAttr("data-css", css);

		return this;
	}
	public String getInitMethod() {
		return getAttr("data-initMethod");
	}
	public HtmlPage setInitMethod(String initMethod) {
		setAttr("data-initMethod", initMethod);
		return this;
	}
	public String getType() {
		return getAttr("data-type");
	}
	public HtmlPage setType(String type) {
		setAttr("data-type", type);
		return this;
	}
	public String getAction() {
		return getAttr("data-action");
	}
	public HtmlPage setAction(String action) {
		setAttr("data-action", action);
		return this;
	}
	public String getOption() {
		return getAttr("data-option");
	}
	public HtmlPage setOption(String option) {
		setAttr("data-option", option);
		return this;
	}
}
