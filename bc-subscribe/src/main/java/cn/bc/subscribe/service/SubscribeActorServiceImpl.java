package cn.bc.subscribe.service;

import java.util.List;

import org.commontemplate.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.domain.Actor;
import cn.bc.subscribe.dao.SubscribeActorDao;
import cn.bc.subscribe.domain.Subscribe;
import cn.bc.subscribe.domain.SubscribeActor;

/**
 * 订阅者Service接口的默认实现
 * 
 * @author lbj
 * 
 */
public class SubscribeActorServiceImpl extends
		DefaultCrudService<SubscribeActor> implements SubscribeActorService {
	private SubscribeActorDao subscribeActorDao;
	
	@Autowired
	public void setSubscribeActorDao(SubscribeActorDao subscribeActorDao) {
		this.subscribeActorDao = subscribeActorDao;
		this.setCrudDao(subscribeActorDao);
	}

	public List<SubscribeActor> findList(Subscribe subscribe) {
		return this.subscribeActorDao.findList(subscribe);
	}


	public SubscribeActor find4aidpid(Long aid, Long pid) {
		return this.subscribeActorDao.find4aidpid(aid, pid);
	}

	public void save(Long aid, Long pid, int type) {
		Assert.assertNotNull(aid);
		Assert.assertNotNull(pid);
		this.subscribeActorDao.save(aid, pid, type);
	}

	public void delete(Long aid, Long pid) {
		Assert.assertNotNull(aid);
		Assert.assertNotNull(pid);
		this.subscribeActorDao.delete(aid, pid);
	}

	public List<Actor> findList2Actor(Subscribe subscribe) {
		return this.subscribeActorDao.findList2Actor(subscribe);
	}

}