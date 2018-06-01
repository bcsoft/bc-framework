package cn.bc.report.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.report.dao.ReportTemplateDao;
import cn.bc.report.domain.ReportTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 报表模板Service接口的实现
 *
 * @author dragon
 */
public class ReportTemplateServiceImpl extends
  DefaultCrudService<ReportTemplate> implements ReportTemplateService {
  private ReportTemplateDao reportTemplateDao;

  @Autowired
  public void setReportTemplateDao(ReportTemplateDao reportTemplateDao) {
    this.reportTemplateDao = reportTemplateDao;
    this.setCrudDao(reportTemplateDao);
  }

  public ReportTemplate loadByCode(String code) {
    return this.reportTemplateDao.loadByCode(code);
  }

  public boolean isUniqueCode(Long currentId, String code) {
    return this.reportTemplateDao.isUniqueCode(currentId, code);
  }
}
