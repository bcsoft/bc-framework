/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.KeyValue;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.service.ResourceService;
import cn.bc.identity.web.ResourceTypeFormater;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 模块Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ResourceAction extends EntityAction<Long, Resource> {
	private static final long serialVersionUID = 1L;
	public List<KeyValue> types;// 可选的模块类型

	// 模块类型列表
	public ResourceAction() {
		types = new ArrayList<KeyValue>();
		for (Entry<String, String> e : getModuleTypes().entrySet()) {
			types.add(new KeyValue(e.getKey(), e.getValue()));
		}
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.actor"),
				getText("key.role.bc.admin"));// 超级管理员
	}

	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.setCrudService(resourceService);
	}

	public String create() throws Exception {
		String r = super.create();
		this.getE().setType(Resource.TYPE_INNER_LINK);
		return r;
	}

	// 设置视图页面的尺寸
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setHeight(400)
				.setMinWidth(450).setMinHeight(200);
	}

	// 设置表单页面的尺寸
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(618);
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("orderNo", Direction.Asc);
	}

	// 设置表格的列
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();

		// columns.add(new TextColumn("status", getText("actor.status"), 40));
		if (this.useColumn("type"))
			columns.add(new TextColumn("type", getText("resource.type"), 80)
					.setSortable(true).setValueFormater(
							new ResourceTypeFormater(getModuleTypes())));
		if (this.useColumn("belong.name"))
			columns.add(new TextColumn("belong.name",
					getText("resource.belong"), 80));
		if (this.useColumn("orderNo"))
			columns.add(new TextColumn("orderNo", getText("label.order"), 100)
					.setSortable(true).setDir(Direction.Asc));
		if (this.useColumn("name"))
			columns.add(new TextColumn("name", getText("label.name"), 100)
					.setSortable(true));
		if (this.useColumn("url"))
			columns.add(new TextColumn("url", getText("resource.url"))
					.setSortable(true));
		if (this.useColumn("iconClass"))
			columns.add(new TextColumn("iconClass",
					getText("resource.iconClass"), 100).setSortable(true));
		if (this.useColumn("option"))
			columns.add(new TextColumn("option", getText("resource.option"),
					100));

		return columns;
	}

	/**
	 * 获取资源类型值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getModuleTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types = new LinkedHashMap<String, String>();
		types.put(String.valueOf(Resource.TYPE_FOLDER),
				getText("resource.type.folder"));
		types.put(String.valueOf(Resource.TYPE_INNER_LINK),
				getText("resource.type.innerLink"));
		types.put(String.valueOf(Resource.TYPE_OUTER_LINK),
				getText("resource.type.outerLink"));
		return types;
	}

	// 查询条件中要匹配的域
	protected String[] getSearchFields() {
		return new String[] { "orderNo", "name", "url", "iconClass", "option" };
	}

	@Override
	public String save() throws Exception {
		// 处理隶属关系
		Resource belong = this.getE().getBelong();
		if (belong != null
				&& (belong.getId() == null || belong.getId().longValue() == 0)) {
			this.getE().setBelong(null);
		}

		return super.save();
	}
}
