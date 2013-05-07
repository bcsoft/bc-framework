package cn.bc.subscribe.web.struts2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.subscribe.domain.Subscribe;
import cn.bc.subscribe.domain.SubscribeActor;
import cn.bc.subscribe.service.SubscribeActorService;
import cn.bc.subscribe.service.SubscribeService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 订阅表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SubscribeAction extends FileEntityAction<Long, Subscribe> {
	private static final long serialVersionUID = 1L;
	
	public Map<String, String> statusesValue;

	private SubscribeService subscribeService;
	private SubscribeActorService subscirbeActorService;
	private ActorService actorService;

	@Autowired
	public void setSubscribeService(SubscribeService subscribeService) {
		this.subscribeService = subscribeService;
		this.setCrudService(subscribeService);
	}

	@Autowired
	public void setSubscirbeActorService(
			SubscribeActorService subscirbeActorService) {
		this.subscirbeActorService = subscirbeActorService;
	}

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}


	public List<SubscribeActor> actors;

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：订阅管理角色或系统管理员
		return !context.hasAnyRole(getText("key.role.bc.subscribe"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void afterCreate(Subscribe entity) {
		super.afterCreate(entity);
		// 设置状态为草稿
		entity.setStatus(BCConstants.STATUS_DRAFT);
	}

	@Override
	protected void afterEdit(Subscribe entity) {
		super.afterEdit(entity);

		entity.setType(Subscribe.TYPE_EMAIL);
		
		this.actors = this.subscirbeActorService.findList(entity);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(600)
				.setMinHeight(200).setMaxHeight(600);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		// 非编辑状态没有任何操作按钮
		if (!editable)
			return;

		if (this.getE().isNew()||this.getE().getStatus() == BCConstants.STATUS_DRAFT) {
			// 保存为草稿
			pageOption.addButton(new ButtonOption(getText("subscribe.save.draft"),"save").setId("bcSaveBtn"));
			// 发布
			pageOption.addButton(new ButtonOption(getText("subscribe.release"),
					null, "bc.subscribeForm.release"));
		} else {
			// 保存
			pageOption.addButton(this.getDefaultSaveButtonOption());
		}
	}

	@Override
	public String save() throws Exception {
		//唯一性的检测
		boolean isUnique = this.subscribeService.isUnique(this.getE()
				.getEventCode(), this.getE().isNew() ? null : this.getE()
				.getId());
		
		if(!isUnique){
			Json json=new Json();
			json.put("success", false);
			json.put("msg", getText("subscribe.save.success.unique.false.msg"));
			this.json=json.toString();
			return "json";
		}

		return super.save();
	}

	/**
	 * 状态值转换列表：草稿|正常|禁用|全部
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_DRAFT),
				getText("subscribe.status.draft"));
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("subscribe.status.enabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("subscribe.status.disable"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}
	
	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		// 状态列表
		statusesValue = this.getStatuses();
	}
	
	//状态 默认为正常
	public Integer status=BCConstants.STATUS_ENABLED;
	
	public String update() throws Exception{
		if(this.getId() == null ){
			throw new CoreException("must set id property!");
		}
		
		if(!(status==BCConstants.STATUS_ENABLED||status==BCConstants.STATUS_DISABLED)){
			throw new CoreException("must set status error!");
		}
		
		Json json=new Json();
		Subscribe s=this.subscribeService.load(this.getId());
		s.setStatus(status);
		this.subscribeService.save(s);
		json.put("success", true);
		this.json=json.toString();
		return "json";
	}

	// 用户添加订阅
	public String add4personal() throws Exception {
		Json _json = new Json();
		
		SystemContext context = SystemContextHolder.get();
		// 当前用户
		Actor actor=context.getUser();

		// 声明订阅
		Subscribe subscribe;

		if (this.getId() != null) {// 添加一个
			subscribe = this.subscribeService.load(this.getId());
			// 验证是否已添加
			if (this.subscirbeActorService.find4aidpid(actor.getId(),this.getId()) != null) {
				_json.put("success", false);
				_json.put("msg","订阅："+subscribe.getSubject()+"，已添加。");
				json = _json.toString();
				return "json";
			}
			
			this.subscribeService.doAddActor4Personal(subscribe);
		} else {// 添加一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));

				List<Subscribe> subscribes = new ArrayList<Subscribe>();

				for (Long id : ids) {
					subscribe = this.subscribeService.load(id);
					if (subscribe != null) {
						// 验证是否已添加
						if (this.subscirbeActorService.find4aidpid(actor.getId(),id) != null) {
							_json.put("success", false);
							_json.put("msg","订阅："+subscribe.getSubject()+"，已添加。");
							json = _json.toString();
							return "json";
						}
						
						subscribes.add(subscribe);
					}
				}
				this.subscribeService.doAddActor4Personal(subscribes);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		_json.put("success", true);
		_json.put("msg", getText("subscribeActor.create.success"));
		json = _json.toString();
		return "json";
	}

	// 用户删除订阅
	public String delete4personal() throws Exception {
		Json _json = new Json();

		// 声明订阅
		Subscribe subscribe;

		if (this.getId() != null) {// 删除一个
			subscribe = this.subscribeService.load(this.getId());
			this.subscribeService.doDeleteActor4Personal(subscribe);
		} else {// 删除一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));

				List<Subscribe> subscribes = new ArrayList<Subscribe>();

				for (Long id : ids) {
					subscribe = this.subscribeService.load(id);
					if (subscribe != null) {
						subscribes.add(subscribe);
					}
				}
				this.subscribeService.doDeleteActor4Personal(subscribes);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		_json.put("success", true);
		_json.put("msg", getText("form.delete.success"));
		json = _json.toString();
		return "json";
	}

	public Long actorId;
	public String actorIds;

	// 管理员添加订阅
	public String add4manager() throws Exception {
		if (isReadonly()) {
			throw new CoreException("permission denied");
		}
		
		// 声明订阅
		Subscribe subscribe;
		if (this.getId() != null) {
			subscribe = this.subscribeService.load(this.getId());
		}else{
			throw new CoreException("must set property id");
		}
		
		Json _json = new Json();
		Actor actor;
		if (this.actorId != null) {// 添加一个
			actor = this.actorService.load(this.actorId);
			// 验证是否已添加
			if (this.subscirbeActorService.find4aidpid(this.actorId,this.getId()) != null) {
				_json.put("success", false);
				_json.put("msg", "<b>"+actor.getName()+"</b>"+getText("subscribePersonal.add.false"));
				json = _json.toString();
				return "json";
			}
			
			this.subscribeService.doAddActor4Manager(subscribe, actor);
		} else {// 添加一批
			if (this.actorIds != null && this.actorIds.length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.actorIds.split(","));

				List<Actor> actors = new ArrayList<Actor>();

				for (Long id : ids) {
					actor = this.actorService.load(id);
					if (actor != null) {
						
						// 验证是否已添加
						if (this.subscirbeActorService.find4aidpid(actor.getId(),this.getId()) != null) {
							_json.put("success", false);
							_json.put("msg", "<b>"+actor.getName()+"</b>"+getText("subscribePersonal.add.false"));
							json = _json.toString();
							return "json";
						}
						
						actors.add(actor);
					}
				}
				
				this.subscribeService.doAddActor4Manager(subscribe, actors);
			} else {
				throw new CoreException("must set property actorId or actorIds");
			}
		}

		_json.put("success", true);
		_json.put("msg", getText("subscribePersonal.add.success"));
		json = _json.toString();
		return "json";
	}

	// 管理员删除订阅
	public String delete4manager() throws Exception {
		if (isReadonly()) {
			throw new CoreException("permission denied");
		}

		// 声明订阅
		Subscribe subscribe;
		if (this.getId() != null) {
			subscribe = this.subscribeService.load(this.getId());
		}else{
			throw new CoreException("must set property id");
		}
		
		Json _json = new Json();
		Actor actor;
		if (this.actorId != null) {// 删除一个
			actor = this.actorService.load(this.actorId);
			this.subscribeService.doDeleteActor4Manager(subscribe, actor);
		} else {// 删除一批
			if (this.actorIds != null && this.actorIds.length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.actorIds.split(","));

				List<Actor> actors = new ArrayList<Actor>();

				for (Long id : ids) {
					actor = this.actorService.load(id);
					if (actor != null) {
						actors.add(actor);
					}
				}
				
				this.subscribeService.doDeleteActor4Manager(subscribe, actors);
			} else {
				throw new CoreException("must set property actorId or actorIds");
			}
		}

		_json.put("success", true);
		_json.put("msg", getText("form.delete.success"));
		json = _json.toString();
		return "json";
	}
}