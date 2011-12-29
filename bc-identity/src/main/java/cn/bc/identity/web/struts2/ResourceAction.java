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
import cn.bc.identity.domain.Resource;
import cn.bc.identity.service.ResourceService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 资源的表单Action
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

	@Override
	protected void afterCreate(Resource entity) {
		this.getE().setType(Resource.TYPE_INNER_LINK);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(618);
	}

	/**
	 * 获取资源类型值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getModuleTypes() {
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

	@Override
	protected void beforeSave(Resource entity) {
		// 处理隶属关系
		Resource belong = this.getE().getBelong();
		if (belong != null
				&& (belong.getId() == null || belong.getId().longValue() == 0)) {
			this.getE().setBelong(null);
		}
	}
}
