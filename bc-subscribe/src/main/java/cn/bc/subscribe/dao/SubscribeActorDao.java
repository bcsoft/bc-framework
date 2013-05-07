package cn.bc.subscribe.dao;

import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.subscribe.domain.Subscribe;
import cn.bc.subscribe.domain.SubscribeActor;

/**
 * 订阅者Dao接口
 * 
 * @author lbj
 * 
 */
public interface SubscribeActorDao extends CrudDao<SubscribeActor> {
	
	/**
	 * 查找订阅者
	 * @param subscribe
	 * @return
	 */
	List<SubscribeActor> findList(Subscribe subscribe);
	
	/**
	 * 查找订阅者
	 * @param aid
	 * @param pid
	 * @return
	 */
	SubscribeActor find4aidpid(Long aid,Long pid);
	
	
	/**
	 * 删除订阅者
	 * 
	 * @param aid 用户id
	 * @param pid 订阅id
	 * @return
	 */
	void delete(Long aid,Long pid);
	
	
	void delete(SubscribeActor subscribeActor);
	
	void delete(List<SubscribeActor> subscribeActors);
	
	/**
	 * 添加订阅者
	 * 
	 * @param aid 用户id
	 * @param pid 订阅id
	 * @param type 类型 0-用户订阅， 1-系统推送
	 * @return
	 */
	void save(Long aid,Long pid,int type);
	
}
