/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.identity.domain.Actor;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
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
public class DepartmentAction extends AbstractActorAction {
  private static final long serialVersionUID = 1L;

  protected String getEntityConfigName() {
    return "Department";
  }

  @Override
  protected void afterCreate(Actor entity) {
    super.afterCreate(entity);
    this.getE().setType(Actor.TYPE_DEPARTMENT);
    this.getE().setUid(this.getIdGeneratorService().next("department"));
  }

  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    return super.buildFormPageOption(editable).setWidth(618);
  }

  @Override
  protected ButtonOption getDefaultSaveButtonOption() {
    return super.getDefaultSaveButtonOption().setAction(null)
      .setClick("bc.departmentForm.save");
  }

  protected Integer[] getBelongTypes() {
    return new Integer[]{Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT};
  }
}
