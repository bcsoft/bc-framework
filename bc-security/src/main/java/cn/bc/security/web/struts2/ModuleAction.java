/**
 * 
 */
package cn.bc.security.web.struts2;

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
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.security.domain.Module;
import cn.bc.security.service.ModuleService;
import cn.bc.security.web.ModuleTypeFormater;
import cn.bc.web.struts2.CrudAction;
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
public class ModuleAction extends CrudAction<Long, Module> {
	private static final long serialVersionUID = 1L;
	// private ModuleService moduleService;
	public List<KeyValue> types;// 可选的模块类型

	// 模块类型列表
	public ModuleAction() {
		types = new ArrayList<KeyValue>();
		for (Entry<String, String> e : getModuleTypes().entrySet()) {
			types.add(new KeyValue(e.getKey(), e.getValue()));
		}
	}

	@Autowired
	public void setModuleService(ModuleService moduleService) {
		// this.moduleService = moduleService;
		this.setCrudService(moduleService);
	}

	public String create() throws Exception {
		String r = super.create();
		this.getE().setType(Module.TYPE_INNER_LINK);
		return r;
	}

	// 附加查询条件
	protected Condition getCondition() {
		OrCondition or = this.getSearchCondition();
		OrderCondition order = new OrderCondition("code", Direction.Asc);
		return or == null ? order : or.add(order);
	}

	// 设置页面的尺寸
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setHeight(400)
				.setMinWidth(450).setMinHeight(200);
	}

	// 设置表格的列
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();

		// columns.add(new TextColumn("status", getText("actor.status"), 40));
		if (this.useColumn("type"))
			columns.add(new TextColumn("type", getText("module.type"), 80)
					.setSortable(true).setFormater(
							new ModuleTypeFormater(getModuleTypes())));
		if (this.useColumn("belong.name"))
			columns.add(new TextColumn("belong.name", getText("module.belong"),
					80));
		if (this.useColumn("code"))
			columns.add(new TextColumn("code", getText("label.order"), 100)
					.setSortable(true).setDir(Direction.Asc));
		if (this.useColumn("name"))
			columns.add(new TextColumn("name", getText("label.name"), 100)
					.setSortable(true));
		if (this.useColumn("url"))
			columns.add(new TextColumn("url", getText("module.url"))
					.setSortable(true));
		if (this.useColumn("iconClass"))
			columns.add(new TextColumn("iconClass",
					getText("module.iconClass"), 100).setSortable(true));
		if (this.useColumn("option"))
			columns.add(new TextColumn("option", getText("module.option"), 100));

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
		types.put(String.valueOf(Module.TYPE_FOLDER),
				getText("module.type.folder"));
		types.put(String.valueOf(Module.TYPE_OPERATE),
				getText("module.type.operate"));
		types.put(String.valueOf(Module.TYPE_INNER_LINK),
				getText("module.type.innerLink"));
		types.put(String.valueOf(Module.TYPE_OUTER_LINK),
				getText("module.type.outerLink"));
		return types;
	}

	// 查询条件中要匹配的域
	protected String[] getSearchFields() {
		return new String[] { "code", "name", "url", "iconClass", "option" };
	}

	@Override
	public String save() throws Exception {
		// 处理隶属关系
		Module belong = this.getE().getBelong();
		if (belong != null
				&& (belong.getId() == null || belong.getId().longValue() == 0)) {
			this.getE().setBelong(null);
		}

		return super.save();
	}
}
