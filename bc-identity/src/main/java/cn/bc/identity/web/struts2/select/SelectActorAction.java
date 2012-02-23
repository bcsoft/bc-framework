/**
 * 
 */
package cn.bc.identity.web.struts2.select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择Actor信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectActorAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private List<Map<String, String>> es;
	private ActorService actorService;
	public String selecteds;// 当前选中项的id值，多个用逗号连接
	public String excludes;// 当前选中项的id值，多个用逗号连接
	private boolean multiple;// 是否可以多选
	private boolean history;// 是否选择ActorHistory信息

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

	public ActorService getActorService() {
		return actorService;
	}

	@Autowired()
	public void setActorService(
			@Qualifier(value = "actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	/**
	 * 对话框的标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return getText("actor.title.select");
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

	public boolean isHistory() {
		return history;
	}

	public void setHistory(boolean history) {
		this.history = history;
	}

	public String execute() throws Exception {
		// 获取指定类型和状态的的actor信息
		if (this.isHistory()) {
			this.es = this.actorService.findHistory4option(getActorTypes(),
					getActorStatues());
		} else {
			this.es = this.actorService.find4option(getActorTypes(),
					getActorStatues());
		}

		// 排除不能选择的actor
		long[] exclude = this.getExclude();
		if (exclude != null && exclude.length > 0) {
			List<Map<String, String>> ex = new ArrayList<Map<String, String>>();
			for (Map<String, String> actor : this.getEs()) {
				for (int i = 0; i < exclude.length; i++) {
					if (actor.get("id").equals(String.valueOf(exclude[i]))) {
						ex.add(actor);
						break;
					}
				}
			}
			this.getEs().removeAll(ex);
		}

		return SUCCESS;
	}

	// 默认查询所有类型
	protected Integer[] getActorTypes() {
		return new Integer[] { Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT,
				Actor.TYPE_GROUP, Actor.TYPE_USER };
	}

	// 默认仅查询可用状态
	protected Integer[] getActorStatues() {
		return new Integer[] { BCConstants.STATUS_ENABLED };
	}
}
