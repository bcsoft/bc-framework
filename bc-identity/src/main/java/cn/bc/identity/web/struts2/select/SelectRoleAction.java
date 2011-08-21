/**
 * 
 */
package cn.bc.identity.web.struts2.select;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.Role;
import cn.bc.identity.service.RoleService;

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
	public String selecteds;// 当前选中项的id值，多个用逗号连接
	public String excludes;// 当前选中项的id值，多个用逗号连接
	public boolean multiple;// 是否可以多选

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public long[] getSelected() {
		if (selecteds != null && selecteds.length() > 0) {
			String[] ss = selecteds.split(",");
			long[] ids = new long[ss.length];
			for (int i = 0; i < ss.length; i++) {
				ids[i] = Long.parseLong(ss[i]);
			}
			return ids;
		} else {
			return new long[0];
		}
	}

	public long[] getExclude() {
		if (excludes != null && excludes.length() > 0) {
			String[] ss = excludes.split(",");
			long[] ids = new long[ss.length];
			for (int i = 0; i < ss.length; i++) {
				ids[i] = Long.parseLong(ss[i]);
			}
			return ids;
		} else {
			return new long[0];
		}
	}

	public String execute() throws Exception {
		this.es = this.roleService.createQuery()
				.condition(new OrderCondition("code", Direction.Asc)).list();

		// 排除不能选择的
		long[] exclude = this.getExclude();
		if (exclude != null && exclude.length > 0) {
			List<Role> ex = new ArrayList<Role>();
			for (Role m : this.es) {
				for (int i = 0; i < exclude.length; i++) {
					if (m.getId().longValue() == exclude[i]) {
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
