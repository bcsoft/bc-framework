/**
 * 
 */
package cn.bc.identity.web.struts2.select;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.domain.Actor;

/**
 * 选择单位或部门信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectUnitOrDepartmentAction extends SelectActorAction {
	private static final long serialVersionUID = 1L;

	@Override
	protected Integer[] getActorTypes() {
		return new Integer[] { Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT };
	}

	@Override
	public String getTitle() {
		return getText("unitOrDepartment.title.select");
	}
}
