package cn.bc.template.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.template.domain.TemplateParam;
import cn.bc.template.service.TemplateParamService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.persistence.PersistenceException;

/**
 * 模板参数表单Action
 *
 * @author lbj
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateParamAction extends FileEntityAction<Long, TemplateParam> {
  private static final long serialVersionUID = 1L;
  @SuppressWarnings("unused")
  private TemplateParamService templateParamService;

  @Autowired
  public void setTemplateParamService(TemplateParamService templateParamService) {
    this.setCrudService(templateParamService);
    this.templateParamService = templateParamService;
  }

  @Override
  public boolean isReadonly() {
    // 模板管理员或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    // 配置权限：模板管理员
    return !context.hasAnyRole(getText("key.role.bc.template"),
      getText("key.role.bc.admin"));
  }


  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(650)
      .setMinHeight(200).setMinWidth(300).setHeight(575);
  }

  @Override
  protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
    if (!this.isReadonly()) {
      if (editable)
        pageOption.addButton(new ButtonOption(getText("label.save"),
          null, "bc.templateParamForm.save").setId("templateParamSave"));
    }
  }

  @Override
  protected void afterCreate(TemplateParam entity) {
    super.afterCreate(entity);
    // 状态正常
    entity.setStatus(BCConstants.STATUS_ENABLED);
  }

  @Override
  public String delete() throws Exception {
    try {
      if (this.getId() != null) {// 删除一条
        this.getCrudService().delete(this.getId());
      } else {// 删除一批
        if (this.getIds() != null && this.getIds().length() > 0) {
          Long[] ids = cn.bc.core.util.StringUtils
            .stringArray2LongArray(this.getIds().split(","));
          this.getCrudService().delete(ids);
        } else {
          throw new CoreException("must set property id or ids");
        }
      }
    } catch (PersistenceException e) {
      // 处理违反外键约束导致的删除异常，提示用户因关联而无法删除
      //throw new CoreException("JpaSystemException");
      Json json = new Json();
      json.put("msg", getText("templateParam.msg.delete"));
      this.json = json.toString();
      return "json";
    }
    return "deleteSuccess";
  }

}
