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

	/**
	 * 检测编码是否唯一
	 * 
	 * @param currentId
	 * @param code
	 * @return
	 */
	public boolean isUniqueCode(Long currentId, String code);
}
