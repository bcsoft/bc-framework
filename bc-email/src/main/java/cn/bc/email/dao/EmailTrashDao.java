package cn.bc.email.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.email.domain.EmailTrash;

import java.util.List;

/**
 * 邮件垃圾箱Dao接口
 *
 * @author lbj
 */
public interface EmailTrashDao extends CrudDao<EmailTrash> {

  /**
   * 根据操作人id 查找可还原的垃圾邮件
   *
   * @param ownerId
   * @return
   */
  List<EmailTrash> find4resumableByOwnerId(Long ownerId);

}
