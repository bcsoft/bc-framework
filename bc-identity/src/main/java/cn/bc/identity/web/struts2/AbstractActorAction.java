/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorRelationService;
import cn.bc.identity.service.ActorService;
import cn.bc.security.domain.Role;
import cn.bc.web.struts2.CrudAction;

/**
 * 通用的Actor Action
 * 
 * @author dragon
 * 
 */
public abstract class AbstractActorAction extends CrudAction<Long, Actor> {
	private static final long serialVersionUID = 1L;
	private ActorService actorService;
	private ActorRelationService actorRelationService;
	protected Actor belong;// 隶属的上级单位或部门
	public Set<Role> ownedRoles;// 已拥有的角色
	public Set<Role> inheritRolesFromOU;// 从上级组织继承的角色信息
	public String assignRoleIds;// 分派的角色id，多个id用逗号连接

	public Actor getBelong() {
		return belong;
	}

	public void setBelong(Actor belong) {
		this.belong = belong;
	}

	public ActorService getActorService() {
		return actorService;
	}

	@Autowired
	public void setActorService(ActorService actorService) {
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

	// 查询条件中要匹配的域
	protected String[] getSearchFields() {
		return new String[] { "code", "order", "name", "phone", "email" };
	}

	@Override
	public String save() throws Exception {
		// 处理分配的角色
		dealRoles4Save();

		this.getActorService().save4belong(this.getE(), belong);
		return "saveSuccess";
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
		if (this.belong != null) {
			// 加载从上级组织继承的角色信息
			inheritRolesFromOU.addAll(this.belong.getRoles());

			// 加载从上级的上级继承的角色信息
			List<Actor> ancestorOU = this.getActorService()
					.findAncestorOrganization(
							this.belong.getId(),
							new Integer[] { Actor.TYPE_UNIT,
									Actor.TYPE_DEPARTMENT });
			for (Actor ou : ancestorOU) {
				inheritRolesFromOU.addAll(ou.getRoles());
			}
		}
	}

	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));

		// 加载上级信息
		this.belong = (Actor) this.getActorService().loadBelong(this.getId(),
				getBelongTypes());

		// 加载直接分配的角色和从上级继承的角色
		dealRoles4Edit();

		return "form";
	}

	protected Integer[] getBelongTypes() {
		return null;
	};
}
