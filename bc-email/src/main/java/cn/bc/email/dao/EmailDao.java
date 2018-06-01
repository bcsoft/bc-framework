package cn.bc.email.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.email.domain.Email;

/**
 * 邮件Dao接口
 *
 * @author dragon
 */
public interface EmailDao extends CrudDao<Email> {

  /**
   * 根据邮件id和接收人id 修改邮件的查阅状态
   *
   * @param ids
   * @param receiver_id
   * @param read
   */
  void mark(Long[] ids, Long receiver_id, boolean read);

  /**
   * 根据接收人id 将未读的邮件全部标记为已读
   *
   * @param receiver_id
   */
  void mark4read(Long receiver_id);
}
