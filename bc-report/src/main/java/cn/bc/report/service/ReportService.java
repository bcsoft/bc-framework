/**
 *
 */
package cn.bc.report.service;

import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.report.domain.ReportHistory;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.template.domain.Template;

import java.util.Map;

/**
 * 报表服务
 *
 * @author dragon
 */
public interface ReportService {
  /**
   * 创建指定SqlObject的查询对象
   *
   * @param queryType 报表查询类型：jpa 为JPA查询，jdbc 为 jdbc 查询，默认为jpa
   */
  Query<Map<String, Object>> createSqlQuery(String queryType, SqlObject<Map<String, Object>> sqlObject);

  /**
   * 加载指定编码的模板
   *
   * @param templateCode 模板的编码
   */
  Template loadTemplate(String templateCode);

  /**
   * 加载指定编码的报表模板
   *
   * @param reportTemplateCode 模板的编码
   */
  ReportTemplate loadReportTemplate(String reportTemplateCode);

  /**
   * 按既定的条件执行指定的报表模板到历史报表
   *
   * @param reportTemplateCode 报表模板的编码
   * @param condition          既定的条件
   */
  void runReportTemplate2history(String reportTemplateCode,
                                 Condition condition) throws Exception;

  /**
   * 保存历史报表
   *
   * @param reportHistory
   */
  void saveReportHistory(ReportHistory reportHistory);
}
