package cn.bc.report.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.report.domain.ReportTemplate;

/**
 * 报表模板Dao接口
 * 
 * @author dragon
 * 
 */
public interface ReportTemplateDao extends CrudDao<ReportTemplate> {
	/**
	 * 获取指定编码的报表模板对象
	 * 
	 * @param code
	 * @return 指定编码的报表模板对象
	 */
	public ReportTemplate loadByCode(String code);
}
