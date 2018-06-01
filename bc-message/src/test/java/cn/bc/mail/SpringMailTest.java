package cn.bc.mail;

import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.util.Properties;

public class SpringMailTest {
  @Test
  public void sendBy139() {
    // config
    String fromAccount = "xxx@139.com";
    String fromPassword = "***";
    String toAccount = "xxx@163.com";

    // create mail sender
    JavaMailSender mailSender = createJavaMailSender(fromAccount, fromPassword, "smtp.139.com");

    // send
    mailSender.send(createSimpleMailMessage(fromAccount, toAccount));
    //mailSender.send(createMimeMessage(fromAccount, toAccount));
  }

  @Test
  public void sendBy163() {
    // 对于 163 邮箱，需要使用客户端授权密码（在设置中设置）而不是账号的登陆密码

    // config
    String fromAccount = "xxx@163.com";
    String fromPassword = "***";
    String toAccount = "xxx@139.com";

    // create mail sender
    JavaMailSender mailSender = createJavaMailSender(fromAccount, fromPassword, "smtp.163.com");

    // send
    //mailSender.send(createSimpleMailMessage(fromAccount, toAccount));
    mailSender.send(createMimeMessage(fromAccount, toAccount));
  }

  private JavaMailSender createJavaMailSender(String account, String password, String host) {
    // mail sender
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(25);
    mailSender.setDefaultEncoding("UTF-8");
    mailSender.setUsername(account);
    mailSender.setPassword(password);
    Properties javaMailProperties = new Properties();
    javaMailProperties.put("mail.smtp.auth", "true");
    javaMailProperties.put("mail.smtp.timeout", "2500");
    javaMailProperties.put("mail.transport.protocol", "smtp");
    javaMailProperties.put("mail.smtp.starttls.enable", "true");
    mailSender.setJavaMailProperties(javaMailProperties);
    return mailSender;
  }

  private SimpleMailMessage createSimpleMailMessage(String fromAccount, String toAccount) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(fromAccount);
    msg.setTo(toAccount);
    String ts = LocalDateTime.now().toString();
    msg.setSubject("bc-order-" + ts);
    msg.setText("Dear Customer, your order time is " + ts);
    return msg;
  }

  private MimeMessagePreparator createMimeMessage(String fromAccount, String toAccount) {
    return mimeMessage -> {
      String ts = LocalDateTime.now().toString();
      mimeMessage.setFrom(new InternetAddress(fromAccount));
      mimeMessage.setSubject("bc-order-" + ts);                                           // 标题
      mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(toAccount)); // 主送
      String content = "Dear Customer, your order time is <strong>" + ts + "</strong>";
      mimeMessage.setText(content, "UTF-8", "html");    // 内容
    };
  }
}