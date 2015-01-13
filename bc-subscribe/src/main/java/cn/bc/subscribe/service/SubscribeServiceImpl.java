package cn.bc.subscribe.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;

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
		Assert.notNull(subscribe);
		SystemContext context = SystemContextHolder.get();
		Actor actor=context.getUser();
		this.subscribeActorService.save(actor.getId(), subscribe.getId(), 0);
		String subject=actor.getName()+"添加"+subscribe.getSubject();
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_CREATE
						,subject,subject+"。");
	}

	public void doAddActor4Personal(List<Subscribe> subscribes) {
		Assert.notNull(subscribes);
		SystemContext context = SystemContextHolder.get();
		Actor actor=context.getUser();
		
		for(Subscribe subscribe : subscribes){
			this.subscribeActorService.save(actor.getId(), subscribe.getId(), 0);
			String subject=actor.getName()+"添加"+subscribe.getSubject();
			this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_CREATE
							,subject,subject+"。");
		}
	}

	public void doAddActor4Manager(Subscribe subscribe, Actor actor) {
		Assert.notNull(subscribe);
		Assert.notNull(actor);
		SystemContext context = SystemContextHolder.get();
		Actor operater=context.getUser();
		this.subscribeActorService.save(actor.getId(), subscribe.getId(), 1);
		String subject=actor.getName()+"添加"+subscribe.getSubject()+"[系统推送]";
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_CREATE
				,subject,subject+"，操作人："+operater.getName()+"。");
	}

	public void doAddActor4Manager(Subscribe subscribe, List<Actor> actors) {
		Assert.notNull(subscribe);
		Assert.notNull(actors);
		SystemContext context = SystemContextHolder.get();
		Actor operater=context.getUser();
		String actorNames = "";
		for(Actor actor: actors){
			this.subscribeActorService.save(actor.getId(), subscribe.getId(), 1);
			if(actorNames!="")actorNames += "、";
			actorNames +=actor.getName();
		}
		String subject=actorNames+"添加"+subscribe.getSubject()+"[系统推送]";
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_CREATE
				,subject,subject+"，操作人："+operater.getName()+"。");
		
	}
	
	public void doDeleteActor4Personal(Subscribe subscribe) {
		Assert.notNull(subscribe);
		SystemContext context = SystemContextHolder.get();
		Actor actor=context.getUser();
		this.subscribeActorService.delete(actor.getId(), subscribe.getId());
		String subject=actor.getName()+"删除 "+subscribe.getSubject();
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_DELETE
				,subject,subject+"。");
		
	}

	public void doDeleteActor4Personal(List<Subscribe> subscribes) {
		Assert.notNull(subscribes);
		SystemContext context = SystemContextHolder.get();
		Actor actor=context.getUser();
		
		for(Subscribe subscribe : subscribes){
			this.subscribeActorService.delete(actor.getId(), subscribe.getId());
			String subject=actor.getName()+"删除 "+subscribe.getSubject();
			this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_DELETE
					,subject,subject+"。");
		}	
	}

	public void doDeleteActor4Manager(Subscribe subscribe, Actor actor) {
		Assert.notNull(subscribe);
		Assert.notNull(actor);
		SystemContext context = SystemContextHolder.get();
		Actor operater=context.getUser();
		SubscribeActor sa=this.subscribeActorService.find4aidpid(actor.getId(), subscribe.getId());
		this.subscribeActorService.delete(actor.getId(), subscribe.getId());
		String subject = actor.getName()+"被删除"+subscribe.getSubject()+(sa.getType()==SubscribeActor.TYPE_PASSIVE?"[系统推送] ":"");
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_DELETE
				,subject,subject+"，操作人："+operater.getName()+"。");
		
	}

	public void doDeleteActor4Manager(Subscribe subscribe, List<Actor> actors) {
		Assert.notNull(subscribe);
		Assert.notNull(actors);
		SystemContext context = SystemContextHolder.get();
		Actor operater=context.getUser();
		String actorNames = "";
		SubscribeActor sa;
		for(Actor actor: actors){
			sa=this.subscribeActorService.find4aidpid(actor.getId(), subscribe.getId());
			this.subscribeActorService.delete(actor.getId(), subscribe.getId());
			if(actorNames!="")actorNames += "、";
			actorNames+=actor.getName()+(sa.getType()==SubscribeActor.TYPE_PASSIVE?"[系统推送] ":"");
		}
		
		String subject =actorNames +"被删除"+subscribe.getSubject();
		this.saveWorkLog(subscribe.getId()+"",OperateLog.OPERATE_DELETE
				,subject,subject+"，操作人："+operater.getName()+"。");
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