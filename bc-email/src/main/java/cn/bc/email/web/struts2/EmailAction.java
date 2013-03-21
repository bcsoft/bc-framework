package cn.bc.email.web.struts2;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.email.domain.Email;
import cn.bc.email.service.EmailService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 邮件表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class EmailAction extends EntityAction<Long, Email> {
	private static final long serialVersionUID = 1L;
	public Integer type = 0;

	private EmailService emailService;
	private AttachService attachService;
	private IdGeneratorService idGeneratorService;

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
		this.setCrudService(emailService);
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public AttachWidget attachsUI;

	@Override
	protected void afterCreate(Email entity) {
		super.afterCreate(entity);
		// 状态为草稿
		entity.setStatus(Email.STATUS_DRAFT);
		entity.setType(this.type);
		entity.setUid(this.idGeneratorService.next(Email.ATTACH_TYPE));
		entity.setFileDate(Calendar.getInstance());

		SystemContext context = (SystemContext) this.getContext();
		entity.setSender(context.getUser());
		
		this.attachsUI=this.buildAttachsUI(true,false);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(660)
				.setMinHeight(200).setMinWidth(450).setMaxHeight(800);
	}
	
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		
	}

	protected AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = Email.ATTACH_TYPE;
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

}