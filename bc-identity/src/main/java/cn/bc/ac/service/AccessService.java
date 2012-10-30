package cn.bc.ac.service;

import java.util.List;

import cn.bc.ac.domain.AccessHistory;
import cn.bc.identity.domain.Actor;

/**
 * 访问控制Service接口
 * 
 * @author dragon
 * 
 */
public interface AccessService {
	/**
	 * 创建访问控制
	 * 
	 * @param docId
	 * @param docType
	 * @param accessType
	 * @param visitors
	 *            可访问者
	 */
	void doAccessControl(long docId, int docType, int accessType,
			List<Actor> visitors);

	/**
	 * 创建访问历史
	 * 
	 * @param docId
	 * @param docType
	 * @param accessType
	 * @param visitorId
	 */
	void saveAccessHistory(long docId, int docType, int accessType,
			long visitorId);

	/**
	 * 获取可访问者列表
	 * 
	 * @param docId
	 * @param docType
	 * @param accessType
	 * @return
	 */
	List<Actor> findVisitor(long docId, int docType, int accessType);

	/**
	 * 获取访问历史
	 * 
	 * @param docId
	 * @param docType
	 * @param accessType
	 * @param visitorId
	 * @return
	 */
	List<AccessHistory> findVisitHistory(long docId, int docType,
			int accessType, long visitorId);

	/**
	 * 判断访问者能否访问
	 * 
	 * @param docId
	 * @param docType
	 * @param accessType
	 * @param visitorId
	 * @return
	 */
	boolean canVisit(long docId, int docType, int accessType, long visitorId);

	/**
	 * 判断访问者是否已经访问过
	 * 
	 * @param docId
	 * @param docType
	 * @param accessType
	 * @param visitorId
	 * @return
	 */
	boolean hadVisit(long docId, int docType, int accessType, long visitorId);
}
