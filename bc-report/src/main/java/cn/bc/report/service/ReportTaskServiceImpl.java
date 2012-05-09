package cn.bc.report.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.report.dao.ReportTaskDao;
import cn.bc.report.domain.ReportTask;

/**
 * 报表任务Service接口的实现
 * 
 * @author lbj
 * 
 */
public class ReportTaskServiceImpl extends
		DefaultCrudService<ReportTask> implements ReportTaskService {
//	private static Log logger = LogFactory.getLog(ReportTemplateServiceImpl.class);

	@SuppressWarnings("unused")
	private ReportTaskDao reportTaskDao;

	@Autowired
	public void setReportTaskDao(ReportTaskDao reportTaskDao) {
		this.setCrudDao(reportTaskDao);
		this.reportTaskDao=reportTaskDao;
	}

}
