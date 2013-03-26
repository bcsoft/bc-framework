package cn.bc.email.web.struts2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.exception.CoreException;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.email.domain.Email;
import cn.bc.email.domain.EmailTo;
import cn.bc.email.service.EmailService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
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
	public Integer type = 0;//0：新邮件
	public Integer openType = 0;//类型 0：表示查看已发邮件 1：表示查看已收邮件
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
	
	//回复邮件
	public String reply() throws Exception {
		if(this.getId() == null) throw new CoreException("id is null!");
		//需要回复的邮件
		Email entity=this.emailService.load(this.getId());
		
		// 初始化E
		this.setE(createEntity());
		// 初始化表单的配置信息
		this.formPageOption = buildFormPageOption(true);
		this.getE().setStatus(Email.STATUS_DRAFT);
		this.getE().setType(Email.TYPE_REPLY);
		this.getE().setUid(this.idGeneratorService.next(Email.ATTACH_TYPE));
		this.getE().setFileDate(Calendar.getInstance());
		SystemContext context = (SystemContext) this.getContext();
		this.getE().setSender(context.getUser());
		//设置回复的主题
		this.getE().setSubject(getText("email.reply")+"："+entity.getSubject());
		
		//回复的发送人
		Actor sender=entity.getSender();
		EmailTo et=new EmailTo();
		et.setRead(false);
		et.setOrderNo(0);
		et.setReceiver(sender);
		et.setType(EmailTo.TYPE_TO);
		Set<EmailTo> ets=new HashSet<EmailTo>();
		ets.add(et);
		this.getE().setTo(ets);
		
		
		//设置回复的内容
		String content="<div>&nbsp;</div><div>&nbsp;</div>";
		content+="<p><span style=\"background-color: rgb(238, 236, 225);\">";
		content+="－－－－&nbsp;回复邮件信息&nbsp;－－－－<br>";
		content+="<b>发件人</b>："+entity.getSender().getName()+"<br>";
		content+="<b>日期</b>："+DateUtils.formatCalendar2Minute(entity.getSendDate());
		content+="&nbsp;("+DateUtils.getWeekCN(entity.getSendDate())+")<br>";
		content+="<b>主题</b>："+entity.getSubject()+"<br>";
		content+="</span></p>";
		content+=entity.getContent();
		this.getE().setContent(content);
		
		this.attachsUI=this.buildAttachsUI(true,false);
		return "form";
	}
	
	//转发
	public String forward() throws Exception {
		if(this.getId() == null) throw new CoreException("id is null!");
		//需要转发的邮件
		Email entity=this.emailService.load(this.getId());
		// 初始化E
		this.setE(createEntity());
		// 初始化表单的配置信息
		this.formPageOption = buildFormPageOption(true);
		this.getE().setStatus(Email.STATUS_DRAFT);
		this.getE().setType(Email.TYPE_FORWARD);
		this.getE().setUid(this.idGeneratorService.next(Email.ATTACH_TYPE));
		this.getE().setFileDate(Calendar.getInstance());
		SystemContext context = (SystemContext) this.getContext();
		this.getE().setSender(context.getUser());
		
		//设置转发的主题
		this.getE().setSubject(getText("email.forwoard")+"："+entity.getSubject());
		//设置转发的内容
		String content="<div>&nbsp;</div><div>&nbsp;</div>";
		content+="<p><span style=\"background-color: rgb(238, 236, 225);\">";
		content+="－－－－&nbsp;转发邮件信息&nbsp;－－－－<br>";
		content+="<b>发件人</b>："+entity.getSender().getName()+"<br>";
		
		//收件人信息
		String receiver="";
		//抄送信息
		String cc="";

		int i = 0;
		int j = 0;
		for(EmailTo et:entity.getTo()){
			//收件人的信息
			if(et.getType()==EmailTo.TYPE_TO){
				if(i >0) receiver+="、";
				receiver+=et.getReceiver().getName();
				i++;
			}
			//抄送信息
			if(et.getType()==EmailTo.TYPE_CC){
				if(j >0) cc+="、";
				cc+=et.getReceiver().getName();
				j++;
			}
		}
		
		if(receiver.length()>0)content+="<b>收件人</b>："+receiver+"<br>";
		
		if(cc.length()>0)content+="<b>抄送</b>："+cc+"<br>";
		
		
		if(receiver.length()==0 && cc.length()==0)content+="<b>收件人</b>："+"<br>";
		
		content+="<b>日期</b>："+DateUtils.formatCalendar2Minute(entity.getSendDate());
		content+="&nbsp;("+DateUtils.getWeekCN(entity.getSendDate())+")<br>";
		content+="<b>主题</b>："+entity.getSubject()+"<br>";
		content+="</span></p>";
		content+=entity.getContent();
		
		this.getE().setContent(content);
		
		//复制附件的处理
		this.attachService.doCopy(Email.ATTACH_TYPE, entity.getUid(), Email.ATTACH_TYPE, this.getE().getUid(), false);
		this.attachsUI=this.buildAttachsUI(false,false);
		return "form";
	}
	
	

	@Override
	protected void afterOpen(Email entity) {
		super.afterOpen(entity);
		this.attachsUI=this.buildAttachsUI(false,true);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(670)
				.setMinHeight(200).setHeight(460);
	}
	
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		// 非编辑状态没有任何操作按钮
		if (!editable)return;
		
		//pageOption.addButton(new ButtonOption(getText("label.preview"), null,
				//"bc.emailForm.preview"));
		
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
		Set<EmailTo> del_emailTos =new HashSet<EmailTo>();
		try {
			if (this.receivers != null && this.receivers.length() > 0) {
				JSONArray jsons = new JSONArray(this.receivers);
				JSONObject json;
				EmailTo et;
				Actor upper;
				Actor receiver;
				List<Actor> lis;
				int j=0;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					//已添加的用户
					if(json.getInt("type")==4){
						receiver=this.actorService.load(json.getLong("id"));
						//检测待添加的收件人是否与带上级中的重复。
						for(EmailTo to:emailTos){
							if(to.getReceiver().equals(receiver)&&to.getUpper()!=null){
								del_emailTos.add(to);
							}
						}
						//删除带岗位的收件人，优先保存不带岗位的
						for(EmailTo to:del_emailTos){
								emailTos.remove(to);
						}
						
						et=new EmailTo();
						et.setEmail(entity);
						et.setRead(false);
						et.setReceiver(receiver);
						et.setType(json.getInt("toType"));
						et.setOrderNo(i+j);
						emailTos.add(et);
					}else{
						//部门或岗位
						upper=this.actorService.load(json.getLong("id"));
						lis=this.actorService.findFollower(json.getLong("id")
								,new Integer[] { ActorRelation.TYPE_BELONG }
								, new Integer[]{Actor.TYPE_USER});
						for(Actor a:lis){
							boolean _save=true;
							//已保存的接收人不再进行保存
							for(EmailTo to:emailTos){
								if(to.getReceiver().equals(a)){
									_save=false;
								}
							}
							
							if(_save){
								et=new EmailTo();
								et.setEmail(entity);
								et.setRead(false);
								et.setReceiver(a);
								et.setUpper(upper);
								et.setType(json.getInt("toType"));
								et.setOrderNo(i+j);
								emailTos.add(et);
							}
						}
					}
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