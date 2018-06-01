package cn.bc.form.struts2;

import cn.bc.form.domain.Field;
import cn.bc.form.service.FieldService;
import cn.bc.identity.service.ActorService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 字段表单Action
 *
 * @author hwx
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FieldAction extends EntityAction<Long, Field> implements
  SessionAware {
  private static final long serialVersionUID = 1L;
  private FieldService fieldService;
  private ActorService actorService;

  @Autowired
  public void setFieldService(FieldService fieldService) {
    this.setCrudService(fieldService);
    this.fieldService = fieldService;
  }

  @Autowired
  public void setActorService(ActorService actorService) {
    this.actorService = actorService;
  }

  @Override
  protected PageOption buildFormPageOption(boolean editable) {
    if (!isReadonly()) { // 如果是管理员
      editable = true;
    } else {
      editable = false;
    }
    return super.buildFormPageOption(editable).setWidth(720)
      .setMinWidth(250).setHeight(550).setMinHeight(200)
      .setMaxHeight(700);
  }

  @Override
  protected void buildFormPageButtons(PageOption option, boolean editable) {

    option.addButton(getDefaultSaveButtonOption());

  }

  @Override
  public String save() throws Exception {
    Field e = this.getE();
    this.beforeSave(e);
    this.getCrudService().save(e);
    this.afterSave(e);
    return "saveSuccess";
  }

}
