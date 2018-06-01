/**
 *
 */
package cn.bc.desktop.web.struts2;

import cn.bc.BCConstants;
import cn.bc.desktop.domain.Personal;
import cn.bc.desktop.service.PersonalService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 个人设置Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PersonalAction extends EntityAction<Long, Personal> {
  private static final long serialVersionUID = 1L;
  private String font;
  private String theme;
  private PersonalService personalService;

  @Autowired
  public void setPersonalService(PersonalService personalService) {
    this.personalService = personalService;
    this.setCrudService(personalService);
  }

  public String getFont() {
    return font;
  }

  public void setFont(String font) {
    this.font = font;
  }

  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  @Override
  public String edit() throws Exception {
    SystemContext context = (SystemContext) this.getContext();
    Actor actor = context.getUser();
    Personal personal = this.personalService.loadByActor(actor.getId());
    if (personal == null) {// 没有就从全局配置复制一个
      Personal common = this.personalService.loadGlobalConfig();
      personal = new Personal();
      personal.setStatus(BCConstants.STATUS_ENABLED);
      // personal.setInner(common.isInner());
      personal.setFont(common.getFont());
      personal.setTheme(common.getTheme());
      personal.setActorId(actor.getId());
    }

    this.setE(personal);
    return "form";
  }

  // 更新个人设置
  public String update() throws Exception {
    SystemContext context = (SystemContext) this.getContext();
    Actor actor = context.getUser();
    Personal personal = this.personalService.loadByActor(actor.getId());
    if (personal == null) {// 没有就从全局配置复制一个
      Personal common = this.personalService.loadGlobalConfig();
      personal = new Personal();
      personal.setStatus(BCConstants.STATUS_ENABLED);
      // personal.setInner(common.isInner());
      personal.setFont(common.getFont());
      personal.setTheme(common.getTheme());
      personal.setActorId(actor.getId());
      if (font != null && font.length() > 0)
        personal.setFont(font);
      if (theme != null && theme.length() > 0)
        personal.setTheme(theme);
    } else {// 仅更新传入的数据
      if (font != null && font.length() > 0)
        personal.setFont(font);
      if (theme != null && theme.length() > 0)
        personal.setTheme(theme);
    }
    this.personalService.save(personal);

    return "saveSuccess";
  }
}
