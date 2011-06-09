/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.Actor;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 部门Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class DepartmentAction extends AbstractActorAction {
	private static final long serialVersionUID = 1L;

	protected String getEntityConfigName() {
		return "Department";
	}

	public String create() throws Exception {
		String r = super.create();
		this.getE().setType(Actor.TYPE_DEPARTMENT);
		return r;
	}

	// 设置页面的尺寸
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(650).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	// 设置表格的列
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();

		if (this.useColumn("status"))
			columns.add(new TextColumn("status", getText("actor.status"), 60)
					.setFormater(new EntityStatusFormater(getEntityStatuses())));
		if (this.useColumn("order"))
			columns.add(new TextColumn("order", getText("actor.order"), 80)
					.setSortable(true).setDir(Direction.Asc));
		if (this.useColumn("code"))
			columns.add(new TextColumn("code", getText("actor.code"), 80)
					.setSortable(true));
		if (this.useColumn("name"))
			columns.add(new TextColumn("name", getText("actor.name"))
					.setSortable(true));
		if (this.useColumn("phone"))
			columns.add(new TextColumn("phone", getText("actor.phone"), 120));
		// columns.add(new TextColumn("email", getText("actor.email"), 150));

		return columns;
	}

	// 附加部门的查询条件
	protected Condition getCondition() {
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("type", new Integer(
				Actor.TYPE_DEPARTMENT)));
		// condition.add(new EqualsCondition("status", new
		// Integer(Entity.STATUS_ENABLED)));
		condition.add(new OrderCondition("order", Direction.Asc).add("code",
				Direction.Asc));
		return condition.add(this.getSearchCondition());
	}

	protected Integer[] getBelongTypes() {
		return new Integer[] { Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT };
	}
}
