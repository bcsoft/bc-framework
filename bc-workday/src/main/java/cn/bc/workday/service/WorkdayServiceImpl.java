package cn.bc.workday.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.workday.dao.WorkdayDao;
import cn.bc.workday.domain.Workday;

public class WorkdayServiceImpl extends DefaultCrudService<Workday> implements WorkdayService{
	private WorkdayDao workdayDao;
	@Autowired
	public void setWorkdayDao(WorkdayDao workdayDao) {
		this.workdayDao = workdayDao;
		this.setCrudDao(workdayDao);
	}
	
	@Override
	public Workday save(Workday entity) {
		
		return super.save(entity);
	}

	@Override
	public boolean checkDateIsCross(Date fromDate, Date toDate) {
		return this.workdayDao.checkDateIsCross(fromDate, toDate);
	}

	@Override
	public boolean checkDateIsCross(long id, Date fromDate, Date toDate) {
		return this.workdayDao.checkDateIsCross( id,fromDate, toDate);
	}

	@Override
	public int getRealWorkingdays(Date fromDate, Date toDate,
			int workdaysEveryWeeks) {
		return this.workdayDao.getRealWorkingdays(fromDate, toDate, workdaysEveryWeeks);
	}
}
