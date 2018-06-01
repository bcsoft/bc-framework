package cn.bc.email.service;

import cn.bc.core.service.CrudService;
import cn.bc.email.domain.EmailHistory;

/**
 * 邮件查看历史Service接口
 *
 * @author lbj
 */
public interface EmailHistoryService extends CrudService<EmailHistory> {

  /**
   * 获取邮件接收人查阅邮件的次数
   *
   * @param emailId
   * @param receiverCode
   * @return
   */
  int getEmailReceiverReadCount(Long emailId, String receiverCode);
}