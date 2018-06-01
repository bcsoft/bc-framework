package cn.bc.log.web.struts2;

import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Calendar;

/**
 * 操作日志Action
 *
 * @author zxr
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OperateLogAction extends EntityAction<Long, OperateLog> {
  private static final long serialVersionUID = 1L;
  @SuppressWarnings("unused")
  private OperateLogService operateLogService;
  private AttachService attachService;
  public AttachWidget attachsUI;
  private IdGeneratorService idGeneratorService;
  public String carId;// 车辆Id
  public String carManId;// 司机Id
  public String module;// 所属模块

  @Autowired
  public void setOperateLogService(OperateLogService operateLogService) {
    this.setCrudService(operateLogService);
    this.operateLogService = operateLogService;

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
    // 操作日志管理员或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole(getText("key.role.bc.operateLog"),
      getText("key.role.bc.admin"));

  }

  @Override
  protected void initForm(boolean editable) throws Exception {
    super.initForm(editable);

  }

  @Override
  protected void afterCreate(OperateLog entity) {

    // 设置创建人信息
    entity.setFileDate(Calendar.getInstance());
    entity.setAuthor(SystemContextHolder.get().getUserHistory());
    // 车辆页签下新建操作日志
    if (carId != null) {
      entity.setPid(carId);
    }
    // 司机页签下新建操作日志
    if (carManId != null) {
      entity.setPid(carManId);
    }
    // 所属模块
    if (module != null) {
      entity.setPtype(module);
    }
    // 用户创建
    entity.setWay(OperateLog.WAY_USER);
    // 工作日志
    entity.setType(OperateLog.TYPE_WORK);
    entity.setUid(this.idGeneratorService.next("WorkLog"));

    // 构建附件控件
    attachsUI = buildAttachsUI(true, false);
  }

  @Override
  protected void afterEdit(OperateLog entity) {
    super.afterEdit(entity);
    // 构建附件控件
    attachsUI = buildAttachsUI(false, false);
  }

  @Override
  protected void afterOpen(OperateLog entity) {
    super.afterOpen(entity);
    // 构建附件控件
    attachsUI = buildAttachsUI(false, true);
  }

  private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
    // 构建附件控件
    String ptype = OperateLog.class.getSimpleName();
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

  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(600)
      .setMinWidth(250).setHeight(420);
  }

  @Override
  protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
    boolean readonly = this.isReadonly();
    if (this.useFormPrint()) {
      // 添加打印按钮
      pageOption.addButton(this.getDefaultPrintButtonOption());
    }

    if (editable) {// edit,create
      // 添加默认的保存按钮
      pageOption.addButton(this.getDefaultSaveButtonOption());
    } else {// open
      if (!readonly) {
        pageOption.addButton(new ButtonOption(
          getText("operateLong.optype.doMaintenance"), null,
          "bc.operateLongForm.doMaintenance"));
      }
    }
  }

}
