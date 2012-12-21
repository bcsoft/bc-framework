package cn.bc.mail;

import org.springframework.mail.MailException;

/**
 * 邮件Service接口
 * 
 * @author dragon
 * 
 */
public interface MailService {
	/**
	 * 发送邮件
	 * 
	 * @param subject
	 *            邮件标题
	 * @param content
	 *            邮件内容
	 * @param to
	 *            主送人
	 * @throws MailException
	 */
	void send(String subject, String content, String to) throws MailException;
}
