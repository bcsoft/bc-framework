package cn.bc.identity.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.service.PrivilegeService;
import cn.bc.web.ui.json.Json;


@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PrivilegeByRoleAction extends AbstractActorAction {
	
	@Autowired
	private PrivilegeService privilegeService; 
	
	public String roleId;
	public String userId;//返回的是historyActor表的id要特殊处理
	public String groupId;
	public String unitOrDepId;
	public String actorId;
	public String resourceId;
	
	public String addUser() {
		Json json = new Json();
		Long roleId = Long.valueOf(this.roleId);
		String[] userIds = userId.split(",");
		for(String u : userIds) {
			privilegeService.addUser(roleId, Long.valueOf(u));
		}
		this.json = json.toString();
		return "json";
	}
	
	public String addGroup() {
		Json json = new Json();
		Long roleId = Long.valueOf(this.roleId);
		String[] groupIds = groupId.split(",");
		for(String g : groupIds) {
			privilegeService.addGroup(roleId, Long.valueOf(g));
		}
		this.json = json.toString();
		return "json";
	}
	
	public String addUnitOrDep() {
		Json json = new Json();
		Long roleId = Long.valueOf(this.roleId);
		String[] unitOrDepIds = unitOrDepId.split(",");
		for(String u : unitOrDepIds) {
			privilegeService.addUnitOrDep(roleId, Long.valueOf(u));
		}
		this.json = json.toString();
		return "json";
	}
	
	
	public String deleteActor() {
		Json json = new Json();
		Long roleId = Long.valueOf(this.roleId);
		String[] actorIds = actorId.split(",");
		for(String a : actorIds) {
			privilegeService.deleteActor(roleId, Long.valueOf(a));
		}
		this.json = json.toString();
		return "json";
	}
}