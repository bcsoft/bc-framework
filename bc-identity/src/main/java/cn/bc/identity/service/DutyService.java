package cn.bc.identity.service;

import cn.bc.core.Page;
import cn.bc.core.query.condition.AdvanceCondition;
import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.Duty;

import java.util.List;

/**
 * 职务 Service
 *
 * @author dragon 2016-07-19
 */
public interface DutyService extends CrudService<Duty> {
	/**
	 * 获取职务列表信息
	 *
	 * @param pageNo    页码，为空代表第一页
	 * @param pageSize  页容量，为空代表系统默认
	 * @param condition 过滤条件
	 */
	Page<Duty> page(Integer pageNo, Integer pageSize, List<AdvanceCondition> condition);
}
