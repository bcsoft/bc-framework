package cn.bc.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class MailServiceImplTest {
	MailService mailService;

	@Autowired
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	@Test
	public void testSend() {
		Mail mail = new Mail();
		mail.setSubject("[BCMail:测试标题]");
		mail.setContent("[BCMail:测试内容]\r\nreturn 换行测试！");
		mail.setTo(new String[] { "rongjih@163.com", "rongjihuang@gmail.com" });
		this.mailService.send(mail);
	}
}
