package cn.bc.report.service;

import cn.bc.core.service.CrudService;
import cn.bc.report.domain.ReportTemplate;

/**
 * 报表模板Service接口
 * 
 * @author dragon
 * 
 */
public interface ReportTemplateService extends CrudService<ReportTemplate> {
	/**
	 * 获取指定编码的报表模板对象
	 * 
	 * @param code
	 * @return 指定编码的报表模板对象
	 */
	public ReportTemplate loadByCode(String code);
}
