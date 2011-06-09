package cn.bc.web.ui.html.menu;

import cn.bc.web.ui.html.A;
import cn.bc.web.ui.html.Li;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Text;

public class MenuItem extends Li {
	private String label;
	private String url;
	private String iconClass;
	private Menu childMenu;

	public String getType() {
		return getAttr("data-type");
	}

	public MenuItem setType(String type) {
		setAttr("data-type", type);
		return this;
	}

	public String getValue() {
		return getAttr("data-value");
	}

	public MenuItem setValue(String value) {
		setAttr("data-value", value);
		return this;
	}

	public String getLabel() {
		return label;
	}

	public MenuItem setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public MenuItem setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getIconClass() {
		return iconClass;
	}

	public MenuItem setIconClass(String iconClass) {
		this.iconClass = iconClass;
		return this;
	}

	public Menu getChildMenu() {
		return childMenu;
	}

	public void setChildMenu(Menu childMenu) {
		this.childMenu = childMenu;
		this.childMenu.addClazz("childMenu");
	}

	public StringBuffer render(StringBuffer main) {
		// 清空环境
		if (this.children != null)
			this.children.clear();

		// 创建链接
		A a = new A();
		this.addChild(a);
		a.setAttr("href",
				(this.url != null && this.url.length() > 0) ? this.url : "#");
		if (this.label != null && this.label.length() > 0)//显示的名称
			a.addChild(new Text(this.label));
		if (this.iconClass != null && this.iconClass.length() > 0){//TODO 显示的图标
			Span span = new Span();
			span.addClazz("ui-icon menuItem-icon " + this.iconClass);
			a.addChild(span);
		}
		
		// 创建子菜单
		if (childMenu != null)
			this.addChild(this.childMenu);

		return super.render(main);
	}
}
