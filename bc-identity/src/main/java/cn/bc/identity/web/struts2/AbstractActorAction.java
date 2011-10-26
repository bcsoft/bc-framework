/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.Role;
import cn.bc.identity.service.ActorRelationService;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 通用的Actor Action
 * 
 * @author dragon
 * 
 */
public abstract class AbstractActorAction extends EntityAction<Long, Actor> {
	private static final long serialVersionUID = 1L;
	private ActorService actorService;
	private ActorRelationService actorRelationService;
	public String belongIds;// 隶属的上级单位或部门id,多个id用逗号连接
	public String belongNames;// 隶属的上级单位或部门name,多个name用逗号连接
	public Set<Role> ownedRoles;// 已拥有的角色
	public Set<Role> inheritRolesFromOU;// 从上级组织继承的角色信息
	public String assignRoleIds;// 分派的角色id，多个id用逗号连接
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	public String status; // Actor的状态，多个用逗号连接

	public IdGeneratorService getIdGeneratorService() {
		return idGeneratorService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public ActorService getActorService() {
		return actorService;
	}

	@Autowired
	public void setActorService(
			@Qualifier(value = "actorService") ActorService actorService) {
		this.actorService = actorService;
		this.setCrudService(actorService);
	}

	public ActorRelationService getActorRelationService() {
		return actorRelationService;
	}

	@Autowired
	public void setActorRelationService(
			ActorRelationService actorRelationService) {
		this.actorRelationService = actorRelationService;
	}

	protected Class<? extends Actor> getEntityClass() {
		return Actor.class;
	}

	protected String getEntityConfigName() {
		return "Actor";
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.actor"),
				getText("key.role.bc.admin"));// 组织架构管理或超级管理角色
	}

	// 查询条件中要匹配的域
	protected String[] getSearchFields() {
		return new String[] { "code", "orderNo", "name", "pinYin", "phone",
				"email" };
	}

	@Override
	public String save() throws Exception {
		// 处理分配的角色
		dealRoles4Save();

		this.getActorService().save4belong(this.getE(), this.buildBelongIds());
		return "saveSuccess";
	}

	protected Long[] buildBelongIds() {
		Long[] _belongIds;
		if (belongIds != null && belongIds.length() > 0)
			_belongIds = StringUtils
					.stringArray2LongArray(belongIds.split(","));
		else
			_belongIds = null;
		return _belongIds;
	}

	/**
	 * 处理分配的角色
	 */
	protected void dealRoles4Save() {
		Set<Role> roles = null;
		if (this.assignRoleIds != null && this.assignRoleIds.length() > 0) {
			roles = new HashSet<Role>();
			String[] rids = this.assignRoleIds.split(",");
			Role r;
			for (String rid : rids) {
				r = new Role();
				r.setId(new Long(rid));
				roles.add(r);
			}
		}
		if (this.getE().getRoles() != null) {
			this.getE().getRoles().clear();
			this.getE().getRoles().addAll(roles);
		} else {
			this.getE().setRoles(roles);
		}
	}

	/**
	 * 加载直接分配的角色和从上级继承的角色
	 */
	protected void dealRoles4Edit() {
		// 加载直接分配的角色信息
		this.ownedRoles = this.getActorService().load(this.getId()).getRoles();

		this.inheritRolesFromOU = new HashSet<Role>();
		if (belongIds != null && belongIds.length() > 0) {
			// 加载从上级组织继承的角色信息
			List<Actor> belongs = this.getActorService().findBelong(
					this.getId(), getBelongTypes());
			for (Actor belong : belongs) {
				inheritRolesFromOU.addAll(belong.getRoles());
			}

			// 加载从上级的上级继承的角色信息
			for (Actor belong : belongs) {
				List<Actor> ancestorOU = this.getActorService()
						.findAncestorOrganization(
								belong.getId(),
								new Integer[] { Actor.TYPE_UNIT,
										Actor.TYPE_DEPARTMENT });
				for (Actor ou : ancestorOU) {
					inheritRolesFromOU.addAll(ou.getRoles());
				}
			}
		}
	}

	@Override
	public String edit() throws Exception {
		String r = super.edit();

		// 加载上级信息
		initBelongs();

		// 加载直接分配的角色和从上级继承的角色
		dealRoles4Edit();

		return r;
	}

	// 加载上级信息
	protected void initBelongs() {
		List<Actor> belongs = this.getActorService().findBelong(this.getId(),
				getBelongTypes());
		List<String> ids = new ArrayList<String>();
		List<String> names = new ArrayList<String>();
		for (Actor belong : belongs) {
			ids.add(belong.getId().toString());
			names.add(belong.getName());
		}
		this.belongIds = org.springframework.util.StringUtils
				.collectionToCommaDelimitedString(ids);
		this.belongNames = org.springframework.util.StringUtils
				.collectionToCommaDelimitedString(names);
	}

	protected Integer[] getBelongTypes() {
		return null;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("orderNo", Direction.Asc);
	}

	@Override
	protected Condition getSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("status", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("status",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		return statusCondition;
	}

	@Override
	protected Toolbar buildToolbar() {
		return super.buildToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getEntityStatuses(), "status", -1,
								getText("title.click2changeSearchStatus")));
	}
}
