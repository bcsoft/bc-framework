/**
 * 
 */
package cn.bc.security.web.struts2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.security.domain.Role;
import cn.bc.security.service.RoleService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择角色信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectRoleAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	public List<Role> es;
	private RoleService roleService;
	public long[] selected;// 当前选中项的id值，多个用逗号连接
	public long[] exclude;// 要排除可选择的项的id，多个用逗号连接
	public boolean multiple;// 是否可以多选

	@Autowired
	public void setModuleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public String execute() throws Exception {
		this.es = this.roleService.createQuery()
				.condition(new OrderCondition("code", Direction.Asc)).list();

		// 排除不能选择的
		if (this.exclude != null && this.exclude.length > 0) {
			List<Role> ex = new ArrayList<Role>();
			for (Role m : this.es) {
				for (int i = 0; i < this.exclude.length; i++) {
					if (m.getId().longValue() == this.exclude[i]) {
						ex.add(m);
						break;
					}
				}
			}
			this.es.removeAll(ex);
		}

		return SUCCESS;
	}
}
