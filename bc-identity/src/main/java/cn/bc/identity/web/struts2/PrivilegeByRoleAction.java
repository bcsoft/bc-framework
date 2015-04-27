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
		Long userId = Long.valueOf(this.userId);
		boolean success = privilegeService.addUser(roleId, userId);
		json.put("success", success);
		this.json = json.toString();
		return "json";
	}
	
	public String addGroup() {
		Json json = new Json();
		Long roleId = Long.valueOf(this.roleId);
		Long groupId = Long.valueOf(this.groupId);
		boolean success = privilegeService.addGroup(roleId, groupId);
		json.put("success", success);
		this.json = json.toString();
		return "json";
	}
	
	public String addUnitOrDep() {
		Json json = new Json();
		Long roleId = Long.valueOf(this.roleId);
		Long unitOrDepId = Long.valueOf(this.unitOrDepId);
		boolean success = privilegeService.addUnitOrDep(roleId, unitOrDepId);
		json.put("success", success);
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
	public String addResource() {
		Json json = new Json();
		Long roleId = Long.valueOf(this.roleId);
		Long resourceId = Long.valueOf(this.resourceId);
		boolean success = privilegeService.addResource(roleId, resourceId);
		json.put("success", success);
		this.json = json.toString();
		return "json";
	}
	public String deleteResource() {
		Json json = new Json();
		Long roleId = Long.valueOf(this.roleId);
		String[] resourceIds = resourceId.split(",");
		for(String r : resourceIds) {
			privilegeService.deleteResource(roleId, Long.valueOf(r));
		}
		this.json = json.toString();
		return "json";
	}
	
}
