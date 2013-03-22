package cn.bc.email.web.struts2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.email.domain.Email;
import cn.bc.email.domain.EmailTo;
import cn.bc.email.service.EmailService;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
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
	public String receivers;//邮件接收人
	

	private EmailService emailService;
	private AttachService attachService;
	private IdGeneratorService idGeneratorService;
	private ActorService actorService;
	
	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}

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
		// 非编辑状态没有任何操作按钮
		if (!editable)return;
		
		pageOption.addButton(new ButtonOption(getText("label.preview"), null,
				"bc.emailForm.preview"));
		
		pageOption
		.addButton(new ButtonOption(getText("email.send"), null,
				"bc.emailForm.save"));
		
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

	@Override
	protected void beforeSave(Email entity) {
		super.beforeSave(entity);
		//发送状态下设置发送日期
		if(entity.getStatus()==Email.STATUS_SENDED){
			entity.setSendDate(Calendar.getInstance());
		}
		
		Set<EmailTo> emailTos =new HashSet<EmailTo>();
		try {
			if (this.receivers != null && this.receivers.length() > 0) {
				EmailTo et;
				JSONArray jsons = new JSONArray(this.receivers);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					et=new EmailTo();
					et.setEmail(entity);
					et.setRead(false);
					et.setReceiver(this.actorService.load(json.getLong("id")));
					et.setType(json.getInt("type"));
					et.setOrderNo(i);
					emailTos.add(et);
				}
			}
			
			if(this.getE().getTo()!=null){
				this.getE().getTo().clear();
				this.getE().getTo().addAll(emailTos);
			}else{
				this.getE().setTo(emailTos);
			}

		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	

	
	//预览
	public String preview() throws Exception{
		Email e=this.getE();
		e.setSendDate(Calendar.getInstance());
		
		this.setE(e);
		// 强制表单只读
		this.formPageOption = buildFormPageOption(false);
		
		this.attachsUI=this.buildAttachsUI(false,true);
		
		return "formr";
	}

}