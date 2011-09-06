/**
 * 
 */
package cn.bc.feedback.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.feedback.domain.Feedback;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 用户反馈Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeedbackAction extends EntityAction<Long, Feedback> implements
		SessionAware {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private IdGeneratorService idGeneratorService;
	private AttachService attachService;
	private String MANAGER_KEY = "R_MANAGER_FEEDBACK";// 管理角色的编码
	public boolean isManager;

	@Autowired
	public void setFeedbackService(
			@Qualifier(value = "feedbackService") CrudService<Feedback> crudService) {
		this.setCrudService(crudService);
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Override
	public String create() throws Exception {
		this.readonly = false;
		SystemContext context = (SystemContext) this.getContext();
		Feedback e = this.getCrudService().create();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUserHistory());

		e.setStatus(Feedback.STATUS_DRAFT);

		e.setUid(this.idGeneratorService.next("feedback.uid"));
		this.setE(e);

		// 构建附件控件
		attachsUI = buildAttachsUI(true);

		// 构建对话框参数
		this.formPageOption = buildFormPageOption();

		return "form";
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(680).setMinWidth(250)
				.setMinHeight(200).setModal(false);
		if (this.getE().isNew()) {
			option.addButton(new ButtonOption(getText("label.preview"),
					"preview"));
			option.addButton(new ButtonOption(getText("label.submit"), "submit"));
		}
		return option;
	}

	// 提交反馈
	@Override
	public String save() throws Exception {
		Feedback e = this.getE();
		if (e.getStatus() == Feedback.STATUS_DRAFT)
			e.setStatus(Feedback.STATUS_SUMMIT);
		
		SystemContext context = (SystemContext) this.getContext();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		
		this.getCrudService().save(e);
		return "saveSuccess";
	}

	public AttachWidget attachsUI;

	@Override
	public String edit() throws Exception {
		this.readonly = false;
		this.setE(this.getCrudService().load(this.getId()));
		this.formPageOption = buildFormPageOption();

		// 构建附件控件
		attachsUI = buildAttachsUI(false);

		// TODO 获取回复信息列表

		return "form";
	}

	private AttachWidget buildAttachsUI(boolean isNew) {
		isManager = isManager();
		// 构建附件控件
		String ptype = "feedback.main";
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(this.isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
					.getE().getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		attachsUI.setReadOnly(!this.getE().isNew());
		return attachsUI;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("subject");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected Condition getSpecalCondition() {
		SystemContext context = (SystemContext) this.getContext();
		// 是否本模块管理员
		isManager = isManager();

		if (isManager) {// 本模块管理员看全部
			return null;
		} else {// 普通用户仅看自己提交的
			return new EqualsCondition("author.id", context.getUserHistory().getId());
		}
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();

		isManager = isManager();

		// 新建按钮
		tb.addButton(getDefaultCreateToolbarButton());

		// 查看按钮
		tb.addButton(getDefaultOpenToolbarButton());

		if (isManager) {
			// 彻底删除按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
					.setText(getText("label.delete.clean")).setAction("delete"));
		} else {// 普通用户
			// 删除按钮
			tb.addButton(getDefaultDeleteToolbarButton());
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "subject", "content", "author.name" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否本模块管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status", getText("label.status"), 80)
				.setSortable(true).setValueFormater(
						new KeyValueFormater(getStatuses())));
		columns.add(new TextColumn("subject", getText("label.subject"))
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("fileDate", getText("label.submitDate"), 150)
				.setSortable(true).setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		if (isManager) {
			columns.add(new TextColumn("author.name",
					getText("label.submitterName"), 80).setSortable(true));
			columns.add(new TextColumn("author.unitName", getText("label.unitName"),
					80).setSortable(true));
		}
		return columns;
	}

	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}
//
//	@Override
//	protected String getJs() {
//		return contextPath + "/bc/feedback/list.js";
//	}

	/**
	 * 获取状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(Feedback.STATUS_DRAFT),
				getText("feedback.status.draft"));
		statuses.put(String.valueOf(Feedback.STATUS_SUMMIT),
				getText("feedback.status.submmit"));
		statuses.put(String.valueOf(Feedback.STATUS_ACCEPT),
				getText("feedback.status.accept"));
		statuses.put(String.valueOf(Feedback.STATUS_REJECT),
				getText("feedback.status.reject"));
		statuses.put(String.valueOf(Feedback.STATUS_DELETED),
				getText("feedback.status.deleted"));
		return statuses;
	}
}
