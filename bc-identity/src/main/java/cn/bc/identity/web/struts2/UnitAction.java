/**
 * 
 */
package cn.bc.identity.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.domain.Actor;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 单位Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class UnitAction extends AbstractActorAction {
	private static final long serialVersionUID = 1L;

	protected String getEntityConfigName() {
		return "Unit";
	}

	@Override
	protected void afterCreate(Actor entity) {
		super.afterCreate(entity);
		this.getE().setType(Actor.TYPE_UNIT);
		this.getE().setUid(this.getIdGeneratorService().next("unit"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(618);
	}

	@Override
	protected ButtonOption getDefaultSaveButtonOption() {
		return super.getDefaultSaveButtonOption().setAction(null)
				.setClick("bc.unitForm.save").setId("bcSaveDlgButton");
	}

	protected Integer[] getBelongTypes() {
		return new Integer[] { Actor.TYPE_UNIT };
	}
}
