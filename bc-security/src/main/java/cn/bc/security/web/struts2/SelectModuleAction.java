/**
 * 
 */
package cn.bc.security.web.struts2;

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
import cn.bc.security.domain.Module;
import cn.bc.security.service.ModuleService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择模块信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectModuleAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private List<Module> es;
	private ModuleService moduleService;
	private long[] selected;// 当前选中项的id值
	private long[] exclude;// 要排除可选择的项的id
	private Integer[] types;// 资源类型
	private boolean multiple;// 是否可以多选

	@Autowired
	public void setModuleService(ModuleService actorService) {
		this.moduleService = actorService;
	}

	public Integer[] getTypes() {
		return types;
	}

	public void setTypes(Integer[] types) {
		this.types = types;
	}

	public List<Module> getEs() {
		return es;
	}

	public void setEs(List<Module> es) {
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
					Module.TYPE_FOLDER)));
		}
		condition.add(new OrderCondition("code", Direction.Asc));
		this.es = this.moduleService.createQuery().condition(condition).list();

		// 排除不能选择的
		if (this.exclude != null && this.exclude.length > 0) {
			List<Module> ex = new ArrayList<Module>();
			for (Module m : this.es) {
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
