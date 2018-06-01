package cn.bc.identity.service;

import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.Resource;

import java.util.List;
import java.util.Map;

/**
 * 资源Service接口
 *
 * @author dragon
 */
public interface ResourceService extends CrudService<Resource> {
  /**
   * 获取指定类型和状态的Resource信息
   *
   * @param types   类型列表
   * @param statues 状态列表
   * @return 返回结果中的元素Map格式为：
   * <ul>
   * <li>id -- Resource的id</li>
   * <li>type -- Resource的type</li>
   * <li>name -- Resource的name</li>
   * <li>pname -- Resource的pname</li>
   * </ul>
   */
  List<Map<String, String>> find4option(Integer[] types, Integer[] statues);

  /**
   * @return
   */
  Map<Long, Resource> findAll();
}
