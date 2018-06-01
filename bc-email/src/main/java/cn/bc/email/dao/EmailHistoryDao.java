package cn.bc.email.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.email.domain.EmailHistory;

/**
 * 邮件查看历史Dao接口
 *
 * @author lbj
 */
public interface EmailHistoryDao extends CrudDao<EmailHistory> {

  /**
   * 获取邮件接收人查阅邮件的次数
   *
   * @param emailId
   * @param receiverCode
   * @return
   */
  int getEmailReceiverReadCount(Long emailId, String receiverCode);
}