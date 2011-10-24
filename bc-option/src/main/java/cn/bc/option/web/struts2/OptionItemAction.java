/**
 * 
 */
package cn.bc.option.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 选项条目Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OptionItemAction extends EntityAction<Long, OptionItem> {
	// private static Log logger = LogFactory.getLog(OptionItemAction.class);
	private static final long serialVersionUID = 1L;

	@Autowired
	public void setOptionItemService(
			@Qualifier(value = "optionItemService") CrudService<OptionItem> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.option"),
				getText("key.role.bc.admin"));// 选项管理或超级管理角色
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("value");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("optionGroup.orderNo").add("orderNo",
				Direction.Asc);
	}

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(650).setMinWidth(200)
				.setHeight(350).setMinHeight(200);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "optionGroup.value", "orderNo", "value", "key",
				"icon" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		if (this.useColumn("status"))
			columns.add(new TextColumn("status", getText("label.status"), 60)
					.setSortable(true).setValueFormater(
							new KeyValueFormater(getEntityStatuses())));
		if (this.useColumn("orderNo"))
			columns.add(new TextColumn("orderNo", getText("label.order"), 70)
					.setSortable(true));
		if (this.useColumn("optionGroup.value"))
			columns.add(new TextColumn("optionGroup.value",
					getText("option.optionGroup"), 120).setSortable(true)
					.setUseTitleFromLabel(true));
		if (this.useColumn("key"))
			columns.add(new TextColumn("key", getText("option.key"))
					.setSortable(true).setUseTitleFromLabel(true));
		if (this.useColumn("value"))
			columns.add(new TextColumn("value", getText("option.value"), 120)
					.setSortable(true).setUseTitleFromLabel(true));
		if (this.useColumn("icon"))
			columns.add(new TextColumn("icon", getText("option.icon"), 60)
					.setSortable(true));
		return columns;
	}
}
