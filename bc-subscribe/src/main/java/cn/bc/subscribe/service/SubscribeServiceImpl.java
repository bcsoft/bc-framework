package cn.bc.subscribe.service;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.subscribe.dao.SubscribeDao;
import cn.bc.subscribe.domain.Subscribe;
import cn.bc.subscribe.domain.SubscribeActor;

/**
 * 订阅Service接口的默认实现
 * 
 * @author lbj
 * 
 */
public class SubscribeServiceImpl extends DefaultCrudService<Subscribe> implements SubscribeService{
	private SubscribeDao subscribeDao;
	private SubscribeActorService subscribeActorService;
	private OperateLogService operateLogService;
	private IdGeneratorService idGeneratorService;
	
	@Autowired
	public void setSubscribeDao(SubscribeDao subscribeDao) {
		this.subscribeDao = subscribeDao;
		this.setCrudDao(subscribeDao);
	}
	
	@Autowired
	public void setSubscribeActorService(SubscribeActorService subscribeActorService) {
		this.subscribeActorService = subscribeActorService;
	}

	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public boolean isUnique(String eventCode, Long id) {
		return this.subscribeDao.isUnique(eventCode, id);
	}

	public void doAddActor4Personal(Subscribe subscribe) {
		Assert.assertNotNull(subscribe);
		SystemContext context = SystemContextHolder.get();
		Actor actor=context.getUser();
		this.subscribeActorService.save(actor.getId(), subscribe.getId(), 0);
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_CREATE
						,"用户<b>"+actor.getName()+"</b>添加订阅","用户：<b>"
						+actor.getName()+"</b>，添加的订阅：<b>"+subscribe.getSubject()+"</b>.");
	}

	public void doAddActor4Personal(List<Subscribe> subscribes) {
		Assert.assertNotNull(subscribes);
		SystemContext context = SystemContextHolder.get();
		Actor actor=context.getUser();
		
		for(Subscribe subscribe : subscribes){
			this.subscribeActorService.save(actor.getId(), subscribe.getId(), 0);
			this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_CREATE
							,"用户<b>"+actor.getName()+"</b>添加订阅","用户：<b>"
							+actor.getName()+"</b>，添加的订阅：<b>"+subscribe.getSubject()+"</b>.");
		}
	}

	public void doAddActor4Manager(Subscribe subscribe, Actor actor) {
		Assert.assertNotNull(subscribe);
		Assert.assertNotNull(actor);
		this.subscribeActorService.save(actor.getId(), subscribe.getId(), 1);
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_CREATE
				,"用户<b>"+actor.getName()+"</b>被配置系统推送订阅",
				"用户：<b>"+actor.getName()+"</b>，被配置系统推送的订阅：<b>"
				+subscribe.getSubject()+"</b>.");
	}

	public void doAddActor4Manager(Subscribe subscribe, List<Actor> actors) {
		Assert.assertNotNull(subscribe);
		Assert.assertNotNull(actors);
		SystemContext context = SystemContextHolder.get();
		Actor operater=context.getUser();
		
		String actorNames = "";
		for(Actor actor: actors){
			this.subscribeActorService.save(actor.getId(), subscribe.getId(), 1);
			if(actorNames!="")actorNames += "、";
			actorNames +=actor.getName();
		}
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_CREATE
				,"用户<b>"+actorNames+"</b>被配置系统推送订阅",
				"用户：<b>"+actorNames+"</b>，被配置系统推送的订阅：<b>"
				+subscribe.getSubject()+"</b>,操作人：<b>"
				+operater.getName()+"</b>.");
		
	}
	
	public void doDeleteActor4Personal(Subscribe subscribe) {
		Assert.assertNotNull(subscribe);
		SystemContext context = SystemContextHolder.get();
		Actor actor=context.getUser();
		this.subscribeActorService.delete(actor.getId(), subscribe.getId());
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_DELETE
				,"用户<b>"+actor.getName()+"</b>删除订阅","用户：<b>"
				+actor.getName()+"</b>，删除的订阅：<b>"+subscribe.getSubject()+"</b>.");
		
	}

	public void doDeleteActor4Personal(List<Subscribe> subscribes) {
		Assert.assertNotNull(subscribes);
		SystemContext context = SystemContextHolder.get();
		Actor actor=context.getUser();
		
		for(Subscribe subscribe : subscribes){
			this.subscribeActorService.delete(actor.getId(), subscribe.getId());
			this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_DELETE
					,"用户<b>"+actor.getName()+"</b>删除订阅","用户：<b>"
					+actor.getName()+"</b>，删除的订阅：<b>"+subscribe.getSubject()+"</b>.");
		}	
	}

	public void doDeleteActor4Manager(Subscribe subscribe, Actor actor) {
		Assert.assertNotNull(subscribe);
		Assert.assertNotNull(actor);
		SubscribeActor sa=this.subscribeActorService.find4aidpid(actor.getId(), subscribe.getId());
		this.subscribeActorService.delete(actor.getId(), subscribe.getId());
		
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_DELETE
				,"用户<b>"+actor.getName()+"</b>被删除"+(sa.getType()==SubscribeActor.TYPE_ACTIVE?"订阅":"系统推送订阅"),"用户：<b>"
				+actor.getName()+"</b>，被删除的"+(sa.getType()==SubscribeActor.TYPE_ACTIVE?"订阅":"系统推送订阅")+"<b>"+subscribe.getSubject()+"</b>.");
		
	}

	public void doDeleteActor4Manager(Subscribe subscribe, List<Actor> actors) {
		Assert.assertNotNull(subscribe);
		Assert.assertNotNull(actors);
		String actorNames = "";
		SubscribeActor sa;
		for(Actor actor: actors){
			sa=this.subscribeActorService.find4aidpid(actor.getId(), subscribe.getId());
			this.subscribeActorService.delete(actor.getId(), subscribe.getId());
			if(actorNames!="")actorNames += "、";
			actorNames+="<b"+actor.getName()+"</b>"+(sa.getType()==SubscribeActor.TYPE_ACTIVE?"":"[系统推送]");
		}
		
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_DELETE
				,actorNames+"删除订阅"
				,actorNames+"删除订阅，"+"订阅：<b>"+subscribe.getSubject()+"</b>。");
	}
	
	//工作日志
	private OperateLog saveWorkLog(String pid,String operate,String subject,
			String content) {
		OperateLog worklog = new OperateLog();
		worklog.setPtype(Subscribe.class.getSimpleName());
		worklog.setPid(pid);
		worklog.setType(OperateLog.TYPE_WORK);// 工作日志
		worklog.setWay(OperateLog.WAY_SYSTEM);
		worklog.setOperate(operate);
		worklog.setFileDate(Calendar.getInstance());
		worklog.setAuthor(SystemContextHolder.get().getUserHistory());
		worklog.setSubject(subject);
		worklog.setContent(content);
		worklog.setUid(this.idGeneratorService.next("WorkLog"));

		return operateLogService.save(worklog);
	}

	public Subscribe loadByEventCode(String eventCode) {
		return this.subscribeDao.loadByEventCode(eventCode);
	}
	
}