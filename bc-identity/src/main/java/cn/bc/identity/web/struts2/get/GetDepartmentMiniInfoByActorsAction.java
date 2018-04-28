package cn.bc.identity.web.struts2.get;

import cn.bc.identity.dto.DepartmentByActorDto4MiniInfo;
import cn.bc.identity.service.ActorHistoryService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

/**
 * AJAX 批量获取指定用户当前所属部门 Action
 *
 * @author cjw
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class GetDepartmentMiniInfoByActorsAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory.getLog("GetUsersAction");
	private ActorHistoryService actorHistoryService;

	private Long[] actorHistoryIds; // 用户历史 Id 数组
	public String json;

	public void setActorHistoryIds(String actorHistoryIds) {
		this.actorHistoryIds = Arrays.stream(actorHistoryIds.split(",")).map(Long::parseLong).toArray(Long[]::new);
	}

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

	@Override
	public String execute() throws Exception {
		JSONArray json = new JSONArray();
		if (actorHistoryIds == null || actorHistoryIds.length == 0) // 如果 actorHistoryIds 为空则报错
			throw new NullPointerException("用户历史 Id 不能为空!");
		// 获取用户当前所属部门简易信息数据集合
		List<DepartmentByActorDto4MiniInfo> data = actorHistoryService.findDepartmentMiniInfoByActors(actorHistoryIds);
		// 转换为 Json 数组
		convert2JsonArray(json, data);
		if (logger.isDebugEnabled()) logger.debug("json=" + json);
		this.json = json.toString();
		return "json";
	}


	/**
	 * 转换数据为JsonArray格式
	 *
	 * @param array 返回结果的 Json 数组
	 * @param data  用户当前所属部门简易信息数据集合
	 */
	protected void convert2JsonArray(JSONArray array, List<DepartmentByActorDto4MiniInfo> data) {
		for (DepartmentByActorDto4MiniInfo d : data) array.put(this.convert2JsonObject(d));
	}

	/**
	 * 转换对象为JsonObject格式
	 *
	 * @param obj 转换成 Json 的原数据对象
	 */
	protected Object convert2JsonObject(DepartmentByActorDto4MiniInfo obj) {
		JSONObject jsonObject = new JSONObject(obj);
		return jsonObject;
	}
}
