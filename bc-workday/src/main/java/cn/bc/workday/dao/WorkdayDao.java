package cn.bc.workday.dao;

import java.util.Date;

import cn.bc.core.dao.CrudDao;
import cn.bc.workday.domain.Workday;

/**
 * 工作日模块的Dao
 * @author LeeDane
 *
 */
public interface WorkdayDao extends CrudDao<Workday>{
	/**
	 * 检查开始日期和结束日期是否出现时间交叉
	 * @param fromDate  开始日期
	 * @param toDate  结束日期
	 */
	boolean checkDateIsCross(Date fromDate, Date toDate);
	
	/**
	 * 检查开始日期和结束日期是否出现时间交叉
	 * @param id  编辑状态时的id
	 * @param fromDate  开始日期
	 * @param toDate  结束日期
	 */
	boolean checkDateIsCross(long id, Date fromDate, Date toDate);
}