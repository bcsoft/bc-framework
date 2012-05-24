package cn.bc.report.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.Assert;

import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.report.domain.ReportHistory;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;

/**
 * 报表任务Service接口的实现
 * 
 * @author lbj
 * 
 */
public class ReportServiceImpl implements ReportService {
	private JpaTemplate jpaTemplate;
	private TemplateService templateService;
	private ReportTemplateService reportTemplateService;
	private ReportHistoryService reportHistoryService;

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	@Autowired
	public void setReportTemplateService(
			ReportTemplateService reportTemplateService) {
		this.reportTemplateService = reportTemplateService;
	}

	@Autowired
	public void setReportHistoryService(
			ReportHistoryService reportHistoryService) {
		this.reportHistoryService = reportHistoryService;
	}

	public Query<Map<String, Object>> createSqlQuery(
			SqlObject<Map<String, Object>> sqlObject) {
		return new HibernateJpaNativeQuery<Map<String, Object>>(jpaTemplate,
				sqlObject);
	}

	public Template loadTemplate(String templateCode) {
		return this.templateService.loadByCode(templateCode);
	}

	public ReportTemplate loadReportTemplate(String reportTemplateCode) {
		return this.reportTemplateService.loadByCode(reportTemplateCode);
	}

	public void runReportTemplate2history(String code, Condition condition)
			throws Exception {
		// 加载报表模板
		ReportTemplate tpl = this.loadReportTemplate(code);
		Assert.notNull(tpl, "指定的报表模板不存在:code=" + code);

		// 执行并生成历史报表
		ReportHistory h = tpl.run2history(this, condition);
		h.setSourceType("用户生成");
		h.setSourceId(tpl.getId());
		h.setAuthor(SystemContextHolder.get().getUserHistory());

		// 保存历史报表
		this.reportHistoryService.save(h);
	}

	public void saveReportHistory(ReportHistory reportHistory) {
		this.reportHistoryService.save(reportHistory);
	}
}
