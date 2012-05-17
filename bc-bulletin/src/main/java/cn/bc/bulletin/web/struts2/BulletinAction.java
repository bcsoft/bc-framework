/**
 * 
 */
package cn.bc.bulletin.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.bulletin.domain.Bulletin;
import cn.bc.core.service.CrudService;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 电子公告Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class BulletinAction extends EntityAction<Long, Bulletin> implements
		SessionAware {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private IdGeneratorService idGeneratorService;
	private AttachService attachService;
	public String statusDesc;

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
	public boolean isReadonly() {
		if (this.getE() != null) {// 表单
			return this.getE().getStatus() != Bulletin.STATUS_DRAFT;
		} else {// 视图
			SystemContext context = (SystemContext) this.getContext();
			boolean readonly = !context.hasAnyRole(
					getText("key.role.bc.bulletin"),
					getText("key.role.bc.admin"));// 电子公告管理或超级管理角色
			return readonly;
		}
	}

	@Override
	protected void afterCreate(Bulletin entity) {
		SystemContext context = (SystemContext) this.getContext();
		Bulletin e = this.getE();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUserHistory());
		e.setUnit(context.getUnit());

		e.setScope(Bulletin.SCOPE_SYSTEM);
		e.setStatus(Bulletin.STATUS_DRAFT);

		e.setUid(this.idGeneratorService.next("bulletin.uid"));

		// 构建附件控件
		attachsUI = buildAttachsUI(true, false);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(680)
				.setMaxHeight(700);
	}

	@Override
	protected void buildFormPageButtons(PageOption option, boolean editable) {
		if (!this.isReadonly()) {
			option.addButton(new ButtonOption(getText("label.preview"),
					"preview"));
			if (this.getE().getStatus() == Bulletin.STATUS_DRAFT)
				option.addButton(new ButtonOption(getText("bulletin.issue"),
						null, "bc.bulletinForm.issue"));
			option.addButton(this.getDefaultSaveButtonOption());
		} else {
			option.addButton(new ButtonOption(getText("bulletin.seeByNewWin"),
					"preview"));
		}
	}

	@Override
	protected void beforeSave(Bulletin entity) {
		Bulletin e = this.getE();
		if (e.getIssuer() != null && e.getIssuer().getId() == null)
			e.setIssuer(null);

		SystemContext context = (SystemContext) this.getContext();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
	}

	// 发布
	public String issue() throws Exception {
		SystemContext context = (SystemContext) this.getContext();
		Bulletin e = this.getE();

		// 最后修改人
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());

		// 发布人
		e.setIssuer(context.getUser());
		e.setIssueDate(Calendar.getInstance());
		e.setStatus(Bulletin.STATUS_ISSUED);

		this.getCrudService().save(e);

		Json json = new Json();
		json.put("id", e.getId());
		json.put("msg", getText("bulletin.issueSuccess"));
		this.json = json.toString();
		return "json";
	}

	public AttachWidget attachsUI;

	@Override
	protected void afterEdit(Bulletin entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false);
	}

	@Override
	protected void afterOpen(Bulletin entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, true);
	}

	protected AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = "bulletin.main";
		String puid = this.getE().getUid();
		boolean readonly = forceReadonly ? true : this.isReadonly();
		AttachWidget attachsUI = AttachWidget.defaultAttachWidget(isNew,
				readonly, isFlashUpload(), this.attachService, ptype, puid);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));

		return attachsUI;
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态描述
		statusDesc = this.getStatuses().get(
				String.valueOf(this.getE().getStatus()));
	}

	@Override
	protected String getJs() {
		return contextPath + "/bc/bulletin/list.js";
	}

	/**
	 * 获取公告状态值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(Bulletin.STATUS_DRAFT),
				getText("bulletin.status.draft"));
		statuses.put(String.valueOf(Bulletin.STATUS_ISSUED),
				getText("bulletin.status.issued"));
		statuses.put(String.valueOf(Bulletin.STATUS_OVERDUE),
				getText("bulletin.status.overdue"));
		return statuses;
	}
}
