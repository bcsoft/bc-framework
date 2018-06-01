/**
 *
 */
package cn.bc.feedback.web.struts2;

import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.feedback.domain.Feedback;
import cn.bc.feedback.domain.Reply;
import cn.bc.feedback.service.FeedbackService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Calendar;

/**
 * 用户反馈表单Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeedbackAction extends EntityAction<Long, Feedback> {
  // private static Log logger = LogFactory.getLog(FeedbackAction.class);
  private static final long serialVersionUID = 1L;
  private IdGeneratorService idGeneratorService;
  private FeedbackService feedbackService;
  private AttachService attachService;
  public boolean isManager;
  public boolean hasDeletePriviledge;

  @Autowired
  public void setFeedbackService(FeedbackService feedbackService) {
    this.setCrudService(feedbackService);
    this.feedbackService = feedbackService;
  }

  @Autowired
  public void setAttachService(AttachService attachService) {
    this.attachService = attachService;
  }

  @Autowired
  public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
    this.idGeneratorService = idGeneratorService;
  }

  @Override
  public boolean isReadonly() {
    SystemContext context = (SystemContext) this.getContext();
    if (this.getE() != null && this.getE().isNew())
      return false;
    else
      return !context.hasAnyRole(getText("key.role.bc.feedback"),
        getText("key.role.bc.admin"));// 反馈管理或超级管理角色
  }

  @Override
  protected void afterCreate(Feedback entity) {
    SystemContext context = (SystemContext) this.getContext();
    Feedback e = this.getE();
    e.setFileDate(Calendar.getInstance());
    e.setAuthor(context.getUserHistory());

    e.setStatus(Feedback.STATUS_DRAFT);

    e.setUid(this.idGeneratorService.next("feedback.uid"));

    // 构建附件控件
    attachsUI = buildAttachsUI(true, false);
  }

  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(600).setHeight(500);
  }

  @Override
  protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
    if (this.getE().isNew() && editable) {
      // 预览按钮
      pageOption.addButton(new ButtonOption(getText("label.preview"),
        "preview"));

      // 提交按钮
      pageOption.addButton(new ButtonOption(getText("label.submit"),
        "submit"));
    }
  }

  @Override
  protected void beforeSave(Feedback entity) {
    Feedback e = this.getE();
    if (e.getStatus() == Feedback.STATUS_DRAFT)
      e.setStatus(Feedback.STATUS_SUMMIT);

    SystemContext context = (SystemContext) this.getContext();
    e.setModifier(context.getUserHistory());
    e.setModifiedDate(Calendar.getInstance());
  }

  public AttachWidget attachsUI;

  @Override
  protected void afterEdit(Feedback entity) {
    // 构建附件控件
    attachsUI = buildAttachsUI(false, false);

    // 是否有删除权限
    hasDeletePriviledge = ((SystemContext) this.getContext()).hasAnyRole(
      getText("key.role.bc.feedback"), getText("key.role.bc.admin"));
  }

  @Override
  protected void afterOpen(Feedback entity) {
    // 构建附件控件
    attachsUI = buildAttachsUI(false, true);

    // 是否有删除权限
    hasDeletePriviledge = ((SystemContext) this.getContext()).hasAnyRole(
      getText("key.role.bc.feedback"), getText("key.role.bc.admin"));
  }

  @Override
  protected void initForm(boolean editable) throws Exception {
    super.initForm(editable);
  }

  private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
    // 构建附件控件
    String ptype = "feedback.main";
    AttachWidget attachsUI = new AttachWidget();
    attachsUI.setFlashUpload(isFlashUpload());
    attachsUI.addClazz("formAttachs");
    if (!isNew)
      attachsUI.addAttach(this.attachService.findByPtype(ptype, this
        .getE().getUid()));
    attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

    // 上传附件的限制
    attachsUI.addExtension(getText("app.attachs.extensions"))
      .setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
      .setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));

    attachsUI.setReadOnly(forceReadonly ? true : this.isReadonly()
      || !this.getE().isNew());
    return attachsUI;
  }

  public String content;

  public String doReply() throws Exception {
    JSONObject json = new JSONObject();
    if (this.getId() != null && content != null && content.length() > 0) {
      Reply reply = this.feedbackService.addReply(this.getId(), content);
      json.put("success", true);
      json.put("id", reply.getId());
      json.put("authorName", reply.getAuthor().getName());
      json.put("content", reply.getContent());
      json.put("fileDate",
        DateUtils.formatCalendar2Minute(reply.getFileDate()));
    } else {
      json.put("success", false);
      json.put("msg", this.getId() != null ? "回复内容不能为空！"
        : "没有指定回复所属的反馈信息！");
    }

    this.json = json.toString();
    return "json";
  }

  public String deleteReply() throws Exception {
    JSONObject json = new JSONObject();
    if (this.getId() != null) {
      // 是否有删除权限
      hasDeletePriviledge = ((SystemContext) this.getContext())
        .hasAnyRole(getText("key.role.bc.feedback"),
          getText("key.role.bc.admin"));

      if (hasDeletePriviledge) {
        this.feedbackService.deleteReply(this.getId());
        json.put("success", true);
      } else {
        json.put("success", false);
        json.put("msg", "你没有删除回复的权限！");
      }
    } else {
      json.put("success", false);
      json.put("msg", "没有指定要删除的信息！");
    }
    this.json = json.toString();
    return "json";
  }
}
