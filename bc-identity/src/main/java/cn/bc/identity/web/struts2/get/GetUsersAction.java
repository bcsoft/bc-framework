package cn.bc.identity.web.struts2.get;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.service.ActorService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * AJAX获取用户Action
 * 
 * @author hwx
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class GetUsersAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory.getLog("GetUsersAction");
	private ActorService actorService;
	public String group;// 指定岗位的编码
	public String status = String.valueOf(BCConstants.STATUS_ENABLED) + ","
			+ String.valueOf(BCConstants.STATUS_DISABLED); // 用户的状态，多个用逗号连接sx
	public String json;

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}

	@Override
	public String execute() throws Exception {
		JSONArray json = new JSONArray();
		
		Long groupId = null;
		if(this.group != null && !this.group.trim().equals("")) {
			Actor g = this.actorService.loadByCode(group);
			groupId = g.getId();
		}
		
		// 获取数据
		List<Actor> data = this.actorService
				.findFollowerWithName(groupId, null,
						new Integer[] { ActorRelation.TYPE_BELONG },
						new Integer[] { Actor.TYPE_USER },
						new Integer[] { BCConstants.STATUS_ENABLED });
		convert2JsonArray(json, data);
		if (logger.isDebugEnabled())
			logger.debug("json=" + json);

		// 返回结果
		this.json = json.toString();
		return "json";
	}

	/**
	 * 转换数据为JsonArray格式
	 * 
	 * @param array
	 * @param data
	 */
	protected void convert2JsonArray(JSONArray array,
			List<Actor> data) {
		for (Actor d : data) {
			array.put(this.convert2JsonObject(d));
		}
	}

	/**
	 * 转换对象为JsonObject格式
	 * 
	 * @param obj
	 * @return
	 */
	protected Object convert2JsonObject(Actor obj) {
		return new JSONObject(obj);
	}
}
