/**
 * 
 */
package cn.bc.web.struts2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.Entity;
import cn.bc.web.struts2.jpa.ViewActionWithJpa;

/**
 * 平台各模块视图Action的基类封装
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class ViewAction<T extends Object> extends ViewActionWithJpa<T> {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}

	/**
	 * 状态值转换列表：正常|禁用|删除|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBCStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Entity.STATUS_ENABLED),
				getText("bc.status.enabled"));
		statuses.put(String.valueOf(Entity.STATUS_DISABLED),
				getText("bc.status.disabled"));
		statuses.put(String.valueOf(Entity.STATUS_DELETED),
				getText("bc.status.deleted"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}
}
