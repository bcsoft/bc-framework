package cn.bc.report.service;

import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.db.jdbc.spring.SpringJdbcQuery;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.orm.jpa.JpaNativeQuery;
import cn.bc.report.domain.ReportHistory;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 报表任务Service接口的实现
 *
 * @author lbj
 */
public class ReportServiceImpl implements ReportService, BeanFactoryAware {
	@Autowired
	protected DataSource dataSource;
	@PersistenceContext
	protected EntityManager entityManager;
	private TemplateService templateService;
	private ReportTemplateService reportTemplateService;
	private ReportHistoryService reportHistoryService;
	private BeanResolver beanResolver;

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	@Autowired
	public void setReportTemplateService(ReportTemplateService reportTemplateService) {
		this.reportTemplateService = reportTemplateService;
	}

	@Autowired
	public void setReportHistoryService(ReportHistoryService reportHistoryService) {
		this.reportHistoryService = reportHistoryService;
	}

	@Autowired
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanResolver = new BeanFactoryResolver(beanFactory);
	}

	public Query<Map<String, Object>> createSqlQuery(String queryType, SqlObject<Map<String, Object>> sqlObject) {
		if ("jdbc".equalsIgnoreCase(queryType)) {
			SpringJdbcQuery<Map<String, Object>> springJdbcQuery = new SpringJdbcQuery<>(dataSource, new ColumnMapRowMapper());
			springJdbcQuery.setSql(sqlObject.getNativeSql());
			springJdbcQuery.setSqlArgs(sqlObject.getArgs());
			return springJdbcQuery;
		} else {
			return new JpaNativeQuery<>(entityManager, sqlObject);
		}
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
		ReportHistory h = tpl.run2history(this.beanResolver, this, condition);
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
