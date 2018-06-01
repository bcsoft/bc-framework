package cn.bc.email.web.struts2;

import cn.bc.core.exception.CoreException;
import cn.bc.email.domain.EmailTrash;
import cn.bc.email.service.EmailService;
import cn.bc.email.service.EmailTrashService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 垃圾邮件Action
 *
 * @author lbj
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class EmailTrashAction extends EntityAction<Long, EmailTrash> {
  private static final long serialVersionUID = 1L;
  public Long emailId;
  public String emailIds;
  public Integer source;//来源 1-发件箱，2-收件箱
  public Integer status;//状态 0-可恢复的删除 1-不可恢复的删除

  private EmailTrashService emailTrashService;
  private EmailService emailService;

  @Autowired
  public void setEmailService(EmailService emailService) {
    this.emailService = emailService;
  }

  @Autowired
  public void setEmailTrashService(EmailTrashService emailTrashService) {
    this.emailTrashService = emailTrashService;
    this.setCrudService(emailTrashService);
  }

  //移动到垃圾箱或彻底删除
  public String deleteEmail() throws Exception {
    Json json = new Json();
    if (this.source == null) throw new CoreException("must set property source!");
    if (this.status == null) throw new CoreException("must set property status!");

    SystemContext context = (SystemContext) this.getContext();
    EmailTrash entity;

    if (this.emailId != null) {//操作一个邮件
      entity = this.createEntity();
      entity.setEmail(this.emailService.load(this.emailId));
      entity.setSource(this.source);
      entity.setStatus(this.status);
      entity.setHandleDate(Calendar.getInstance());
      entity.setOwner(context.getUser());
      this.emailTrashService.save(entity);
    } else {
      if (this.emailIds != null && this.emailIds.length() > 0) {//操作一批邮件
        Long[] emailIds = cn.bc.core.util.StringUtils
          .stringArray2LongArray(this.emailIds.split(","));
        Set<EmailTrash> ets = new HashSet<EmailTrash>();
        for (Long emailId : emailIds) {
          entity = this.createEntity();
          entity.setEmail(this.emailService.load(emailId));
          entity.setSource(this.source);
          entity.setStatus(this.status);
          entity.setHandleDate(Calendar.getInstance());
          entity.setOwner(context.getUser());
          ets.add(entity);
        }
        this.emailTrashService.save(ets);
      } else {
        throw new CoreException("must set property emailId or emailIds!");
      }
    }

    json.put("success", true);
    if (this.status.equals(EmailTrash.STATUS_RESUMABLE)) {
      json.put("msg", getText("emailTrash.msg.move"));
    } else {
      json.put("msg", getText("emailTrash.msg.delete"));
    }
    this.json = json.toString();
    return "json";
  }


  @Override
  public String delete() throws Exception {
    if (this.status == null) throw new CoreException("must set property status!");

    Json json = new Json();
    if (this.status.equals(EmailTrash.STATUS_RESUMABLE)) {//可恢复时操作
      return super.delete();
    } else {//删除时操作
      SystemContext context = (SystemContext) this.getContext();
      EmailTrash entity;

      if (this.getId() != null) {// 操作一个邮件
        entity = this.emailTrashService.load(this.getId());
        entity.setStatus(this.status);
        entity.setOwner(context.getUser());
        entity.setHandleDate(Calendar.getInstance());
        this.emailTrashService.save(entity);
      } else {// 操作一批邮件
        if (this.getIds() != null && this.getIds().length() > 0) {
          Long[] ids = cn.bc.core.util.StringUtils
            .stringArray2LongArray(this.getIds().split(","));
          Set<EmailTrash> ets = new HashSet<EmailTrash>();
          for (Long id : ids) {
            entity = this.emailTrashService.load(id);
            entity.setStatus(this.status);
            entity.setOwner(context.getUser());
            entity.setHandleDate(Calendar.getInstance());
            ets.add(entity);
          }

          this.emailTrashService.save(ets);
        } else {
          throw new CoreException("must set property id or ids");
        }
      }

      json.put("success", true);
      json.put("msg", getText("form.delete.success"));
      this.json = json.toString();
      return "json";
    }
  }

  //清空垃圾箱显示的邮件
  public String clear() throws Exception {
    SystemContext context = (SystemContext) this.getContext();

    int size = 0;
    //取得当前用户 垃圾箱显示的邮件
    List<EmailTrash> etlist = this.emailTrashService.find4resumableByOwnerId(context.getUser().getId());
    if (etlist != null && etlist.size() > 0) {
      size = etlist.size();
      Set<EmailTrash> ets = new HashSet<EmailTrash>();
      for (EmailTrash et : etlist) {
        et.setHandleDate(Calendar.getInstance());
        et.setStatus(EmailTrash.STATUS_DELETED);
        ets.add(et);
      }
      this.emailTrashService.save(ets);
    }

    Json json = new Json();
    json.put("size", size);
    json.put("success", true);
    this.json = json.toString();
    return "json";
  }

}