/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.util.Calendar;
import java.util.List;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.FileSizeFormater;
import cn.bc.web.struts2.CrudAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 附件Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AttachAction extends CrudAction<Long, Attach> implements
		SessionAware {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.setCrudService(attachService);
	}

	@Override
	public String create() throws Exception {
		SystemContext context = (SystemContext) this.getContext();
		Attach e = this.getCrudService().create();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUser());
		e.setDepartId(context.getBelong().getId());
		e.setDepartName(context.getBelong().getName());
		e.setUnitId(context.getUnit().getId());
		e.setUnitName(context.getUnit().getName());

		this.setE(e);
		return "form";
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("subject");
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(900).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();

		// 是否附件管理员
		boolean isManager = ((SystemContext) this.getContext())
				.hasAnyPriviledge("attach.manager");

		if (isManager) {
			// 删除按钮
			tb.addButton(getDefaultDeleteToolbarButton());
		} else {// 普通用户
			tb.addButton(getDefaultOpenToolbarButton());
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "subject", "path", "extend", "ptype", "puid",
				"authorName" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("fileDate", getText("attach.fileDate"), 130)
				.setSortable(true).setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn("authorName", getText("attach.authorName"),
				80).setSortable(true));
		columns.add(new TextColumn("size", getText("attach.size"), 80)
				.setSortable(true).setValueFormater(new FileSizeFormater()));
		columns.add(new TextColumn("extend", getText("attach.extend"), 50)
				.setSortable(true));
		columns.add(new TextColumn("subject", getText("attach.subject"))
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("path", getText("attach.path"), 120)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("ptype", getText("attach.ptype"), 100)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("puid", getText("attach.puid"), 100)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("appPath", getText("attach.appPath"), 100)
				.setSortable(true).setValueFormater(
						new BooleanFormater(getText("label.yes"),
								getText("label.no"))));
		return columns;
	}
}
