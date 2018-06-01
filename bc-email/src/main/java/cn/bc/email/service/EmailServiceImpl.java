package cn.bc.email.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.email.dao.EmailDao;
import cn.bc.email.domain.Email;
import cn.bc.email.domain.EmailTo;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 邮件service接口的实现
 *
 * @author dragon
 */
public class EmailServiceImpl extends DefaultCrudService<Email> implements
  EmailService {
  private EmailDao emailDao;
  private IdGeneratorService idGeneratorService;
  private ActorService actorService;

  @Autowired
  public void setEmailDao(EmailDao emailDao) {
    this.setCrudDao(emailDao);
    this.emailDao = emailDao;
  }

  @Autowired
  public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
    this.idGeneratorService = idGeneratorService;
  }

  @Autowired
  public void setActorService(ActorService actorService) {
    this.actorService = actorService;
  }

  public void mark(Long[] ids, Long receiver_id, boolean read) {
    this.emailDao.mark(ids, receiver_id, read);
  }

  public void mark4read(Long receiver_id) {
    this.emailDao.mark4read(receiver_id);
  }

  public void doSend(Actor sender, Actor receiver, String subject, String content) {
    Email email = new Email();
    email.setSubject(subject);
    email.setContent(content);
    email.setFileDate(Calendar.getInstance());
    email.setSendDate(email.getFileDate());
    email.setSender(sender);
    email.setStatus(Email.STATUS_SENDED);
    email.setUid(this.idGeneratorService.next(Email.class.getSimpleName()));
    email.setType(Email.TYPE_NEW);

    Set<EmailTo> tos = new HashSet<EmailTo>();
    EmailTo emailTo;
    if (receiver.getType() == Actor.TYPE_USER) {// 发送到人
      emailTo = new EmailTo();
      emailTo.setType(EmailTo.TYPE_TO);// 主送
      emailTo.setEmail(email);
      emailTo.setOrderNo(0);
      emailTo.setReceiver(receiver);
      tos.add(emailTo);
    } else if (receiver.getType() == Actor.TYPE_GROUP || receiver.getType() == Actor.TYPE_DEPARTMENT || receiver.getType() == Actor.TYPE_UNIT) {// 发送到岗位、部门、单位
      List<Actor> followers = this.actorService.findFollower(receiver.getId(), new Integer[]{ActorRelation.TYPE_BELONG}, new Integer[]{Actor.TYPE_USER});
      int i = 0;
      for (Actor f : followers) {
        emailTo = new EmailTo();
        emailTo.setType(EmailTo.TYPE_TO);// 主送
        emailTo.setEmail(email);
        emailTo.setOrderNo(i++);
        emailTo.setUpper(receiver);
        emailTo.setReceiver(f);
        tos.add(emailTo);
      }
    }
    email.setTos(tos);

    this.save(email);
  }
}
