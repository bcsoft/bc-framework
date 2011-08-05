/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.domain.Role;
import cn.bc.identity.service.RoleService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 角色Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RoleAction extends EntityAction<Long, Role> {
	private static final long serialVersionUID = 1L;

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.setCrudService(roleService);
	}

	public String create() throws Exception {
		String r = super.create();
		this.getE().setType(Role.TYPE_DEFAULT);
		return r;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("orderNo", Direction.Asc);
	}

	// 设置页面的尺寸
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(500).setHeight(400)
				.setMinWidth(300).setMinHeight(200);
	}

	// 设置表格的列
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();

		if (this.useColumn("orderNo"))
			columns.add(new TextColumn("orderNo", getText("label.order"), 80)
					.setSortable(true).setDir(Direction.Asc));
		if (this.useColumn("code"))
			columns.add(new TextColumn("code", getText("label.code"), 200)
					.setSortable(true));
		if (this.useColumn("name"))
			columns.add(new TextColumn("name", getText("label.name"))
					.setSortable(true));

		return columns;
	}

	// 查询条件中要匹配的域
	protected String[] getSearchFields() {
		return new String[] { "code", "orderNo", "name" };
	}

	public String assignResourceIds;// 分派的模块id，多个id用逗号连接

	@Override
	public String save() throws Exception {
		// 处理分配的模块
		Set<Resource> resources = null;
		if (this.assignResourceIds != null
				&& this.assignResourceIds.length() > 0) {
			resources = new HashSet<Resource>();
			String[] resourceIds = this.assignResourceIds.split(",");
			Resource resource;
			for (String resourceId : resourceIds) {
				resource = new Resource();
				resource.setId(new Long(resourceId));
				resources.add(resource);
			}
		}
		if (this.getE().getResources() != null) {
			this.getE().getResources().clear();
			this.getE().getResources().addAll(resources);
		} else {
			this.getE().setResources(resources);
		}

		this.getCrudService().save(this.getE());
		return "saveSuccess";
	}
}
