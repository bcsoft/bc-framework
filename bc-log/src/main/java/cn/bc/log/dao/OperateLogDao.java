package cn.bc.log.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.log.domain.OperateLog;

import java.util.List;

/**
 * 操作日志Dao接口
 *
 * @author dragon
 */
public interface OperateLogDao extends CrudDao<OperateLog> {
  /**
   * 获取指定文档的操作日志
   *
   * @param ptype 文档类型
   * @param pid   文档标识号
   * @return
   */
  List<OperateLog> find(String ptype, String pid);
}
