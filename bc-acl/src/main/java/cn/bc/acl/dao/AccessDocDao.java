package cn.bc.acl.dao;

import cn.bc.acl.domain.AccessDoc;
import cn.bc.core.dao.CrudDao;

/**
 * 访问对象Dao接口
 *
 * @author lbj
 */
public interface AccessDocDao extends CrudDao<AccessDoc> {

  /**
   * 获取访问对象
   *
   * @param docId   文档标识
   * @param docType 文档类型
   * @return
   */
  AccessDoc load(String docId, String docType);


}
