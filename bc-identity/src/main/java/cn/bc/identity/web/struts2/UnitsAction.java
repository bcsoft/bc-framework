/**
 * 
 */
package cn.bc.identity.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.domain.Actor;

/**
 * 单位Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class UnitsAction extends AbstractActorsAction {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getFormActionName() {
		return "unit";
	}

	@Override
	protected String getActorType() {
		// 单位条件
		return String.valueOf(Actor.TYPE_UNIT);
	}
}
