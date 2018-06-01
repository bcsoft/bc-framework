package cn.bc.report.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.report.service.ReportTemplateService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 报表模板表单Action
 *
 * @author lbj
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportTemplateAction extends FileEntityAction<Long, ReportTemplate> {
  private static final long serialVersionUID = 1L;
  private ReportTemplateService reportTemplateService;
  private ActorService actorService;


  public String assignUserIds;// 分配的用户id，多个id用逗号连接
  public Set<Actor> ownedUsers;// 已分配的用户

  @Autowired
  public void setReportTemplateService(ReportTemplateService reportTemplateService) {
    this.reportTemplateService = reportTemplateService;
    this.setCrudService(reportTemplateService);
  }

  @Autowired
  public void setActorService(ActorService actorService) {
    this.actorService = actorService;
  }

  @Override
  public boolean isReadonly() {
    SystemContext context = (SystemContext) this.getContext();
    // 配置权限：报表管理员，报表模板管理员、超级管理员
    return !context.hasAnyRole(getText("key.role.bc.report"),
      getText("key.role.bc.report.template"),
      getText("key.role.bc.admin"));
  }


  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(650)
      .setMinHeight(200).setMinWidth(300).setHeight(500);
  }


  @Override
  protected void afterEdit(ReportTemplate entity) {
    super.afterEdit(entity);

    // 加载已分配的用户
    this.ownedUsers = entity.getUsers();
  }


  @Override
  protected void afterOpen(ReportTemplate entity) {
    super.afterOpen(entity);

    // 加载已分配的用户
    this.ownedUsers = entity.getUsers();
  }

  @Override
  protected void buildFormPageButtons(PageOption pageOption, boolean editable) {

    boolean readonly = this.isReadonly();

    if (editable && !readonly) {
      // 添加执行按钮
      pageOption.addButton(new ButtonOption(getText("reportTemplate.execute"), null,
        "bc.reportTemplateForm.execute"));
      // 添加保存按钮
      pageOption.addButton(new ButtonOption(getText("label.save"), null,
        "bc.reportTemplateForm.save"));
    }
  }


  @Override
  protected void beforeSave(ReportTemplate entity) {
    super.beforeSave(entity);
    // 处理分配的用户
    Long[] userIds = null;
    if (this.assignUserIds != null && this.assignUserIds.length() > 0) {
      String[] uIds = this.assignUserIds.split(",");
      userIds = new Long[uIds.length];
      for (int i = 0; i < uIds.length; i++) {
        userIds[i] = new Long(uIds[i]);
      }
    }

    if (userIds != null && userIds.length > 0) {
      Set<Actor> users = null;
      Actor user = null;
      for (int i = 0; i < userIds.length; i++) {
        if (i == 0) {
          users = new HashSet<Actor>();
        }
        user = this.actorService.load(userIds[i]);
        users.add(user);
      }

      if (this.getE().getUsers() != null) {
        this.getE().getUsers().clear();
        this.getE().getUsers().addAll(users);
      } else {
        this.getE().setUsers(users);
      }

    }
  }

  @Override
  public String delete() throws Exception {
    SystemContext context = this.getSystyemContext();
    // 将状态设置为禁用而不是物理删除,更新最后修改人和修改时间
    Map<String, Object> attributes = new HashMap<String, Object>();
    attributes.put("status", new Integer(BCConstants.STATUS_DISABLED));
    attributes.put("modifier", context.getUserHistory());
    attributes.put("modifiedDate", Calendar.getInstance());

    if (this.getId() != null) {// 处理一条
      this.reportTemplateService.update(this.getId(), attributes);
    } else {// 处理一批
      if (this.getIds() != null && this.getIds().length() > 0) {
        Long[] ids = cn.bc.core.util.StringUtils
          .stringArray2LongArray(this.getIds().split(","));
        this.reportTemplateService.update(ids, attributes);
      } else {
        throw new CoreException("must set property id or ids");
      }
    }
    Json json = new Json();
    json.put("msg", getText("form.disabled.success"));
    this.json = json.toString();
    return "json";
  }

  public Long rid;
  public String code;

  //检测编码唯一
  public String isUniqueCode() {
    Json json = new Json();
    boolean flag = this.reportTemplateService.isUniqueCode(rid, code);
    json.put("result", !flag);
    this.json = json.toString();
    return "json";
  }
}
