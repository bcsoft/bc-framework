package cn.bc.identity.service;

import cn.bc.core.Page;
import cn.bc.core.query.condition.AdvanceCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.DutyDao;
import cn.bc.identity.domain.Duty;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * 职务 Service 实现
 *
 * @author dragon 2016-07-19
 */
@Named
@Singleton
public class DutyServiceImpl extends DefaultCrudService<Duty> implements DutyService {
	private DutyDao dao;

	@Inject
	public void setDao(DutyDao dao) {
		this.dao = dao;
		super.setCrudDao(dao);
	}

	@Override
	public Page<Duty> page(Integer pageNo, Integer pageSize, List<AdvanceCondition> condition) {
		return dao.page(pageNo, pageSize, condition);
	}
}