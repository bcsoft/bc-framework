package cn.bc.report.service;

import cn.bc.core.service.CrudService;
import cn.bc.report.domain.ReportHistory;

import java.util.List;
import java.util.Map;

/**
 * 历史报表Service接口
 *
 * @author lbj
 */
public interface ReportHistoryService extends CrudService<ReportHistory> {
  /**
   * 查找所属分类选项
   *
   * @return
   */
  public List<Map<String, String>> findCategoryOption();

  /**
   * 查找来源选项
   */
  public List<Map<String, String>> findSourceOption();
}
