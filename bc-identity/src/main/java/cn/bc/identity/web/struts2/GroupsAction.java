/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.identity.domain.Actor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 岗位Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class GroupsAction extends AbstractActorsAction {
  private static final long serialVersionUID = 1L;

  @Override
  protected String getFormActionName() {
    return "group";
  }

  @Override
  protected String getActorType() {
    // 岗位条件
    return String.valueOf(Actor.TYPE_GROUP);
  }
}
