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
		String subject = "[BC: mail test]";
		String content = "[BC:Content]";
		String to = "rongjih@163.com";

		this.mailService.send(subject, content, to);
	}
}
