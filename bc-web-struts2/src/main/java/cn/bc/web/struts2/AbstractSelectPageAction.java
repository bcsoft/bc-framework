/**
 * 
 */
package cn.bc.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 选择对话框的抽象Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class AbstractSelectPageAction<T extends Object> extends
		ViewAction<T> {
	private static final long serialVersionUID = 1L;

	/** 获取点击确认按钮的回调函数名 */
	protected abstract String getClickOkMethod();

	@Override
	protected String getGridDblRowMethod() {
		return this.getClickOkMethod();
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setMinWidth(260)
				.setHeight(450).setMinHeight(220).setModal(true)
				.setMinimizable(false).setMaximizable(false)
				.addButton(buildOkButton());
	}

	/**
	 * 创建对话框的确认按钮
	 * 
	 * @return
	 */
	protected ButtonOption buildOkButton() {
		return new ButtonOption(getOkButtonLabel(), null, getClickOkMethod());
	}

	/**
	 * 对话框确认按钮显示的文字
	 * 
	 * @return
	 */
	protected String getOkButtonLabel() {
		return getText("label.ok");
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 添加空白条
		tb.addButton(Toolbar.getDefaultEmptyToolbarButton());

		// 添加搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}

	@Override
	protected String getFormActionName() {
		// TODO Auto-generated method stub
		return null;
	}
}