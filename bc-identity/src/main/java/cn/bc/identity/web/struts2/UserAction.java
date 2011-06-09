/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
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
import cn.bc.identity.domain.ActorDetail;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.domain.Duty;
import cn.bc.identity.service.DutyService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.service.UserService;
import cn.bc.security.domain.Role;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 用户Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class UserAction extends AbstractActorAction {
	private static final long serialVersionUID = 1L;
	// private static final Log logger = LogFactory.getLog(UserAction.class);
	private UserService userService;
	private DutyService dutyService;
	private IdGeneratorService idGeneratorService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
		this.setActorService(userService);
	}

	@Autowired
	public void setDutyService(DutyService dutyService) {
		this.dutyService = dutyService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	protected String getEntityConfigName() {
		return "User";
	}

	public String create() throws Exception {
		String r = super.create();
		this.getE().setType(Actor.TYPE_USER);
		this.getE().setStatus(Actor.STATUS_ENABLED);
		this.getE().setUid(this.idGeneratorService.next("user.uid"));

		// 初始化用户的扩展信息
		ActorDetail detail = new ActorDetail();
		detail.setCreateDate(Calendar.getInstance());
		detail.setSex(ActorDetail.SEX_NONE);
		this.getE().setDetail(detail);

		// 表单可选项的加载
		initSelects();
		this.ownedGroups = new ArrayList<Actor>();

		return r;
	}

	// 设置页面的尺寸
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(700).setMinWidth(450)
				.setHeight(500).setMinHeight(200);
	}

	@Override
	protected Toolbar buildToolbar() {
		return super.buildToolbar().addButton(
				new ToolbarButton().setIcon("ui-icon-document")
						.setText(getText("user.password.reset"))
						.setClick("bc.userList.setPassword"));
	}

	@Override
	protected String getJs() {
		String cp = ServletActionContext.getRequest().getContextPath();
		return cp + "/bc/identity/user/list.js";
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
			columns.add(new TextColumn("code", getText("user.code"))
					.setSortable(true));
		if (this.useColumn("name"))
			columns.add(new TextColumn("name", getText("user.name"), 100)
					.setSortable(true));
		if (this.useColumn("phone"))
			columns.add(new TextColumn("phone", getText("user.phone"), 100));
		if (this.useColumn("email"))
			columns.add(new TextColumn("email", getText("user.email"), 100));

		return columns;
	}

	// 附加用户查询条件
	protected Condition getCondition() {
		AndCondition condition = new AndCondition();
		condition
				.add(new EqualsCondition("type", new Integer(Actor.TYPE_USER)));
		// condition.add(new EqualsCondition("status", new
		// Integer(Entity.STATUS_ENABLED)));
		condition.add(new OrderCondition("order", Direction.Asc).add("code",
				Direction.Asc));
		return condition.add(this.getSearchCondition());
	}

	protected Integer[] getBelongTypes() {
		return new Integer[] { Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT };
	}

	@Override
	public String save() throws Exception {
		// 处理分配的角色
		dealRoles4Save();

		// 处理分派的岗位
		Long[] groupIds = null;
		if (this.assignGroupIds != null && this.assignGroupIds.length() > 0) {
			String[] gids = this.assignGroupIds.split(",");
			groupIds = new Long[gids.length];
			for (int i = 0; i < gids.length; i++) {
				groupIds[i] = new Long(gids[i]);
			}
		}

		// 保存
		this.userService.save(this.getE(), this.belong, groupIds);
		return "saveSuccess";
	}

	public List<Duty> duties;// 可选的职务列表
	public List<Actor> ownedGroups;// 已拥有的岗位
	public Set<Role> inheritRolesFromGroup;// 从岗位间接获取的角色信息
	public String assignGroupIds;// 分派的岗位id，多个id用逗号连接

	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));

		// 加载上级组织信息
		this.belong = (Actor) this.getActorService().loadBelong(this.getId(),
				getBelongTypes());

		// 表单可选项的加载
		initSelects();

		// 加载已拥有的岗位信息
		this.ownedGroups = this.userService.findMaster(this.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_GROUP });

		// 加载直接分配的角色和从上级继承的角色
		dealRoles4Edit();

		// 加载从岗位间接获取的角色信息
		this.inheritRolesFromGroup = new HashSet<Role>();
		for (Actor g : ownedGroups) {
			inheritRolesFromGroup.addAll(g.getRoles());
		}

		return "form";
	}

	// 表单可选项的加载
	public void initSelects() {
		// 加载可选的职务列表
		this.duties = this.dutyService.createQuery()
				.condition(new OrderCondition("code", Direction.Asc)).list();
	}
}
