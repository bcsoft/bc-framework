package cn.bc.acl.service;

import java.util.List;

import cn.bc.acl.domain.AccessActor;
import cn.bc.acl.domain.AccessHistory;
import cn.bc.identity.domain.Actor;

/**
 * 访问控制Service接口
 * 
 * @author dragon
 * 
 */
public interface AccessService{
	/**
	 * 创建访问控制
	 * 
	 * @param docId
	 *            文档ID
	 * @param docType
	 *            文档类型
	 * @param accessType
	 *            访问类型
	 * @param visitorIDs
	 *            可访问者id列表(Actor的ID)
	 */
	void doAccessControl(Long docId, int docType, int accessType,
			Long[] visitorIDs);

	/**
	 * 创建访问历史
	 * 
	 * @param docId
	 *            文档ID
	 * @param docType
	 *            文档类型
	 * @param accessType
	 *            访问类型
	 * @param visitorHID
	 *            访问者id(ActorHistory的ID)
	 */
	void saveAccessHistory(Long docId, int docType, int accessType,
			Long visitorHID);

	/**
	 * 获取可访问者列表
	 * 
	 * @param docId
	 *            文档ID
	 * @param docType
	 *            文档类型
	 * @param accessType
	 *            访问类型
	 * @return
	 */
	List<Actor> findVisitor(Long docId, int docType, int accessType);

	/**
	 * 获取访问历史
	 * 
	 * @param docId
	 *            文档ID
	 * @param docType
	 *            文档类型
	 * @param accessType
	 *            访问类型
	 * @param visitorHID
	 *            访问者id(ActorHistory的ID)
	 * @return
	 */
	List<AccessHistory> findVisitHistory(Long docId, int docType,
			int accessType, Long visitorHID);

	/**
	 * 判断访问者能否访问
	 * 
	 * @param docId
	 *            文档ID
	 * @param docType
	 *            文档类型
	 * @param accessType
	 *            访问类型
	 * @param visitorID
	 *            访问者id(Actor的ID)
	 * @return
	 */
	boolean canVisit(Long docId, int docType, int accessType, Long visitorID);

	/**
	 * 判断访问者是否已经访问过
	 * 
	 * @param docId
	 *            文档ID
	 * @param docType
	 *            文档类型
	 * @param accessType
	 *            访问类型
	 * @param visitorID
	 *            访问者id(Actor的ID)
	 * @return
	 */
	boolean hadVisit(Long docId, int docType, int accessType, Long visitorID);

	/**
	 * 获取信息的访问人数和总访问次数
	 * 
	 * @param docId
	 *            文档ID
	 * @param docType
	 *            文档类型
	 * @param accessType
	 *            访问类型
	 * @return [0]-访问人数，[1]-总访问次数
	 */
	int[] visitedCount(Long docId, int docType, int accessType);
	
	
	/**
	 * 获取访问者对象
	 * @param actor 访问人
	 * @return
	 */
	List<AccessActor> find(Actor actor);
	
	/**
	 * 获取访问者对象
	 * @param actor访问人
	 * @param docType 文档类型
	 * @return
	 */
	List<AccessActor> find(Actor actor,String docType);
}