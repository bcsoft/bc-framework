/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.service.GroupService;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 岗位Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class GroupAction extends AbstractActorAction {
	private static final long serialVersionUID = 1L;
	private GroupService groupService;

	@Autowired
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	protected String getEntityConfigName() {
		return "Group";
	}

	public String create() throws Exception {
		String r = super.create();
		this.getE().setStatus(Actor.STATUS_ENABLED);
		this.getE().setType(Actor.TYPE_GROUP);
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

	// 附加岗位的查询条件
	protected Condition getCondition() {
		AndCondition condition = new AndCondition();
		condition
				.add(new EqualsCondition("type", new Integer(Actor.TYPE_GROUP)));
		// condition.add(new EqualsCondition("status", new
		// Integer(Entity.STATUS_ENABLED)));
		condition.add(new OrderCondition("order", Direction.Asc).add("code",
				Direction.Asc));
		return condition.add(this.getSearchCondition());
	}

	protected Integer[] getBelongTypes() {
		return new Integer[] { Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT };
	}

	public String assignUserIds;// 分配的用户id，多个id用逗号连接
	public List<Actor> ownedUsers;// 已分配的用户

	@Override
	public String save() throws Exception {
		// 处理分配的角色
		dealRoles4Save();

		// 处理分配的用户
		Long[] userIds = null;
		if (this.assignUserIds != null && this.assignUserIds.length() > 0) {
			String[] uIds = this.assignUserIds.split(",");
			userIds = new Long[uIds.length];
			for (int i = 0; i < uIds.length; i++) {
				userIds[i] = new Long(uIds[i]);
			}
		}

		// 保存
		this.groupService.save(this.getE(), this.belong, userIds);
		return "saveSuccess";
	}

	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));

		// 加载上级组织信息
		this.belong = (Actor) this.getActorService().loadBelong(this.getId(),
				getBelongTypes());

		// 加载已分配的用户
		this.ownedUsers = this.groupService.findFollower(this.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_USER });

		// 加载直接分配的角色和从上级继承的角色
		dealRoles4Edit();

		return "form";
	}
}
