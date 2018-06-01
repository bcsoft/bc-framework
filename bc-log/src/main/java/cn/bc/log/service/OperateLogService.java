package cn.bc.log.service;

import cn.bc.core.service.CrudService;
import cn.bc.log.domain.AuditItem;
import cn.bc.log.domain.OperateLog;

import java.util.List;
import java.util.Set;

/**
 * 操作日志Service接口
 *
 * @author dragon
 */
public interface OperateLogService extends CrudService<OperateLog> {
  /**
   * 获取指定文档的工作日志
   *
   * @param ptype 文档类型
   * @param pid   文档标识号
   * @return
   */
  List<OperateLog> find(String ptype, String pid);

  /**
   * 插入一条系统自动生成的工作日志信息
   *
   * @param ptype   所属模块，不能为空，如User、Role，一般为类名
   * @param pid     文档标识号，不能为空
   * @param subject 标题，不能为空
   * @param content 详细内容
   * @param operate 操作分类，如create、update、delete等
   * @return 插入成功后的工作日志信息
   */
  OperateLog saveWorkLog(String ptype, String pid, String subject,
                         String content, String operate);

  /**
   * 插入一条工作日志信息
   *
   * @param ptype   所属模块，不能为空，如User、Role，一般为类名
   * @param pid     文档标识号，不能为空
   * @param subject 标题，不能为空
   * @param content 详细内容
   * @param operate 操作分类，如create、update、delete等
   * @param way     创建方式
   * @return 插入成功后的工作日志信息
   */
  OperateLog saveWorkLog(String ptype, String pid, String subject,
                         String content, String operate, int way);

  /**
   * 插入一条审计日志信息
   *
   * @param ptype      所属模块，不能为空，如User、Role，一般为类名
   * @param pid        文档标识号，不能为空
   * @param auditItems 审计条目列表
   * @param subject    标题，不能为空
   * @param content    详细内容
   * @param operate    操作分类，如create、update、delete等
   * @return 插入成功后的审计日志信息
   */
  OperateLog saveAuditLog(String ptype, String pid,
                          Set<AuditItem> auditItems, String subject, String content,
                          String operate);
}
