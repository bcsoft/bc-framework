/**
 * 
 */
package cn.bc.feedback.web.struts2;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.service.CrudService;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.feedback.domain.Feedback;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 用户反馈表单Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeedbackAction extends EntityAction<Long, Feedback> {
	// private static Log logger = LogFactory.getLog(FeedbackAction.class);
	private static final long serialVersionUID = 1L;
	private IdGeneratorService idGeneratorService;
	private AttachService attachService;
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
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		if (this.getE() != null && this.getE().isNew())
			return false;
		else
			return !context.hasAnyRole(getText("key.role.bc.feedback"),
					getText("key.role.bc.admin"));// 反馈管理或超级管理角色
	}

	@Override
	protected void afterCreate(Feedback entity) {
		SystemContext context = (SystemContext) this.getContext();
		Feedback e = this.getE();
		e.setFileDate(Calendar.getInstance());
		e.setAuthor(context.getUserHistory());

		e.setStatus(Feedback.STATUS_DRAFT);

		e.setUid(this.idGeneratorService.next("feedback.uid"));

		// 构建附件控件
		attachsUI = buildAttachsUI(true, false);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(600);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (this.getE().isNew() && editable) {
			// 预览按钮
			pageOption.addButton(new ButtonOption(getText("label.preview"),
					"preview"));

			// 提交按钮
			pageOption.addButton(new ButtonOption(getText("label.submit"),
					"submit"));
		}
	}

	@Override
	protected void beforeSave(Feedback entity) {
		Feedback e = this.getE();
		if (e.getStatus() == Feedback.STATUS_DRAFT)
			e.setStatus(Feedback.STATUS_SUMMIT);

		SystemContext context = (SystemContext) this.getContext();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
	}

	public AttachWidget attachsUI;

	@Override
	protected void afterEdit(Feedback entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false);

		// TODO 获取回复信息列表
	}

	@Override
	protected void afterOpen(Feedback entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, true);

		// TODO 获取回复信息列表
	}

	@Override
	protected void initForm(boolean editable) {
		super.initForm(editable);
	}

	private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = "feedback.main";
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
					.getE().getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));

		attachsUI.setReadOnly(forceReadonly ? true : this.isReadonly()
				|| !this.getE().isNew());
		return attachsUI;
	}
}
