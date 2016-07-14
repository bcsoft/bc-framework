package cn.bc.identity.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.DutyDao;
import cn.bc.identity.domain.Duty;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

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
	public Map<String, Object> data(int pageNo, int pageSize, String search) {
		boolean ignoreCase = true;
		if (ignoreCase)
			return dao.dataByNativeQuery(pageNo, pageSize, search);
		else
			return dao.dataByJpaQuery(pageNo, pageSize, search);
	}
}