package cn.bc.identity.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.identity.domain.Resource;

import java.util.List;
import java.util.Map;

/**
 * 资源Dao接口
 *
 * @author dragon
 */
public interface ResourceDao extends CrudDao<Resource> {
  /**
   * 获取指定类型和状态的Role信息
   *
   * @param types   类型列表
   * @param statues 状态列表
   * @return 返回结果中的元素Map格式为：
   * <ul>
   * <li>id -- Resource的id</li>
   * <li>type -- Resource的type</li>
   * <li>name -- Resource的name</li>
   * <li>pname -- Resource的pname</li>
   * </ul>o
   */
  List<Map<String, String>> find4option(Integer[] types, Integer[] statues);
}
