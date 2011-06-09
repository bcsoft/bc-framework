/**
 * 
 */
package cn.bc.desktop.web.struts2;

import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.Entity;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.desktop.domain.Shortcut;
import cn.bc.desktop.service.ShortcutService;
import cn.bc.identity.domain.Actor;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.struts2.CrudAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 桌面快捷方式Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ShortcutAction extends CrudAction<Long, Shortcut> implements
		SessionAware {
	private static final long serialVersionUID = 1L;
	private ShortcutService shortcutService;
	private Map<String, Object> session;

	@Autowired
	public void setShortcutService(ShortcutService shortcutService) {
		this.shortcutService = shortcutService;
		this.setCrudService(shortcutService);
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public String create() throws Exception {
		this.setE(this.shortcutService.create());
		this.getE().setStatus(Entity.STATUS_ENABLED);
		this.getE().setStandalone(true);
		// 设置属于当前用户
		this.getE().setActor((Actor) this.session.get("user"));
		return "form";
	}

	@Override
	protected GridData buildGridData() {
		return super.buildGridData().setRowLabelExpression("name");
	}

	@Override
	protected Condition getCondition() {
		Actor curUser = (Actor) this.session.get("user");
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("actor.id", curUser.getId()));// 当前用户的桌面快捷方式
		condition.add(new OrderCondition("order", Direction.Asc));
		return condition.add(this.getSearchCondition());
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "name", "url", "iconClass" };
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(600).setMinWidth(200)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("order", getText("shortcut.order"), 80)
				.setDir(Direction.Asc).setSortable(true));
		columns.add(new TextColumn("standalone",
				getText("shortcut.standalone"), 80).setSortable(true)
				.setFormater(
						new BooleanFormater(getText("shortcut.standalone.yes"),
								getText("shortcut.standalone.no"))));
		columns.add(new TextColumn("name", getText("shortcut.name"), 100)
				.setSortable(true));
		columns.add(new TextColumn("url", getText("shortcut.url"))
				.setSortable(true));
		columns.add(new TextColumn("iconClass", getText("shortcut.iconClass"),
				80).setSortable(true));
		return columns;
	}

	public String name = "模块名称";

	// 选择图标对话框
	public String selectIconClass() throws Exception {

		return SUCCESS;
	}
}
