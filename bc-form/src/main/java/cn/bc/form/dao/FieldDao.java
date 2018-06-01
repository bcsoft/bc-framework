package cn.bc.form.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.form.domain.Field;

import java.util.List;

/**
 * 表单字段Dao
 *
 * @author hwx
 */

public interface FieldDao extends CrudDao<Field> {
  /**
   * 查找指定表单的所有字段信息
   *
   * @param pid 其他模块调用此模块时，该模块记录的id
   * @return
   */
  List<Field> findByParent(Long pid);
}
