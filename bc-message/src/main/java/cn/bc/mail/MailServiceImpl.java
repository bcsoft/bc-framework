package cn.bc.mail;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * 邮件Service接口的默认实现
 * 
 * @author dragon
 * 
 */
public class MailServiceImpl implements MailService {
	// private static Log logger = LogFactory.getLog(MailServiceImpl.class);
	private boolean async = false;// 是否异步发送邮件：默认同步，设为true异步发送
	private MailSender mailSender;
	private SimpleMailMessage templateMessage;

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void send(final String subject, final String content, final String to)
			throws MailException {
		if (async) { // 异步发送
			Runnable thread = new Runnable() {
				public void run() {
					// 复制一个线程安全的邮件消息
					SimpleMailMessage msg = new SimpleMailMessage(
							templateMessage);

					msg.setTo(to); // 发送人
					msg.setSubject(subject); // 标题
					msg.setText(content); // 内容

					// 发送邮件
					mailSender.send(msg);
				}
			};
			new Thread(thread).start();
		} else { // 同步发送
			// 复制一个线程安全的邮件消息
			SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

			msg.setTo(to); // 发送人
			msg.setSubject(subject); // 标题
			msg.setText(content); // 内容

			// 发送邮件
			this.mailSender.send(msg);
		}
	}
}
