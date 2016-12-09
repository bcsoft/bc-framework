package cn.bc.identity.dao;

import cn.bc.core.Page;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.AdvanceCondition;
import cn.bc.identity.domain.Duty;

import java.util.List;
import java.util.Map;

/**
 * 职务 Dao
 *
 * @author dragon 2016-07-19
 */
public interface DutyDao extends CrudDao<Duty> {
	/**
	 * 获取职务列表信息
	 *
	 * @param pageNo    页码，为空代表第一页
	 * @param pageSize  页容量，为空代表系统默认
	 * @param condition 过滤条件
	 */
	Page<Duty> page(Integer pageNo, Integer pageSize, List<AdvanceCondition> condition);

	/**
	 * For Learning: 获取视图分页数据（使用 JPA）
	 *
	 * @param pageNo   页码
	 * @param pageSize 页容量
	 * @param search   过滤的内容
	 * @return
	 */
	Map<String, Object> dataByJpaQuery(int pageNo, int pageSize, String search);

	/**
	 * For Learning: 获取视图分页数据（使用 JPA 原生查询）
	 *
	 * @param pageNo   页码
	 * @param pageSize 页容量
	 * @param search   过滤的内容
	 * @return
	 */
	Map<String, Object> dataByNativeQuery(int pageNo, int pageSize, String search);
}