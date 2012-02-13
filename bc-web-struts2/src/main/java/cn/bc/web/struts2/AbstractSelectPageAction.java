/**
 * 
 */
package cn.bc.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.web.struts2.jpa.ViewActionWithJpa;
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
		ViewActionWithJpa<T> {
	private static final long serialVersionUID = 1L;

	/** 获取点击确认按钮的回调函数名 */
	protected abstract String getClickOkMethod();

	@Override
	protected String getGridDblRowMethod() {
		return this.getClickOkMethod();
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super
				.getHtmlPageOption()
				.setWidth(400)
				.setMinWidth(260)
				.setHeight(450)
				.setMinHeight(220)
				.setModal(true)
				.setMinimizable(false)
				.setMaximizable(false)
				.addButton(
						new ButtonOption(getText("label.ok"), null,
								getClickOkMethod()));
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