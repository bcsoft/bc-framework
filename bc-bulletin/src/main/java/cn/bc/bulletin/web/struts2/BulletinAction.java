/**
 * 
 */
package cn.bc.bulletin.web.struts2;

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

import cn.bc.bulletin.domain.Bulletin;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.MixCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.CrudAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 电子公告Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class BulletinAction extends CrudAction<Long, Bulletin> implements
		SessionAware {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private IdGeneratorService idGeneratorService;
	private AttachService attachService;
	private String MANAGER_KEY = "R_MANAGER_BULLETIN";// 公告管理角色的编码
	public boolean isManager;

	@Autowired
	public void setBulletinService(
			@Qualifier(value = "bulletinService") CrudService<Bulletin> crudService) {
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
		SystemContext context = (SystemContext) this.getContext();
		Bulletin e = this.getCrudService().create();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUser());
		e.setAuthorDepartId(context.getBelong().getId());
		e.setAuthorDepartName(context.getBelong().getName());
		e.setAuthorUnitId(context.getUnit().getId());
		e.setAuthorUnitName(context.getUnit().getName());

		e.setScope(Bulletin.SCOPE_LOCALUNIT);
		e.setStatus(Bulletin.STATUS_DRAFT);

		e.setUid(this.idGeneratorService.next("bulletin.uid"));
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
		if (isManager()) {
			option.addButton(new ButtonOption(getText("label.preview"),
					"preview"));
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	public String save() throws Exception {
		Bulletin e = this.getE();
		if (e.getIssuer() != null && e.getIssuer().getId() == null)
			e.setIssuer(null);
		
		SystemContext context = (SystemContext) this.getContext();
		e.setModifierId(context.getUser().getId());
		e.setModifierName(context.getUser().getName());
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

		return "form";
	}

	private AttachWidget buildAttachsUI(boolean isNew) {
		isManager = isManager();
		// 构建附件控件
		String ptype = "bulletin.main";
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
		if (!isManager) {
			attachsUI.setReadOnly(true);
		}
		return attachsUI;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("subject");
	}

	@Override
	protected Condition getSpecalCondition() {
		SystemContext context = (SystemContext) this.getContext();
		// 是否公告管理员
		isManager = isManager();
		Actor unit = context.getUnit();

		// 其他单位且已发布的全系统公告
		Condition commonCondition = new AndCondition().setAddBracket(true)
				.add(new EqualsCondition("status", Bulletin.STATUS_ISSUED))
				.add(new EqualsCondition("scope", Bulletin.SCOPE_SYSTEM))
				.add(new NotEqualsCondition("unitId", unit.getId()));

		MixCondition c = new OrCondition().setAddBracket(true);
		if (isManager) {// 管理员看本单位的所有状态公告或全系统公告
			c.add(new EqualsCondition("unitId", unit.getId()));// 本单位公告
			c.add(commonCondition);
		} else {// 普通用户仅看已发布的本单位或全系统公告
			c.add(new AndCondition().add(
					new EqualsCondition("unitId", unit.getId())).add(
					new EqualsCondition("status", Bulletin.STATUS_ISSUED)));// 本单位已发布公告
			c.add(commonCondition);
		}
		return c;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		if (isManager()) {// 管理员看本单位的所有状态公告或全系统公告
			return new OrderCondition("status")
					.add("issueDate", Direction.Desc);
		} else {// 普通用户仅看已发布的本单位或全系统公告
			return new OrderCondition("issueDate", Direction.Desc);
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

		if (isManager) {
			// 新建按钮
			tb.addButton(getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(getDefaultEditToolbarButton());

			// 删除按钮
			tb.addButton(getDefaultDeleteToolbarButton());
		} else {// 普通用户
			// 查看按钮
			tb.addButton(getDefaultOpenToolbarButton());
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "subject", "content", "issuerName" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		// 是否公告管理员
		isManager = isManager();

		List<Column> columns = super.buildGridColumns();
		if (isManager)
			if (this.useColumn("status"))
				columns.add(new TextColumn("status",
						getText("bulletin.status"), 80).setSortable(true)
						.setValueFormater(new KeyValueFormater(getStatuses())));
		if (this.useColumn("issueDate"))
			columns.add(new TextColumn("issueDate",
					getText("bulletin.issueDate"), 90).setSortable(true)
					.setDir(Direction.Desc)
					.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		if (this.useColumn("issuerName"))
			columns.add(new TextColumn("issuerName",
					getText("bulletin.issuerName"), 90).setSortable(true));
		if (this.useColumn("subject"))
			columns.add(new TextColumn("subject", getText("bulletin.subject"))
					.setSortable(true).setUseTitleFromLabel(true));
		if (this.useColumn("scope"))
			columns.add(new TextColumn("scope", getText("bulletin.scope"), 90)
					.setSortable(true).setValueFormater(
							new KeyValueFormater(getEntityStatuses())));
		if (isManager) {
			if (this.useColumn("fileDate"))
				columns.add(new TextColumn("fileDate",
						getText("bulletin.fileDate"), 150)
						.setSortable(true)
						.setDir(Direction.Desc)
						.setValueFormater(
								new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
			if (this.useColumn("authorName"))
				columns.add(new TextColumn("authorName",
						getText("bulletin.authorName"), 80).setSortable(true));
			if (this.useColumn("unitName"))
				columns.add(new TextColumn("unitName",
						getText("bulletin.unitName"), 80).setSortable(true));
		}
		return columns;
	}

	// 判断当前用户是否是公告管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}

	@Override
	protected String getJs() {
		return contextPath + "/bc/bulletin/list.js";
	}

	/**
	 * 获取公告发布范围值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> scopes = new HashMap<String, String>();
		scopes = new HashMap<String, String>();
		scopes.put(String.valueOf(Bulletin.SCOPE_LOCALUNIT),
				getText("bulletin.scope.localUnit"));
		scopes.put(String.valueOf(Bulletin.SCOPE_SYSTEM),
				getText("bulletin.scope.system"));
		return scopes;
	}

	/**
	 * 获取公告状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(Bulletin.STATUS_DRAFT),
				getText("bulletin.status.draft"));
		statuses.put(String.valueOf(Bulletin.STATUS_ISSUED),
				getText("bulletin.status.issued"));
		statuses.put(String.valueOf(Bulletin.STATUS_OVERDUE),
				getText("bulletin.status.overdue"));
		return statuses;
	}
}
