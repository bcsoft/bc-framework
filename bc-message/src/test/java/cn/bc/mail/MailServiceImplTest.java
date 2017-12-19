package cn.bc.mail;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.identity.web.SystemContextImpl;
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
	public ActorService actorService;
	@Autowired
	public ActorHistoryService actorHistoryService;

	@Autowired
	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	private void doSetUser() {
		Actor a = actorService.loadByCode("may");
		ActorHistory ah = actorHistoryService.loadByCode("may");
		SystemContext context = new SystemContextImpl();
		context.setAttr(SystemContext.KEY_USER, a);
		context.setAttr(SystemContext.KEY_USER_HISTORY, ah);
		SystemContextHolder.set(context);
	}

	@Test
	public void testSend() {
		doSetUser();
		Mail mail = new Mail();
		mail.setSubject("[BCMail:会议提醒]");
		mail.setContent("[BCMail:提醒内容]\r\n请出准时出席周五的会议！");
		mail.setHtml(true);
		mail.setTo(new String[]{"1287635921@qq.com"});
		this.mailService.send(mail);
	}
}
