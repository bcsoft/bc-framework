package cn.bc.form.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.form.domain.Form;

/**
 * 表单Dao
 *
 * @author hwx
 */

public interface FormDao extends CrudDao<Form> {
  /**
   * 删除指定的表单
   *
   * @param type 类型
   * @param code 编码
   * @param pid  业务ID
   * @param ver  版本号
   */
  void delete(String type, String code, Long pid, Float ver);

  /**
   * 获取当前的最新版本号
   *
   * @param type
   * @param code
   * @param pid
   * @return
   */
  Float getNewestVer(String type, String code, Long pid);
}
