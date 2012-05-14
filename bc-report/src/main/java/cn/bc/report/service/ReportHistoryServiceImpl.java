package cn.bc.report.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.report.dao.ReportHistoryDao;
import cn.bc.report.domain.ReportHistory;

/**
 * 历史报表Service接口的实现
 * 
 * @author lbj
 * 
 */
public class ReportHistoryServiceImpl extends
		DefaultCrudService<ReportHistory> implements ReportHistoryService {
//	private static Log logger = LogFactory.getLog(ReportHistoryServiceImpl.class);

	private ReportHistoryDao reportHistoryDao;

	@Autowired
	public void setReportHistoryDao(ReportHistoryDao reportHistoryDao) {
		this.reportHistoryDao = reportHistoryDao;
		this.setCrudDao(reportHistoryDao);
	}

	public List<Map<String, String>> findCategoryOption() {
		return this.reportHistoryDao.findCategoryOption();
	}

	public List<Map<String, String>> findSourceOption() {
		return this.reportHistoryDao.findSourceOption();
	}
}
