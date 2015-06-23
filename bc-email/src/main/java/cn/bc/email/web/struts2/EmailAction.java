package cn.bc.email.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.email.domain.Email;
import cn.bc.email.domain.EmailHistory;
import cn.bc.email.domain.EmailTo;
import cn.bc.email.service.EmailHistoryService;
import cn.bc.email.service.EmailService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

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
	public Integer type = 0;// 0：新邮件
	public Integer openType;// 类型 1-已发邮件 2-已收邮件 3-垃圾邮件
	public String receivers;// 邮件接收人
	public String week4cn;//星期
	public Integer trashSource;//从垃圾箱查看时的来源 1-发件箱，2-收件箱
	public String trashHandleDate;//移动到垃圾箱时的操作时间
	
	public List<Actor> receiverList;//收件人集合
	public List<Actor> ccList;//抄送集合
	public List<Actor> bccList;//密送集合
	public Map<Long,List<Actor>> owenGroupUserMap;//在上级组织中的用户，map.key为：上级组织的id

	private EmailService emailService;
	private EmailHistoryService emailHistoryService;
	private AttachService attachService;
	private IdGeneratorService idGeneratorService;
	private ActorService actorService;

	@Autowired
	public void setEmailHistoryService(EmailHistoryService emailHistoryService) {
		this.emailHistoryService = emailHistoryService;
	}

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
		entity.setType(Email.TYPE_NEW);
		entity.setUid(this.idGeneratorService.next(Email.ATTACH_TYPE));
		entity.setFileDate(Calendar.getInstance());

		SystemContext context = (SystemContext) this.getContext();
		entity.setSender(context.getUser());

		this.attachsUI = this.buildAttachsUI(true, false);
	}

	@Override
	protected void afterOpen(Email entity) {
		super.afterOpen(entity);
		this.attachsUI = this.buildAttachsUI(false, true);
		
		this.week4cn=DateUtils.getWeekCN(entity.getSendDate());
		
		SystemContext context = (SystemContext) this.getContext();
		
		//标记为已读
		if(this.openType.equals(2)){
			this.emailService.mark(new Long[]{entity.getId()}, context.getUser().getId(), true);
		}
		
		//插入查看历史
		if(this.openType.equals(1)||this.openType.equals(2)||this.openType.equals(3)){
			EmailHistory eh=this.emailHistoryService.create();
			eh.setEmail(entity);
			eh.setFileDate(Calendar.getInstance());
			eh.setReader(context.getUserHistory());
			this.emailHistoryService.save(eh);
		}
		
		this.sortable();
	}
	
	@Override
	protected void afterEdit(Email entity) {
		this.attachsUI = this.buildAttachsUI(false, false);
	}
	
	@Override
	protected PageOption buildPageOption(boolean editable) {
		return super.buildPageOption(editable).setWidth(630)
				.setMinHeight(200).setHeight(460);
	}

	@Override
	protected void buildPageButtons(PageOption pageOption, boolean editable) {
		// 非编辑状态没有任何操作按钮
		if (!editable)
			return;
		pageOption.addButton(new ButtonOption(getText("label.save")+getText("bc.status.draft"), null,
				"bc.emailForm.save"));
		pageOption.addButton(new ButtonOption(getText("email.send"), null,
				"bc.emailForm.send"));

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
		
		//发送时保存
		if(entity.getStatus() == Email.STATUS_SENDED){
		
			// 设置发送日期
			entity.setSendDate(Calendar.getInstance());

			Set<EmailTo> emailTos = new HashSet<EmailTo>();
			Set<EmailTo> del_emailTos = new HashSet<EmailTo>();
			try {
				if (this.receivers != null && this.receivers.length() > 0) {
					JSONArray jsons = new JSONArray(this.receivers);
					JSONObject json;
					EmailTo et;
					Actor upper;
					Actor receiver;
					List<Actor> lis;
					int j = 0;
					for (int i = 0; i < jsons.length(); i++) {
						json = jsons.getJSONObject(i);
						// 已添加的用户
						if (json.getInt("type") == 4) {
							receiver = this.actorService.load(json.getLong("id"));
							// 检测待添加的收件人是否与带上级中的重复。
							for (EmailTo to : emailTos) {
								if (to.getReceiver().equals(receiver)
										&& to.getUpper() != null) {
									del_emailTos.add(to);
								}
							}
							// 删除带岗位的收件人，优先保存不带岗位的
							for (EmailTo to : del_emailTos) {
								emailTos.remove(to);
							}
	
							et = new EmailTo();
							et.setEmail(entity);
							et.setRead(false);
							et.setReceiver(receiver);
							et.setType(json.getInt("toType"));
							et.setOrderNo(i + j);
							emailTos.add(et);
						} else {
							// 部门或岗位
							upper = this.actorService.load(json.getLong("id"));
							
							//岗位
							if(upper.getType()== Actor.TYPE_GROUP){
								lis = this.actorService.findFollowerWithName(
										json.getLong("id"),null,
										new Integer[] { ActorRelation.TYPE_BELONG },
										new Integer[] { Actor.TYPE_USER },
										new Integer[]{BCConstants.STATUS_ENABLED});
							//部门
							}else{
								lis = this.actorService.findDescendantUser(json.getLong("id"), 
										new Integer[]{BCConstants.STATUS_ENABLED}, Actor.TYPE_UNIT,Actor.TYPE_DEPARTMENT);
							}
							
							
							for (Actor a : lis) {
								boolean _save = true;
								// 已保存的接收人不再进行保存
								for (EmailTo to : emailTos) {
									if (to.getReceiver().equals(a)) {
										_save = false;
									}
								}
	
								if (_save) {
									et = new EmailTo();
									et.setEmail(entity);
									et.setRead(false);
									et.setReceiver(a);
									et.setUpper(upper);
									et.setType(json.getInt("toType"));
									et.setOrderNo(i + j);
									emailTos.add(et);
									j++;
								}
							}
						}
					}
				}
	
				if (this.getE().getTos() != null) {
					this.getE().getTos().clear();
					this.getE().getTos().addAll(emailTos);
				} else {
					this.getE().setTos(emailTos);
				}
	
			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
				try {
					throw e;
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
			
		}else{//草稿时保存
			Set<EmailTo> emailTos = new HashSet<EmailTo>();
			try {
				if (this.receivers != null && this.receivers.length() > 0) {
					JSONArray jsons = new JSONArray(this.receivers);
					JSONObject json;
					EmailTo et;
					for (int i = 0; i < jsons.length(); i++) {
						json = jsons.getJSONObject(i);
						et = new EmailTo();
						et.setEmail(entity);
						et.setRead(false);
						et.setType(json.getInt("toType"));
						et.setOrderNo(i);

						// 已添加的用户
						if (json.getInt("type") == 4) {
							et.setReceiver(this.actorService.load(json.getLong("id")));
						} else {
							// 部门或岗位
							et.setUpper(this.actorService.load(json.getLong("id")));

						}
						emailTos.add(et);
					}
				}
	
				if (this.getE().getTos() != null) {
					this.getE().getTos().clear();
					this.getE().getTos().addAll(emailTos);
				} else {
					this.getE().setTos(emailTos);
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
	}
	
	// 回复邮件
	public String reply() throws Exception {
		if (this.getId() == null)
			throw new CoreException("must set property id!");
		// 需要回复的邮件
		Email entity = this.emailService.load(this.getId());

		// 初始化E
		this.setE(createEntity());
		// 初始化表单的配置信息
		this.pageOption = buildFormPageOption(true);
		this.getE().setEmail(entity);
		this.getE().setStatus(Email.STATUS_DRAFT);
		this.getE().setType(Email.TYPE_REPLY);
		this.getE().setUid(this.idGeneratorService.next(Email.ATTACH_TYPE));
		this.getE().setFileDate(Calendar.getInstance());
		SystemContext context = (SystemContext) this.getContext();
		this.getE().setSender(context.getUser());
		// 设置回复的主题
		this.getE().setSubject(
				getText("email.reply") + "：" + entity.getSubject());

		// 回复的发送人
		Actor sender = entity.getSender();
		EmailTo et = new EmailTo();
		et.setRead(false);
		et.setOrderNo(0);
		et.setReceiver(sender);
		et.setType(EmailTo.TYPE_TO);
		Set<EmailTo> ets = new HashSet<EmailTo>();
		ets.add(et);
		this.getE().setTos(ets);

		// 设置回复的内容
		String content = "<div>&nbsp;</div><div>&nbsp;</div>";
		content += "<p><span style=\"background-color: rgb(238, 236, 225);\">";
		content += "－－－－&nbsp;回复邮件信息&nbsp;－－－－<br>";
		content += "<b>发件人</b>：" + entity.getSender().getName() + "<br>";
		content += "<b>日期</b>："
				+ DateUtils.formatCalendar2Minute(entity.getSendDate());
		content += "&nbsp;(" + DateUtils.getWeekCN(entity.getSendDate())
				+ ")<br>";
		content += "<b>主题</b>：" + entity.getSubject() + "<br>";
		content += "</span></p>";
		content += entity.getContent();
		this.getE().setContent(content);

		this.attachsUI = this.buildAttachsUI(true, false);
		return "form";
	}

	// 转发
	public String forward() throws Exception {
		if (this.getId() == null)
			throw new CoreException("must set property id!");
		// 需要转发的邮件
		Email entity = this.emailService.load(this.getId());
		// 初始化E
		this.setE(createEntity());
		// 初始化表单的配置信息
		this.pageOption = buildFormPageOption(true);
		this.getE().setEmail(entity);
		this.getE().setStatus(Email.STATUS_DRAFT);
		this.getE().setType(Email.TYPE_FORWARD);
		this.getE().setUid(this.idGeneratorService.next(Email.ATTACH_TYPE));
		this.getE().setFileDate(Calendar.getInstance());
		SystemContext context = (SystemContext) this.getContext();
		this.getE().setSender(context.getUser());

		// 设置转发的主题
		this.getE().setSubject(
				getText("email.forwoard") + "：" + entity.getSubject());
		// 设置转发的内容
		String content = "<div>&nbsp;</div><div>&nbsp;</div>";
		content += "<p><span style=\"background-color: rgb(238, 236, 225);\">";
		content += "－－－－&nbsp;转发邮件信息&nbsp;－－－－<br>";
		content += "<b>发件人</b>：" + entity.getSender().getName() + "<br>";

		// 收件人信息
		String receiver = "";
		// 抄送信息
		String cc = "";

		Actor receiver_upper=null;
		Actor cc_upper=null;
		
		for (EmailTo et : entity.getTos()) {
			// 收件人的信息
			if (et.getType() == EmailTo.TYPE_TO) {
				if(et.getUpper() == null){
					if (receiver.length()>0)receiver += "、";
					receiver += et.getReceiver().getName();
				}else if(!et.getUpper().equals(receiver_upper) || receiver_upper == null){
					if (receiver.length()>0)receiver += "、";
					receiver += et.getUpper().getName();
					receiver_upper=et.getUpper();
				}
			}
			
			// 抄送信息
			if (et.getType() == EmailTo.TYPE_CC) {
				if(et.getUpper() == null){
					if (cc.length()>0)cc += "、";
					cc += et.getReceiver().getName();
				}else if(!et.getUpper().equals(cc_upper) || cc_upper == null){
					if (cc.length()>0)cc += "、";
					cc += et.getUpper().getName();
					cc_upper=et.getUpper();
				}
			}
		}

		if (receiver.length() > 0)
			content += "<b>收件人</b>：" + receiver + "<br>";
		if (cc.length() > 0)
			content += "<b>抄送</b>：" + cc + "<br>";
		if (receiver.length() == 0 && cc.length() == 0)
			content += "<b>收件人</b>：" + "<br>";

		content += "<b>日期</b>："
				+ DateUtils.formatCalendar2Minute(entity.getSendDate());
		content += "&nbsp;(" + DateUtils.getWeekCN(entity.getSendDate())
				+ ")<br>";
		content += "<b>主题</b>：" + entity.getSubject() + "<br>";
		content += "</span></p>";
		content += entity.getContent();

		this.getE().setContent(content);

		// 复制附件的处理
		this.attachService.doCopy(Email.ATTACH_TYPE, entity.getUid(),
				Email.ATTACH_TYPE, this.getE().getUid(), false);
		this.attachsUI = this.buildAttachsUI(false, false);
		return "form";
	}
	
	public Boolean read;
	
	//标记邮件的状态
	public String mark() throws Exception{
		if(this.getIds()==null)throw new CoreException("must set property ids!");
		if(this.read==null)throw new CoreException("must set property read!");
		Long[] ids = cn.bc.core.util.StringUtils
				.stringArray2LongArray(this.getIds().split(","));
		SystemContext context = (SystemContext) this.getContext();
		this.emailService.mark(ids,context.getUser().getId(), this.read);
		Json json=new Json();
		json.put("success", true);
		json.put("msg", getText("email.mark.success"));
		this.json=json.toString();
		return "json";
	}
	
	
	//将未读的邮件都标记为已读
	public String mark4read() throws Exception{
		SystemContext context = (SystemContext) this.getContext();
		this.emailService.mark4read(context.getUser().getId());
		Json json=new Json();
		json.put("success", true);
		json.put("msg", getText("email.mark.success"));
		this.json=json.toString();
		return "json";
	}
	
	//将收件人分类
	private void sortable(){
		Set<EmailTo> tos= this.getE().getTos();
		if(tos == null)return;
		for(EmailTo to:tos){
			//拥有上级
			if(to.getUpper() != null){
				switch(to.getType()){
					case EmailTo.TYPE_TO:
							if(this.receiverList==null)this.receiverList=new ArrayList<Actor>();
							//若集合中拥有此上级组织 不再加进集合中
							if(!this.receiverList.contains(to.getUpper()))this.receiverList.add(to.getUpper());
						break;
					case EmailTo.TYPE_CC:
							if(this.ccList==null)this.ccList=new ArrayList<Actor>();
							if(!this.ccList.contains(to.getUpper()))this.ccList.add(to.getUpper());
						break;
					case EmailTo.TYPE_BCC:
							if(this.bccList==null)this.bccList=new ArrayList<Actor>();
							if(!this.bccList.contains(to.getUpper()))this.bccList.add(to.getUpper());
						break;
					default:return;
				}
				//将拥有上级组织的收件人添加到集合中，解释时再遍历出这些收件人
				this.addOwenGroupUserMap(to.getUpper().getId(), to.getReceiver());
			
			}else{
				switch(to.getType()){
					case EmailTo.TYPE_TO:
							if(this.receiverList==null)this.receiverList=new ArrayList<Actor>();
							this.receiverList.add(to.getReceiver());
						break;
					case EmailTo.TYPE_CC:
							if(this.ccList==null)this.ccList=new ArrayList<Actor>();
							this.ccList.add(to.getReceiver());
						break;
					case EmailTo.TYPE_BCC:
							if(this.bccList==null)this.bccList=new ArrayList<Actor>();
							this.bccList.add(to.getReceiver());
						break;
					default:return;
				}
			}
		}
	}
	
	//将拥有上级组织的收件人添加到集合中，解释时再遍历出这些收件人
	private void addOwenGroupUserMap(Long upperId,Actor receiver){
		if(this.owenGroupUserMap==null)this.owenGroupUserMap=new HashMap<Long, List<Actor>>();
		List<Actor> userList;
		if(this.owenGroupUserMap.containsKey(upperId)){
			userList = this.owenGroupUserMap.get(upperId);
		}else{
			userList=new ArrayList<Actor>();
		}
		userList.add(receiver);
		this.owenGroupUserMap.put(upperId,userList);
	}

}