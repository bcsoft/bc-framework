package cn.bc.mail;

import org.springframework.mail.MailException;

/**
 * 邮件Service接口
 *
 * @author dragon
 */
public interface MailService {
  /**
   * 发送邮件
   *
   * @param mail 邮件
   * @throws MailException
   */
  void send(Mail mail) throws MailException;
}
