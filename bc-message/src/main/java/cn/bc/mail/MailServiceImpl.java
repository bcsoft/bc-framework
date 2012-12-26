package cn.bc.mail;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import cn.bc.option.service.OptionService;

/**
 * 邮件Service接口的默认实现
 * 
 * @author dragon
 * 
 */
public class MailServiceImpl implements MailService {
	private static Log logger = LogFactory.getLog(MailServiceImpl.class);
	private boolean async = false;// 是否异步发送邮件：默认同步，设为true异步发送
	private OptionService optionService;
	private MailSender mailSender;
	private SimpleMailMessage templateMessage;
	private TaskExecutor taskExecutor;// Spring异步执行器
	private Map<String, String> mailConfig;// 邮件配置

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
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

		// 获取邮件发送配置
		mailConfig = this.optionService
				.findActiveOptionItemByGroupKey("bc.mailSender");

		if (mailConfig != null) {
			if (mailConfig.containsKey("async")) {
				this.setAsync(Boolean.parseBoolean(mailConfig.get("async")));// 异步发送配置
			}
		}

		// log
		if (logger.isDebugEnabled()) {
			logger.debug("subject=" + mail.getSubject());
			logger.debug("content=" + mail.getContent());
			logger.debug("to="
					+ StringUtils.arrayToCommaDelimitedString(mail.getTo()));
			logger.debug("cc="
					+ StringUtils.arrayToCommaDelimitedString(mail.getCc()));
			logger.debug("bcc="
					+ StringUtils.arrayToCommaDelimitedString(mail.getBcc()));
		}

		if (this.async) { // 异步发送
			this.sendByAsynchronousMode(mail);
		} else { // 同步发送
			this.sendBySynchronousMode(mail);
		}
	}

	// 同步发送邮件
	private void sendBySynchronousMode(Mail mail) throws MailException {
		// 复制一个线程安全的邮件消息
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
		if (mailConfig != null) {
			if (mailConfig.containsKey("username")) {
				msg.setFrom(mailConfig.get("username"));// 邮件发送人
			}
			if (mailConfig.containsKey("subject")) {// 默认邮件标题
				msg.setSubject(mailConfig.get("subject"));
			}
		}

		msg.setSubject(mail.getSubject()); // 标题
		msg.setText(mail.getContent()); // 内容

		msg.setTo(mail.getTo()); // 主送
		msg.setCc(mail.getCc()); // 抄送
		msg.setBcc(mail.getBcc()); // 密送

		// 根据选项配置初始化邮件发送器
		this.initMailSender();

		// 发送邮件
		this.mailSender.send(msg);
	}

	private void initMailSender() {
		if (!(mailSender instanceof JavaMailSenderImpl) || mailConfig == null)
			return;

		// 按配置设置
		JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl) mailSender;
		if (mailConfig.containsKey("host"))
			javaMailSender.setHost(mailConfig.get("host"));
		if (mailConfig.containsKey("port"))
			javaMailSender.setPort(Integer.parseInt(mailConfig.get("port")));
		if (mailConfig.containsKey("defaultEncoding"))
			javaMailSender
					.setDefaultEncoding(mailConfig.get("defaultEncoding"));
		if (mailConfig.containsKey("username"))
			javaMailSender.setUsername(mailConfig.get("username"));
		if (mailConfig.containsKey("password"))
			javaMailSender.setPassword(mailConfig.get("password"));
		if (mailConfig.containsKey("javaMailProperties")) {
			try {
				JSONObject json = new JSONObject(
						mailConfig.get("javaMailProperties"));
				Properties ps = javaMailSender.getJavaMailProperties();
				@SuppressWarnings("unchecked")
				Iterator<String> itor = json.keys();
				String key;
				while (itor.hasNext()) {
					key = itor.next();
					ps.setProperty(key, json.getString(key));
				}
			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
			}
		}
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
