/**
 * 
 */
package cn.bc.identity.web.struts2.select;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.MixCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
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
	private List<Resource> es;
	private ResourceService resourceService;
	private long[] selected;// 当前选中项的id值
	private long[] exclude;// 要排除可选择的项的id
	private Integer[] types;// 资源类型
	private boolean multiple;// 是否可以多选

	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public Integer[] getTypes() {
		return types;
	}

	public void setTypes(Integer[] types) {
		this.types = types;
	}

	public List<Resource> getEs() {
		return es;
	}

	public void setEs(List<Resource> es) {
		this.es = es;
	}

	public long[] getSelected() {
		return selected;
	}

	public void setSelected(long[] selected) {
		this.selected = selected;
	}

	public long[] getExclude() {
		return exclude;
	}

	public void setExclude(long[] exclude) {
		this.exclude = exclude;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public String execute() throws Exception {
		MixCondition condition = new AndCondition();
		if (types != null && types.length > 0) {
			condition.add(new InCondition("type", types));
		} else {// 默认为选择模块
			condition.add(new EqualsCondition("type", new Integer(
					Resource.TYPE_FOLDER)));
		}
		condition.add(new OrderCondition("orderNo", Direction.Asc));
		this.es = this.resourceService.createQuery().condition(condition).list();

		// 排除不能选择的
		if (this.exclude != null && this.exclude.length > 0) {
			List<Resource> ex = new ArrayList<Resource>();
			for (Resource m : this.es) {
				for (int i = 0; i < this.exclude.length; i++) {
					if (m.getId().longValue() == this.exclude[i]) {
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
