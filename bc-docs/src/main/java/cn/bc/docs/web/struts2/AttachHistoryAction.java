/**
 *
 */
package cn.bc.docs.web.struts2;

import cn.bc.core.service.CrudService;
import cn.bc.docs.domain.AttachHistory;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 附件操作日志Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AttachHistoryAction extends EntityAction<Long, AttachHistory> {
  // private static Log logger = LogFactory.getLog(BulletinAction.class);
  private static final long serialVersionUID = 1L;

  @Autowired
  public void setAttachHistoryService(
    @Qualifier(value = "attachHistoryService") CrudService<AttachHistory> crudService) {
    this.setCrudService(crudService);
  }

  @Override
  public boolean isReadonly() {
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole(getText("key.role.bc.attach"),
      getText("key.role.bc.admin"));// 附件管理或超级管理角色
  }
}
