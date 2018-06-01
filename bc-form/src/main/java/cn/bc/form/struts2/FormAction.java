package cn.bc.form.struts2;

import cn.bc.form.domain.Form;
import cn.bc.form.service.FormService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 自定义表单Action
 *
 * @author hwx
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FormAction extends EntityAction<Long, Form> implements
  SessionAware {
  private static final long serialVersionUID = 1L;
  private FormService formService;

  @Autowired
  public void setFormService(FormService formService) {
    this.formService = formService;
  }

  @Override
  public boolean isReadonly() {
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole(getText("key.role.bs.workplan.manage"),
      getText("key.role.bc.admin"));
  }

  public boolean isManager() {
    SystemContext context = (SystemContext) this.getContext();
    if (context.hasAnyRole(getText("key.role.bs.workplan.manage"),
      getText("key.role.bc.admin"))) {
      return true;
    } else {
      return false;
    }
  }


}
