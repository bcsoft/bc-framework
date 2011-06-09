package cn.bc.web.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ui组件的抽象实现
 * 
 * @author dragon
 * 
 */
public abstract class AbstractComponent implements Component {
	private static Log logger = LogFactory.getLog(AbstractComponent.class);
	protected List<Render> children;
	protected Map<String, String> attrs = new LinkedHashMap<String, String>();
	private boolean beautiful = false;
	
	public AbstractComponent(){
		this.init();
	}

	protected void init() {
		// 初始化
	}

	public String getTitle() {
		return this.getAttr("title");
	}

	public Component setTitle(String title) {
		this.setAttr("title",title);
		return this;
	}

	/**
	 * 是否美化生成的代码
	 */
	public Component setBeautiful(boolean beautiful) {
		this.beautiful = beautiful;
		return this;
	}

	public StringBuffer render(StringBuffer main) {
		// 渲染属性：如<div id='...' name=...>
		renderAttrs(main);

		// 渲染子节点
		renderChildren(main);

		// 结束标记
		renderEndTag(main);
		if (logger.isDebugEnabled())
			logger.debug(main.toString());

		return main;
	}

	/**
	 * 渲染结束标记
	 * 
	 * @param main
	 *            主节点以初始化好的对象
	 */
	protected void renderEndTag(StringBuffer main) {
		main.append("</" + getTag() + ">");
	}

	/**
	 * 渲染属性
	 * 
	 * @param main
	 *            主节点以初始化好的对象
	 */
	protected void renderAttrs(StringBuffer main) {
		main.append("<" + getTag());
		Map<String, String> attrs = this.getAttrs();
		if (attrs != null && !attrs.isEmpty()) {
			for (Entry<String, String> c : attrs.entrySet()) {
				main.append(" " + c.getKey() + "='" + c.getValue() + "'");
			}
		}
		main.append(">");
	}

	public String toString() {
		StringBuffer main = new StringBuffer();
		render(main);
		return main.toString();
	}

	/**
	 * 渲染子节点
	 * 
	 * @param main
	 *            主节点以初始化好的对象
	 */
	protected void renderChildren(StringBuffer main) {
		List<Render> children = this.getChildren();
		if (children != null && !children.isEmpty()) {
			for (Render c : children) {
				if (beautiful)
					c.render(main.append("\r\n"));
				else
					c.render(main);
			}
		}
		if (beautiful)
			main.append("\r\n");
	}

	public Component addChild(Render child) {
		if (children == null)
			children = new ArrayList<Render>();
		if (child != null)
			children.add(child);
		return this;
	}

	public Component removeChild(Render child) {
		if (children != null)
			children.remove(child);
		return this;
	}

	protected Map<String, String> getAttrs() {
		return attrs;
	}

	public String getAttr(String key) {
		return attrs.get(key);
	}

	public Component setAttr(String key, String value) {
		if (value != null)
			attrs.put(key, value);
		else
			attrs.remove(key);
		return this;
	}

	public List<Render> getChildren() {
		return children;
	}

	public String getId() {
		return getAttr("id");
	}

	public Component setId(String id) {
		setAttr("id", id);
		return this;
	}

	public String getName() {
		return getAttr("name");
	}

	public Component setName(String name) {
		setAttr("name", name);
		return this;
	}

	public String getClazz() {
		return getAttr("class");
	}

	public Component addClazz(String clazz) {
		String cur = getAttr("class");
		if (cur != null)
			setAttr("class", cur + " " + clazz);
		else
			setAttr("class", clazz);

		return this;
	}

	public String getStyle() {
		return getAttr("style");
	}

	public Component addStyle(String key, String value) {
		String cur = getAttr("style");
		if (cur != null)
			setAttr("style", cur + ";" + key + ":" + value);
		else
			setAttr("style", key + ":" + value);
		return this;
	}

	public String getAction() {
		return getAttr("data-action");
	}

	public Component setAction(String action) {
		setAttr("data-action", action);
		return this;
	}
}
