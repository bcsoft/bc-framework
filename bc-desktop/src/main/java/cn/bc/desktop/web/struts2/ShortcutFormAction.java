/**
 *
 */
package cn.bc.desktop.web.struts2;

import cn.bc.BCConstants;
import cn.bc.desktop.domain.Shortcut;
import cn.bc.desktop.service.ShortcutService;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.service.ResourceService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 桌面管理表单
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ShortcutFormAction extends EntityAction<Long, Shortcut> {
	private static final long serialVersionUID = 1L;
	private ShortcutService shortcutService;
	@Autowired
	private ResourceService resourceService;

	@Autowired
	public void setShortcutService(ShortcutService shortcutService) {
		this.shortcutService = shortcutService;
		this.setCrudService(shortcutService);
	}

	@Override
	public boolean isReadonly() {
		return false;// 所有人都可以管理自己的桌面
	}

	@Override
	public String getPageNamespace() {
		return this.getContextPath() + "/bc/shortcut";
	}

	@Override
	protected void addJsCss(List<String> container) {
		container.add("bc/desktop/shortcut/form.js");
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(415).setMinWidth(300).setMinHeight(200);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		super.buildFormPageButtons(pageOption, editable);
	}

	@Override
	protected void afterCreate(Shortcut entity) {
		entity.setStatus(BCConstants.STATUS_ENABLED);
		entity.setStandalone(true);
		entity.setResourceId((long) 0);
		// 设置属于当前用户
		this.getE().setActorId(((SystemContext) this.getContext()).getUser().getId());
	}

	public Long mid;

	/**
	 * 拖动系统菜单中的项到桌面生成快捷方式的保存
	 */
	public String save4drag() throws Exception {
		Assert.notNull(mid, "need to config mid");
		Resource resource = this.resourceService.load(mid);
		Assert.notNull(resource, "unknow resource's id:" + mid);

		Shortcut shortcut = this.shortcutService.create();
		shortcut.setStatus(BCConstants.STATUS_ENABLED);
		shortcut.setStandalone(resource.getType() == Resource.TYPE_OUTER_LINK);
		shortcut.setOrder(resource.getOrderNo());
		shortcut.setName(resource.getName());
		shortcut.setIconClass(resource.getIconClass());
		shortcut.setUrl(resource.getUrl());

		// 设置关联的资源
		shortcut.setResourceId(resource.getId());

		// 设置关联的扩展配置
		shortcut.setCfg(resource.getOption());

		// 设置属于当前用户
		shortcut.setActorId(((SystemContext) this.getContext()).getUser().getId());

		// 保存
		this.shortcutService.save(shortcut);

		JSONObject json = new JSONObject();
		json.put("id", shortcut.getId());
		json.put("msg", "快捷方式“" + resource.getName() + "”已保存！");
		this.json = json.toString();
		return "json";
	}

	// 选择图标对话框
	public String selectIconClass() throws Exception {
		return SUCCESS;
	}
}