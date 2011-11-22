package cn.bc.web.ui.html.toolbar;

import cn.bc.web.ui.html.menu.Menu;
import cn.bc.web.ui.html.menu.MenuItem;

/**
 * 工具条带下拉菜单的按钮
 * 
 * @author dragon
 * 
 */
public class ToolbarMenuButton extends ToolbarButton {
	private Menu menu;// 下拉菜单

	public ToolbarMenuButton(String text) {
		this.setText(text);
	}

	@Override
	protected void init() {
		super.init();
		this.addClazz("bc-menuButton");
		this.setSecondaryIcon("ui-icon-triangle-1-s");
		this.setMenuContainer(".bc-page");
	}

	@Override
	public void renderButton(StringBuffer main) {
		super.renderButton(main);

		// 将菜单隐藏在按钮内
		this.addChild(this.menu);
	}

	public String getChange() {
		return this.getAttr("data-change");
	}

	/**
	 * 设置用户选中菜单项的回调函数名，上下文为按钮所在窗口，第一个参数为选中的项({value:[value],text:[text]})
	 * 
	 * @param change
	 * @return
	 */
	public ToolbarMenuButton setChange(String change) {
		this.setAttr("data-change", change);
		return this;
	}

	public String getMenuContainer() {
		return this.getAttr("data-menucontainer");
	}

	/**
	 * 下拉菜单所在容器的选择表达式，js将使用this.closest([menuSelector])来获取,如果没有设置就放到父容器
	 * 
	 * @param menuContainer
	 * @return
	 */
	public ToolbarMenuButton setMenuContainer(String menuContainer) {
		this.setAttr("data-menucontainer", menuContainer);
		return this;
	}

	public Menu getMenu() {
		return menu;
	}

	/**
	 * 设置下拉菜单
	 * 
	 * @param menu
	 */
	public void setMenu(Menu menu) {
		this.menu = menu;
		initMenuInner();
	}

	/**
	 * 添加一个菜单项
	 * 
	 * @param label
	 *            显示的文字
	 * @param value
	 *            对应的值
	 * @return
	 */
	public ToolbarMenuButton addMenuItem(String label, String value) {
		if (this.menu == null) {
			this.menu = new Menu();
			initMenuInner();
		}
		this.menu.addMenuItem(new MenuItem(label, value));
		return this;
	}

	/**
	 * 添加一个菜单项
	 * 
	 * @param labelOrValue
	 *            显示的文字或值
	 * @return
	 */
	public ToolbarMenuButton addMenuItem(String labelOrValue) {
		if (this.menu == null) {
			this.menu = new Menu();
			initMenuInner();
		}
		this.menu.addMenuItem(new MenuItem(labelOrValue, labelOrValue));
		return this;
	}

	private void initMenuInner() {
		this.menu.addStyle("display", "none");
		this.menu.addStyle("position", "absolute");
	}
}