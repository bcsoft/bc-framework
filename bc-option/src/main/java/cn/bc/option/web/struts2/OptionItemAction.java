/**
 *
 */
package cn.bc.option.web.struts2;

import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.struts2.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 选项条目表单Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OptionItemAction extends EntityAction<Long, OptionItem> {
  // private static Log logger = LogFactory.getLog(OptionItemAction.class);
  private static final long serialVersionUID = 1L;

  @Autowired
  public void setOptionItemService(
    @Qualifier(value = "optionItemService") CrudService<OptionItem> crudService) {
    this.setCrudService(crudService);
  }

  @Override
  public boolean isReadonly() {
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole(getText("key.role.bc.option"),
      getText("key.role.bc.admin"));// 选项管理或超级管理角色
  }
}
