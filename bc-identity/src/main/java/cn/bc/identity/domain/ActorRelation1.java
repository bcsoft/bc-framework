/**
 * 
 */
package cn.bc.identity.domain;

import java.io.Serializable;


/**
 * Actor之间的关联关系 如：<p>1) 用户所隶属的单位或部门 </p><p>2) 用户所在岗位  </p><p>3) 组织的负责人、正职、副职、归档人等 </p> <p>4) 不同组织间的业务关系 </p>
 * @author  dragon
 */
public interface ActorRelation1 extends Serializable{
	/**
	 * 关联类型：默认的隶属或包含关系
	 * <ul>
	 * <li>部门下的下级部门</li>
	 * <li>部门下的岗位</li>
	 * <li>部门下的人员</li>
	 * </ul>
	 * <ul>
	 * <li>单位下的下级单位</li>
	 * <li>单位下的下级部门</li>
	 * <li>单位下的岗位</li>
	 * <li>单位下的人员</li>
	 * </ul>
	 * <ul>
	 * <li>岗位下的人员</li>
	 * </ul>
	 */
	public static final Integer TYPE_BELONG = 0;
	
	/**
	 * 关联类型：自定义的关系，其值请从10开始使用，0到9为底层保留的关系，方便日后平台底层的扩展
	 * <p>这里仅是举个例子说明自定义的用途，如部门下的主管领导、分管领导、文档管理员等</p>
	 */
	public static final Integer TYPE_CUSTOM = 10;

	/**
	 * @return   返回关联关系中的主控方，如岗位与用户关系中的岗位
	 */
	Actor getMaster();
	
	/**
	 * 设置关联关系中的主控方
	 * @param  master
	 */
	void setMaster(Actor master);
	
	/**
	 * @return   返回关联关系中的从属方，如岗位与用户关系中的用户
	 */
	Actor getFollower();
	
	/**
	 * 设置关联关系中的从属方
	 * @param  follower
	 */
	void setFollower(Actor follower	);
	
	/**
	 * @return  返回关联类型
	 */
	Integer getType();
	
	/**
	 * 设置关联类型
	 * @param  string
	 */
	void setType(Integer string);
	
	/**
	 * @return  返回多个从属方之间的排序号
	 */
	String getOrder();
	
	/**
	 * 设置多个从属方之间的排序号
	 * @param order
	 */
	void setOrder(String order);
}