/**
 * 
 */
package cn.bc.identity.web.struts2.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.Entity;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.service.ResourceService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择资源信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectResourceAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private List<Map<String, String>> es;
	private ResourceService resourceService;
	public String selecteds;// 当前选中项的id值，多个用逗号连接
	public String excludes;// 当前选中项的id值，多个用逗号连接
	public String types;// 资源类型，多个用逗号连接
	private boolean multiple;// 是否可以多选

	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public long[] getSelected() {
		if (selecteds != null && selecteds.length() > 0) {
			String[] ss = selecteds.split(",");
			long[] ids = new long[ss.length];
			for (int i = 0; i < ss.length; i++) {
				ids[i] = Long.parseLong(ss[i]);
			}
			return ids;
		} else {
			return new long[0];
		}
	}

	public long[] getExclude() {
		if (excludes != null && excludes.length() > 0) {
			String[] ss = excludes.split(",");
			long[] ids = new long[ss.length];
			for (int i = 0; i < ss.length; i++) {
				ids[i] = Long.parseLong(ss[i]);
			}
			return ids;
		} else {
			return new long[0];
		}
	}

	public Integer[] getType() {
		if (types != null && types.length() > 0) {
			String[] ss = types.split(",");
			Integer[] ids = new Integer[ss.length];
			for (int i = 0; i < ss.length; i++) {
				ids[i] = new Integer(ss[i]);
			}
			return ids;
		} else {
			return Resource.getAllTypes();
		}
	}

	public List<Map<String, String>> getEs() {
		return es;
	}

	public void setEs(List<Map<String, String>> es) {
		this.es = es;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public String execute() throws Exception {
		this.es = this.resourceService.find4option(getType(),
				new Integer[] { Entity.STATUS_ENABLED });

		// 排除不能选择的
		long[] exclude = this.getExclude();
		if (exclude != null && exclude.length > 0) {
			List<Map<String, String>> ex = new ArrayList<Map<String, String>>();
			for (Map<String, String> m : this.es) {
				for (int i = 0; i < exclude.length; i++) {
					if (m.get("id").equals(exclude[i])) {
						ex.add(m);
						break;
					}
				}
			}
			this.es.removeAll(ex);
		}

		return SUCCESS;
	}
}
