package cn.bc.report.dao;

import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.report.domain.ReportTask;

/**
 * 报表任务Dao接口
 * 
 * @author lbj
 * 
 */
public interface ReportTaskDao extends CrudDao<ReportTask> {
	/**
	 * 获取所有可用的调度任务配置
	 * 
	 * @return
	 */
	List<ReportTask> findAllEnabled();
}
