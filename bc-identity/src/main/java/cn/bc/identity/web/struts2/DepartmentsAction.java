/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.identity.domain.Actor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 部门Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class DepartmentsAction extends AbstractActorsAction {
  private static final long serialVersionUID = 1L;

  @Override
  protected String getFormActionName() {
    return "department";
  }

  @Override
  protected String getActorType() {
    // 部门条件
    return String.valueOf(Actor.TYPE_DEPARTMENT);
  }
}
