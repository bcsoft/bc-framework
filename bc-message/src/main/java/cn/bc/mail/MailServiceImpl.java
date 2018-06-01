package cn.bc.mail;

import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.option.service.OptionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件Service接口的默认实现
 *
 * @author dragon
 */
public class MailServiceImpl implements MailService {
  private static Log logger = LogFactory.getLog(MailServiceImpl.class);
  private boolean async = false;// 是否异步发送邮件：默认同步，设为true异步发送
  private OperateLogService operateLogService;
  private IdGeneratorService idGeneratorService;
  private OptionService optionService;
  private JavaMailSender mailSender;
  private SimpleMailMessage templateMessage;
  private TaskExecutor taskExecutor;// Spring异步执行器
  private Map<String, String> mailConfig;// 邮件配置

  public void setTaskExecutor(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  public void setOperateLogService(OperateLogService operateLogService) {
    this.operateLogService = operateLogService;
  }

  public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
    this.idGeneratorService = idGeneratorService;
  }

  public void setMailSender(JavaMailSender mailSender) {
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
      this.sendBySynchronousMode(mail, SystemContextHolder.get()
        .getUserHistory());
    }
  }

  // 同步发送邮件
  private void sendBySynchronousMode(Mail mail,
                                     ActorHistory currentUserHistory) throws MailException {
    // 构建邮件消息
    MailMessage msg = createMailMessage(mail);

    // 根据选项配置初始化邮件发送器
    this.initMailSender();

    try {
      // 发送邮件
      if (msg instanceof SimpleMailMessage) {
        this.mailSender.send((SimpleMailMessage) msg);
      } else if (msg instanceof MimeMailMessage) {
        this.mailSender.send(((MimeMailMessage) msg).getMimeMessage());
      }

      // 记录发送成功日志
      String subject = "发送邮件成功：主送"
        + StringUtils.arrayToCommaDelimitedString(mail.getTo());
      if (mail.getCc() != null && mail.getCc().length > 0) {
        subject += "(抄送"
          + StringUtils.arrayToCommaDelimitedString(mail.getCc())
          + ")";
      }
      if (mail.getBcc() != null && mail.getBcc().length > 0) {
        subject += "(密送"
          + StringUtils
          .arrayToCommaDelimitedString(mail.getBcc())
          + ")";
      }
      String content = "========邮件标题========\r\n" + mail.getSubject();
      content += "\r\n\r\n========邮件内容========\r\n" + mail.getContent();
      this.saveWorkLog(currentUserHistory, subject, content);
    } catch (Exception e) {
      // 记录发送异常日志
      String subject = "发送邮件失败：主送"
        + StringUtils.arrayToCommaDelimitedString(mail.getTo());
      if (mail.getCc() != null && mail.getCc().length > 0) {
        subject += "(抄送"
          + StringUtils.arrayToCommaDelimitedString(mail.getCc())
          + ")";
      }
      if (mail.getBcc() != null && mail.getBcc().length > 0) {
        subject += "(密送"
          + StringUtils.arrayToCommaDelimitedString(mail.getCc())
          + ")";
      }
      String content = "========邮件标题========\r\n" + mail.getSubject();
      content += "\r\n\r\n========邮件内容========\r\n" + mail.getContent();
      StringWriter sw = new StringWriter();
      PrintWriter s = new PrintWriter(sw);
      e.printStackTrace(s);
      content += "\r\n\r\n========异常信息========\r\n" + sw.toString();
      this.saveWorkLog(currentUserHistory, subject, content);
    }
  }

  private MailMessage createMailMessage(Mail mail) throws MailException {
    if (mail.isHtml()) {
      return createMimeMailMessage(mail);
    } else {
      return createSimpleMailMessage(mail);
    }
  }

  // 创建html邮件
  private MimeMailMessage createMimeMailMessage(Mail mail)
    throws MailException {
    MimeMessage javaMailMessage = mailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(javaMailMessage);
    MimeMailMessage msg = new MimeMailMessage(messageHelper);

    if (mailConfig != null) {
      if (mailConfig.containsKey("username")) {
        msg.setFrom(mailConfig.get("username"));// 邮件发送人
      }
      if (mailConfig.containsKey("subject")) {// 默认邮件标题
        msg.setSubject(mailConfig.get("subject"));
      }
    }

    msg.setSubject(mail.getSubject()); // 标题
    try {
      messageHelper.setText(mail.getContent(), true);// html 内容
    } catch (MessagingException e) {
      throw new MailParseException(e.getMessage(), e);
    }

    msg.setTo(mail.getTo()); // 主送
    if (mail.getCc() != null && mail.getCc().length > 0)
      msg.setCc(mail.getCc()); // 抄送
    if (mail.getBcc() != null && mail.getBcc().length > 0)
      msg.setBcc(mail.getBcc()); // 密送
    return msg;
  }

  // 创建纯文本邮件
  private SimpleMailMessage createSimpleMailMessage(Mail mail) {
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
    if (mail.getCc() != null && mail.getCc().length > 0)
      msg.setCc(mail.getCc()); // 抄送
    if (mail.getBcc() != null && mail.getBcc().length > 0)
      msg.setBcc(mail.getBcc()); // 密送
    return msg;
  }

  private OperateLog saveWorkLog(ActorHistory author, String subject,
                                 String content) {
    OperateLog worklog = new OperateLog();
    worklog.setPtype("SendMail");
    worklog.setPid("0");
    worklog.setType(OperateLog.TYPE_WORK);// 工作日志
    worklog.setWay(OperateLog.WAY_SYSTEM);
    worklog.setOperate(OperateLog.OPERATE_CREATE);
    worklog.setFileDate(Calendar.getInstance());
    worklog.setAuthor(author);
    worklog.setSubject(subject);
    worklog.setContent(content);
    worklog.setUid(this.idGeneratorService.next("WorkLog"));

    return operateLogService.save(worklog);
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
          ps.setProperty(key, String.valueOf(json.get(key)));
        }
      } catch (JSONException e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  // 异步发送邮件
  private void sendByAsynchronousMode(final Mail mail) {
    final ActorHistory currentUserHistory = SystemContextHolder.get()
      .getUserHistory();
    taskExecutor.execute(new Runnable() {
      public void run() {
        try {
          sendBySynchronousMode(mail, currentUserHistory);
        } catch (Exception e) {
          logger.warn(e);
        }
      }
    });
  }
}
