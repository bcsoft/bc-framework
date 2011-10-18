/**
 * 
 */
package cn.bc.desktop.web.struts2;

import java.util.List;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import cn.bc.Context;
import cn.bc.core.RichEntity;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.desktop.domain.Shortcut;
import cn.bc.desktop.service.ShortcutService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.service.ResourceService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 桌面快捷方式Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ShortcutAction extends EntityAction<Long, Shortcut> implements
		SessionAware {
	private static final long serialVersionUID = 1L;
	private ShortcutService shortcutService;
	private ResourceService resourceService;

	@Autowired
	public void setShortcutService(ShortcutService shortcutService) {
		this.shortcutService = shortcutService;
		this.setCrudService(shortcutService);
	}

	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	@Override
	public String create() throws Exception {
		this.setE(this.shortcutService.create());
		this.getE().setStatus(RichEntity.STATUS_ENABLED);
		this.getE().setStandalone(true);
		// 设置属于当前用户
		this.getE().setActor(
				((SystemContext) this.session.get(Context.KEY)).getUser());
		return "form";
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("order", Direction.Asc);
	}

	@Override
	protected Condition getSpecalCondition() {
		SystemContext context = (SystemContext) this.session.get(Context.KEY);
		// 当前用户的桌面快捷方式
		Actor curUser = context.getUser();
		return new EqualsCondition("actor.id", curUser.getId());
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
				.setValueFormater(
						new BooleanFormater(getText("shortcut.standalone.yes"),
								getText("shortcut.standalone.no"))));
		columns.add(new TextColumn("name", getText("shortcut.name"), 100)
				.setSortable(true));
		columns.add(new TextColumn("url", getText("shortcut.url")).setSortable(
				true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("iconClass", getText("shortcut.iconClass"),
				90).setSortable(true));
		return columns;
	}

	public String name = "资源名称";

	// 选择图标对话框
	public String selectIconClass() throws Exception {

		return SUCCESS;
	}

	public Long mid;
	public Json json;

	/**
	 * 拖动系统菜单中的项到桌面生成快捷方式的保存
	 * 
	 * @return
	 * @throws Exception
	 */
	public String save4drag() throws Exception {
		Assert.notNull(mid, "need to config mid");
		Resource resource = this.resourceService.load(mid);
		Assert.notNull(resource, "unknow resource's id:" + mid);

		Shortcut shortcut = this.shortcutService.create();
		shortcut.setStatus(RichEntity.STATUS_ENABLED);
		shortcut.setStandalone(resource.getType() == Resource.TYPE_OUTER_LINK);
		shortcut.setOrder(resource.getOrderNo());
		shortcut.setName(resource.getName());
		shortcut.setIconClass(resource.getIconClass());
		shortcut.setUrl(resource.getUrl());

		// 设置关联的资源
		shortcut.setResource(resource);

		// 设置属于当前用户
		shortcut.setActor(((SystemContext) this.getContext()).getUser());
		
		// 保存
		this.shortcutService.save(shortcut);

		json = new Json();
		json.put("id", shortcut.getId());
		json.put("msg", "快捷方式“" + resource.getName() + "”已保存！");
		return "json";
	}

}
