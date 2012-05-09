package cn.bc.report.dao;

import java.util.List;
import java.util.Map;

import cn.bc.core.dao.CrudDao;
import cn.bc.report.domain.ReportHistory;

/**
 * 历史报表Dao接口
 * 
 * @author lbj
 * 
 */
public interface ReportHistoryDao extends CrudDao<ReportHistory> {
	/**
	 * 查找所属分类选项
	 * 
	 * @return
	 */
	public List<Map<String,String>> findCategoryOption();
}
