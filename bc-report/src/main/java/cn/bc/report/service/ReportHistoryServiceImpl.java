package cn.bc.report.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.bc.core.exception.NotExistsException;
import cn.bc.core.exception.PermissionDeniedException;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
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

	@Override
	public void delete(Serializable id) {
		SystemContext context = SystemContextHolder.get();
		boolean isManager = context.hasAnyRole("BC_HISTORY_REPORT_DELETE");
		if(isManager) {
			super.delete(id);
		}else {
			ReportHistory h = load(id);
			if(h == null) throw new NotExistsException("id=" + id);
			if(!h.getAuthor().getId().equals(context.getUserHistory().getId())){
				throw new PermissionDeniedException("删除失败，只能删除自己创建的历史报表！");
			}
			super.delete(id);
		}
	}

	@Override
	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0) return;
		for (Serializable id : ids) {
			this.delete(id);
		}
	}
}
