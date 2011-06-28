/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.docs.domain.AttachHistory;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.CrudAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 附件处理痕迹Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AttachHistoryAction extends CrudAction<Long, AttachHistory> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;

	@Autowired
	public void setAttachHistoryService(
			@Qualifier(value = "attachHistoryService") CrudService<AttachHistory> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("subject");
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(680).setMinWidth(300)
				.setHeight(350).setMinHeight(300);
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();
		// 查看
		tb.addButton(getDefaultOpenToolbarButton());

		// 搜索
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "subject", "format", "authorName" };
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate",Direction.Desc);
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("fileDate",
				getText("attachHistory.fileDate"), 130).setSortable(true)
				.setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn("subject", getText("attachHistory.subject"))
		.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("type", getText("attachHistory.type"), 80)
				.setValueFormater(new KeyValueFormater(getTypes())));
		columns.add(new TextColumn("authorName",
				getText("attachHistory.authorName"), 80).setSortable(true));
		columns.add(new TextColumn("format", getText("attachHistory.format"),
				50).setSortable(true));
		return columns;
	}

	/**
	 * 获取类型值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(AttachHistory.TYPE_DOWNLOAD),
				getText("attachHistory.type.download"));
		types.put(String.valueOf(AttachHistory.TYPE_INLINE),
				getText("attachHistory.type.inline"));
		types.put(String.valueOf(AttachHistory.TYPE_ZIP),
				getText("attachHistory.type.zip"));
		types.put(String.valueOf(AttachHistory.TYPE_CONVERT),
				getText("attachHistory.type.convert"));
		types.put(String.valueOf(AttachHistory.TYPE_DELETED),
				getText("attachHistory.type.deleted"));
		return types;
	}
}
