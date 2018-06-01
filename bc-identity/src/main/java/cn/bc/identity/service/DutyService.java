package cn.bc.identity.service;

import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.Duty;

import java.util.Map;

/**
 * 职务 Service
 *
 * @author dragon 2016-07-19
 */
public interface DutyService extends CrudService<Duty> {
  /**
   * 获取视图分页数据
   *
   * @param pageNo   页码
   * @param pageSize 页容量
   * @param search   过滤的内容
   * @return
   */
  Map<String, Object> data(int pageNo, int pageSize, String search);
}
