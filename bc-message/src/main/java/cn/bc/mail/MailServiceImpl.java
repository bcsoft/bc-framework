package cn.bc.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * 邮件Service接口的默认实现
 * 
 * @author dragon
 * 
 */
public class MailServiceImpl implements MailService {
	private static Log logger = LogFactory.getLog(MailServiceImpl.class);
	private boolean async = false;// 是否异步发送邮件：默认同步，设为true异步发送
	private MailSender mailSender;
	private SimpleMailMessage templateMessage;
	private TaskExecutor taskExecutor;// Spring异步执行器

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public void send(Mail mail) throws MailException {
		if (mail == null) {
			return;
		}
		if (mail.getTo() == null || mail.getTo().length == 0) {
			throw new MailSendException("mail.to couldn't be null");
		}

		if (async) { // 异步发送
			this.sendByAsynchronousMode(mail);
		} else { // 同步发送
			this.sendBySynchronousMode(mail);
		}
	}

	// 同步发送邮件
	private void sendBySynchronousMode(Mail mail) throws MailException {
		// 复制一个线程安全的邮件消息
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setSubject(mail.getSubject()); // 标题
		msg.setText(mail.getContent()); // 内容

		msg.setTo(mail.getTo()); // 主送
		msg.setCc(mail.getCc()); // 抄送
		msg.setBcc(mail.getBcc()); // 密送

		// 发送邮件
		this.mailSender.send(msg);
	}

	// 异步发送邮件
	private void sendByAsynchronousMode(final Mail mail) {
		taskExecutor.execute(new Runnable() {
			public void run() {
				try {
					sendBySynchronousMode(mail);
				} catch (Exception e) {
					logger.warn(e);
				}
			}
		});
	}
}
