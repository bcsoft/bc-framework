/**
 * 
 */
package cn.bc.message.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.message.domain.Message;
import cn.bc.message.service.MessageService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.struts2.CrudAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.toolbar.ToolbarSearchButton;

/**
 * 消息Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class MessageAction extends CrudAction<Long, Message> {
	private static final long serialVersionUID = 1L;
	//private MessageService messageService;

	@Autowired
	public void setMessageService(MessageService messageService) {
		//this.messageService = messageService;
		this.setCrudService(messageService);
	}

	@Override
	protected GridData buildGridData() {
		return super.buildGridData().setRowLabelExpression("subject");
	}

	@Override
	protected Condition getCondition() {
		OrCondition condition = this.getSearchCondition();
		if (condition != null)
			condition.add(new OrderCondition("sendDate", Direction.Desc));
		return condition;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "subject", "content" };
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(620).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();
		// 删除按钮
		tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
				.setText(getText("label.delete")).setAction("delete"));

		// 搜索按钮
		ToolbarSearchButton sb = new ToolbarSearchButton();
		sb.setAction("search").setTitle(getText("title.click2search"));
		tb.addButton(sb);

		return tb;
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("read", getText("message.read"), 40)
				.setSortable(true).setFormater(new BooleanFormater()));
		columns.add(new TextColumn("sendDate", getText("message.sendDate"), 120)
				.setSortable(true).setDir(Direction.Asc)
				.setFormater(new CalendarFormater()));
		columns.add(new TextColumn("sender.name",
				getText("message.sender.name"), 100).setSortable(true));
		columns.add(new TextColumn("subject", getText("message.subject"))
				.setSortable(true));
		return columns;
	}
}
