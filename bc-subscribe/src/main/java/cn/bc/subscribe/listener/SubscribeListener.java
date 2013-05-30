/**
 * 
 */
package cn.bc.subscribe.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commontemplate.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.BCConstants;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.email.domain.Email;
import cn.bc.email.domain.EmailTo;
import cn.bc.email.service.EmailService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.subscribe.domain.Subscribe;
import cn.bc.subscribe.event.SubscribeEvent;
import cn.bc.subscribe.service.SubscribeActorService;
import cn.bc.subscribe.service.SubscribeService;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;

/**
 * 订阅事件的监听器
 * 
 * @author lbj
 * 
 */
public class SubscribeListener implements ApplicationListener<SubscribeEvent> {
	private static Log logger = LogFactory.getLog(SubscribeListener.class);

	private SubscribeService subscribeService;
	private SubscribeActorService subscribeActorService;
	private ActorService actorService;
	private ActorHistoryService actorHistoryService;
	private EmailService emailService;
	private IdGeneratorService idGeneratorService;
	private AttachService attachService;
	private OperateLogService operateLogService;
	private TemplateService templateService;
	
	@Autowired
	public void setSubscribeService(SubscribeService subscribeService) {
		this.subscribeService = subscribeService;
	}
	
	@Autowired
	public void setSubscribeActorService(SubscribeActorService subscribeActorService) {
		this.subscribeActorService = subscribeActorService;
	}
	
	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}
	
	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}
	
	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}
	
	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}
	
	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	public void onApplicationEvent(SubscribeEvent event) {
		Assert.assertNotNull(event);
		Assert.assertNotNull(event.getCode());
		Assert.assertNotNull(event.getSubject());
		
		if(logger.isDebugEnabled()){
			logger.debug("eventCode:"+event.getCode());
			logger.debug("eventSubject:"+event.getSubject());
			logger.debug("eventContent:"+event.getContent());
		}
		
		//查找系统中是否 拥有此订阅
	    Subscribe sb =	this.subscribeService.loadByEventCode(event.getCode());
	    //声明订阅用户
	    List<Actor> actors;
	    
	    // 先排除不发送订阅情况
	    if(sb == null){
	    	if(logger.isDebugEnabled())
				logger.debug("Subscribe is null!");
	    	return;
	    }
	    
	    if(sb.getStatus()!=BCConstants.STATUS_ENABLED){
	    	if(logger.isDebugEnabled())
				logger.debug("Subscribe status is not enabled!");
	    	return;
	    }

	    // 取得需要发送订阅的用户名单
	    actors=this.findActors(null,this.subscribeActorService.findList2Actor(sb));
	    
	    if(actors == null || actors .size()==0){
	    	if(logger.isDebugEnabled())
				logger.debug("SubscribeActor size equal 0!");
	    	return;
	    }
	    
	    //日志记录
	    OperateLog worklog = new OperateLog();
		worklog.setPtype(Subscribe.class.getSimpleName());
		worklog.setPid(sb.getId()+"");
		worklog.setType(OperateLog.TYPE_WORK);// 工作日志
		worklog.setWay(OperateLog.WAY_SYSTEM);
		worklog.setOperate(OperateLog.OPERATE_CREATE);
		worklog.setFileDate(Calendar.getInstance());
		//超级管理员
		worklog.setAuthor(this.actorHistoryService.loadByCode("admin"));
		worklog.setUid(this.idGeneratorService.next("WorkLog"));
	    worklog.setSubject("发送订阅："+event.getSubject());
	    worklog.setContent("");
	    
	    //发送邮件
	    this.sendEmail(event,actors,worklog);
	    
	    //保存日志
	    this.operateLogService.save(worklog);
	}
	
	private void sendEmail(SubscribeEvent event,List<Actor> actors,OperateLog worklog){
		//工作日志的详细内容
		String worklog_content=worklog.getContent();
		Email email=new Email();
		worklog_content+="发送方式：邮件[<br>";
		
		String emailUid=this.idGeneratorService.next(Email.ATTACH_TYPE);
		email.setUid(emailUid);
		email.setStatus(Email.STATUS_SENDED);
		email.setType(Email.TYPE_NEW);
		email.setFileDate(Calendar.getInstance());
		email.setSendDate(Calendar.getInstance());
		email.setSubject(event.getSubject());
		worklog_content+="邮件主题： "+event.getSubject()+"<br>";
		
		Map<String,Object> args = new HashMap<String,Object>();
		args.put("content", event.getContent());
		
		// 根据事件的CODE 加上 “-EMAIL”后缀 查找模板中是否有配置 自定义的模板
		Template custom = this.templateService.loadByCode(event.getCode()+"-EMAIL");
		
		if(custom != null){
			email.setContent(this.templateService.format(custom.getCode(), args));
		}else{//使用默认的邮件模板
			email.setContent(this.templateService.format("BC-EMAIL-SYSTEMAUTOFORWARD", args));
		}
		worklog_content+="邮件内容： "+event.getContent()+"<br>";
		//系统管理员发送
		Actor admin=this.actorService.loadByCode("admin");
		email.setSender(admin);
		
		if(event.getAttachs() != null){
			for(Attach attach:event.getAttachs()){
				//复制附件
				this.attachService.doCopy(attach.getPtype()
						, attach.getPuid(),Email.ATTACH_TYPE, emailUid, true);
			}
		}
		
		//设置发送人
		Set<EmailTo> emailTos = new HashSet<EmailTo>();
		worklog_content+="邮件接收人：";
		EmailTo et;
		int i = 0;
		for(Actor a : actors){
			et = new EmailTo();
			et.setEmail(email);
			et.setRead(false);
			et.setReceiver(a);
			//都为密送
			et.setType(EmailTo.TYPE_BCC);
			et.setOrderNo(i);
			emailTos.add(et);
			
			if(i>0)worklog_content+="、";
			worklog_content+=a.getName();
			i++;
		}
		worklog_content+="<br>]";
		worklog.setContent(worklog_content);
		email.setTo(emailTos);
		
		this.emailService.save(email);
	}
	
	
	// 取得需要发送订阅的用户名单
	private List<Actor> findActors(List<Actor> actors,List<Actor> _actors){
		if(actors == null)
			actors=new ArrayList<Actor>();
		// 声明部门 单位 岗位下的用户临时变量
		List<Actor> juniorActors;
		for(Actor actor:_actors){
			//用户
			if(actor.getType() == Actor.TYPE_USER){
				if(!actors.contains(actor)){
					actors.add(actor);
				}
			}else if(actor.getType() == Actor.TYPE_GROUP){//岗位
				juniorActors=this.actorService.findFollower(actor.getId(), 
						new Integer[] { ActorRelation.TYPE_BELONG },
						new Integer[] { Actor.TYPE_USER});
				//递归处理
				if(juniorActors != null && juniorActors.size()>0)
					findActors(actors,juniorActors);
			}else{//部门 或 单位
				juniorActors=this.actorService.findDescendantUser(actor.getId(), 
						new Integer[]{BCConstants.STATUS_ENABLED}, Actor.TYPE_UNIT,Actor.TYPE_DEPARTMENT);
				//递归处理
				if(juniorActors != null && juniorActors.size()>0)
					findActors(actors,juniorActors);
			}
			
		}
		
		return actors;
	}
	
}
