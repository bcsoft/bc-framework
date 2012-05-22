package cn.bc.report.service;

import java.util.Date;

import cn.bc.core.service.CrudService;
import cn.bc.report.domain.ReportTask;

/**
 * 报表任务Service接口
 * 
 * @author lbj
 * 
 */
public interface ReportTaskService extends CrudService<ReportTask> {
	/**
	 * 计划任务的调度，如果计划已经处于调度状态则重置该任务
	 * 
	 * @param taskId
	 * @return 任务的下次执行时间
	 */
	Date doStart(Long taskId) throws Exception;

	/**
	 * 停止调度
	 * 
	 * @param taskId
	 */
	void doStop(Long taskId) throws Exception;
}
