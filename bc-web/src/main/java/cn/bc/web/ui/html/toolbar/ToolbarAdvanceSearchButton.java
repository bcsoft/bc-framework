package cn.bc.web.ui.html.toolbar;

import cn.bc.web.ui.html.A;

/**
 * 工具条高级搜索按钮
 * 
 * @author dragon
 * 
 */
public class ToolbarAdvanceSearchButton extends ToolbarSearchButton {
	protected void init() {
		super.init();
		this.addClazz("advance");
	}

	public ToolbarAdvanceSearchButton() {
		super();
	}

	/**
	 * 高级搜索按钮的鼠标提示信息
	 * 
	 * @return
	 */
	public String getAdvanceTitle() {
		return this.getAttr("advanceTitle");
	}

	public ToolbarAdvanceSearchButton setAdvanceTitle(String advanceTitle) {
		this.setAttr("advanceTitle", advanceTitle);
		return this;
	}

	/**
	 * 获取条件窗口html的url
	 * 
	 * @return
	 */
	public String getConditionsFormUrl() {
		return this.getAttr("data-url");
	}

	public ToolbarAdvanceSearchButton setConditionsFormUrl(
			String conditionsFormUrl) {
		this.setAttr("data-url", conditionsFormUrl);
		return this;
	}

	public void renderButton(StringBuffer main) {
		super.renderButton(main);
		this.addChild(new A().setId("advanceSearchBtn")
				.addClazz("ui-icon ui-icon-triangle-1-s")
				.setTitle(this.getAdvanceTitle()));
		this.setAdvanceTitle(null);
	}
}
