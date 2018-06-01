package cn.bc.email.web.struts2;

import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.email.domain.Email;
import cn.bc.email.domain.EmailTo;
import cn.bc.email.service.EmailHistoryService;
import cn.bc.email.service.EmailService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 邮件管理表单Action
 *
 * @author lbj
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Email2ManageAction extends EntityAction<Long, Email> {
  private static final long serialVersionUID = 1L;
  public Integer type = 0;// 0：新邮件
  public Integer openType;// 类型 1-已发邮件 2-已收邮件 3-垃圾邮件
  public String receivers;// 邮件接收人
  public String week4cn;//星期

  public List<Map<String, Object>> receiverList;//收件人集合
  public List<Map<String, Object>> ccList;//抄送集合
  public List<Map<String, Object>> bccList;//密送集合

  private EmailService emailService;
  private EmailHistoryService emailHistoryService;
  private AttachService attachService;

  @Autowired
  public void setEmailHistoryService(EmailHistoryService emailHistoryService) {
    this.emailHistoryService = emailHistoryService;
  }

  @Autowired
  public void setEmailService(EmailService emailService) {
    this.emailService = emailService;
    this.setCrudService(emailService);
  }

  @Autowired
  public void setAttachService(AttachService attachService) {
    this.attachService = attachService;
  }

  public AttachWidget attachsUI;


  @Override
  protected void afterOpen(Email entity) {
    super.afterOpen(entity);
    this.attachsUI = this.buildAttachsUI(false, true);
    this.week4cn = DateUtils.getWeekCN(entity.getSendDate());

    this.sortable();
  }

  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(600)
      .setMinHeight(200).setHeight(460);
  }

  @Override
  protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
    pageOption.addButton(new ButtonOption(getText("emailHistory2Manage.open"), null,
      "bc.email2ManageViewBase.openHistory"));

  }

  protected AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
    // 构建附件控件
    String ptype = Email.ATTACH_TYPE;
    String puid = this.getE().getUid();
    boolean readonly = forceReadonly ? true : this.isReadonly();
    AttachWidget attachsUI = AttachWidget.defaultAttachWidget(isNew,
      readonly, isFlashUpload(), this.attachService, ptype, puid);
    // 上传附件的限制
    attachsUI.addExtension(getText("app.attachs.extensions"))
      .setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
      .setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
    return attachsUI;
  }


  //将收件人分类
  private void sortable() {
    Set<EmailTo> tos = this.getE().getTos();
    if (tos == null) return;
    Map<String, Object> map;

    for (EmailTo to : tos) {

      map = new HashMap<String, Object>();
      map.put("receiver", to.getReceiver());
      if (to.getUpper() != null) map.put("upper", to.getUpper());
      map.put("reciverReadCount",
        this.emailHistoryService.getEmailReceiverReadCount(this.getE().getId(), to.getReceiver().getCode()));

      switch (to.getType()) {
        case EmailTo.TYPE_TO:
          if (this.receiverList == null) this.receiverList = new ArrayList<Map<String, Object>>();
          this.receiverList.add(map);
          break;
        case EmailTo.TYPE_CC:
          if (this.ccList == null) this.ccList = new ArrayList<Map<String, Object>>();
          this.ccList.add(map);
          break;
        case EmailTo.TYPE_BCC:
          if (this.bccList == null) this.bccList = new ArrayList<Map<String, Object>>();
          this.bccList.add(map);
          break;
        default:
          return;
      }

    }
  }

}