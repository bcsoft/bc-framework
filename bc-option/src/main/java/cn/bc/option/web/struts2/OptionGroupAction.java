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

import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.option.domain.OptionGroup;
import cn.bc.web.struts2.CrudAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 选项分组Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OptionGroupAction extends CrudAction<Long, OptionGroup> {
	// private static Log logger = LogFactory.getLog(OptionGroupAction.class);
	private static final long serialVersionUID = 1L;

	@Autowired
	public void setOptionGroupService(
			@Qualifier(value = "optionGroupService") CrudService<OptionGroup> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("value");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("orderNo");
	}

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(600).setMinWidth(200)
				.setHeight(350).setMinHeight(200);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "orderNo", "value", "key", "icon" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		if (this.useColumn("orderNo"))
			columns.add(new TextColumn("orderNo", getText("label.order"), 80)
					.setSortable(true));
		if (this.useColumn("key"))
			columns.add(new TextColumn("key", getText("option.key"))
					.setSortable(true));
		if (this.useColumn("value"))
			columns.add(new TextColumn("value", getText("option.value"), 150)
					.setSortable(true));
		if (this.useColumn("icon"))
			columns.add(new TextColumn("icon", getText("option.icon"), 80)
					.setSortable(true));
		return columns;
	}
}
