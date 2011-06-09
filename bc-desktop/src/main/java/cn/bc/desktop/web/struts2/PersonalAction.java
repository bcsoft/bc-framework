/**
 * 
 */
package cn.bc.desktop.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.Entity;
import cn.bc.desktop.domain.Personal;
import cn.bc.desktop.service.PersonalService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.web.struts2.CrudAction;

/**
 * 个人设置Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PersonalAction extends CrudAction<Long, Personal> {
	private static final long serialVersionUID = 1L;
	private String font;
	private String theme;
	private PersonalService personalService;
	private ActorService actorService;

	@Autowired
	public void setPersonalService(PersonalService personalService) {
		this.personalService = personalService;
		this.setCrudService(personalService);
	}

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
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
		Actor actor = (Actor)getCurrentActor();
		Personal personal = this.personalService.loadByActor(actor.getId());
		if (personal == null) {// 没有就从全局配置复制一个
			Personal common = this.personalService.loadGlobalConfig();
			personal = new Personal();
			personal.setStatus(Entity.STATUS_ENABLED);
			personal.setInner(common.isInner());
			personal.setFont(common.getFont());
			personal.setTheme(common.getTheme());
			personal.setActor(actor);
		}
		
		this.setE(personal);
		return "form";
	}

	// 更新个人设置
	public String update() throws Exception {
		Actor actor = getCurrentActor();
		Personal personal = this.personalService.loadByActor(actor.getId());
		if (personal == null) {// 没有就从全局配置复制一个
			Personal common = this.personalService.loadGlobalConfig();
			personal = new Personal();
			personal.setStatus(Entity.STATUS_ENABLED);
			personal.setInner(common.isInner());
			personal.setFont(common.getFont());
			personal.setTheme(common.getTheme());
			personal.setActor((Actor)actor);
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

	private Actor getCurrentActor() {
		Long actorId = new Long(14);
		Actor actor = this.actorService.load(actorId);
		return actor;
	}
}
