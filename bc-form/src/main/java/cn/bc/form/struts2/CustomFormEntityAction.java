package cn.bc.form.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.core.lmx.CoreException;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.Context;
import cn.bc.core.exception.ConstraintViolationException;
import cn.bc.core.exception.InnerLimitedException;
import cn.bc.core.exception.NotExistsException;
import cn.bc.core.exception.PermissionDeniedException;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;
import cn.bc.form.service.CustomFormService;
import cn.bc.form.service.FieldService;
import cn.bc.form.service.FormService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.engine.TemplateEngine;
import cn.bc.template.service.TemplateService;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 自定义表单CRUD入口Action
 * 
 * @author lbj & hwx
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CustomFormEntityAction extends ActionSupport implements
		SessionAware, RequestAware {
	private static final long serialVersionUID = 1L;
	protected Map<String, Object> session;
	protected Map<String, Object> request;
	public Long id; // 自定义表单的id
	public String ids; // 批量删除的id，多个id间用逗号连接
	public String html;// 后台生成的html页面
	private CustomFormService customFormService;
	private FormService formService;
	private FieldService fieldService;
	private TemplateService templateService;
	private IdGeneratorService idGeneratorService;
	private AttachService attachService;
	private TemplateEngine templateEngine;
	public AttachWidget attachsUI;
	public String json;
	public String type; // 表单类型
	public String code; // 编码
	public long pid;
	public String subject;// 表单标题名称
	private boolean isNew;// 表单是否为新建
	/**
	 * 模板编码 如果含字符":"，则进行分拆，前面部分为编码， 后面部分为版本号，如果没有字符":"，将获取当前状态为正常的版本后格式化
	 */
	public String tpl;

	/**
	 * 自定义表单数据，使用标准的Json数据格式：{name:"",value:"",type:"int|long|string|date|..."}
	 * 
	 */
	public String formData;

	/**
	 * 自定义表单信息，使用标准的Json数据格式：{id:"",uid:"",type:"",status:"",subject:"",tpl:"",
	 * authorId:"",fileDate:"",modifierId:"",modifiedDate:""}
	 */
	public String formInfo;

	/**
	 * 创建自定义表单时的额外参数,使用标准Json数据格式[{var : value,type : valueType}]
	 */
	public String extraData;

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Autowired
	public void setCustomFormService(CustomFormService customFormService) {
		this.customFormService = customFormService;
	}

	@Autowired
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	@Autowired
	public void setFieldService(FieldService fieldService) {
		this.fieldService = fieldService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	@Autowired
	public void setTemplateEngine(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public Context getContext() {
		return (Context) this.session.get(Context.KEY);
	}

	public boolean isReadonly() {
		return false;
	}

	@SuppressWarnings("unchecked")
	private void formatHtml(String tpl_, Map<String, Object> args) {
		this.html = (String) templateEngine.render(tpl_, args);
	}

	// 增加系统上下文变量参数
	private void addSystemContextParam(Map<String, Object> args) {
		if (args == null)
			return;
		SystemContext context = SystemContextHolder.get();
		args.put("htmlPageNamespace",
				context.getAttr(SystemContext.KEY_HTMLPAGENAMESPACE));
		args.put("appTs", context.getAttr(SystemContext.KEY_APPTS));

	}

	// 渲染表单
	public String render() throws Exception {
		Form f = this.formService.findByTPC(type, pid, code);
		if (f == null) {
			return this.create();
		} else {
			return this.edit();
		}
	}

	// 创建自定义表单
	public String create() throws Exception {
		isNew = true;
		formatTpl();
		return "page";
	}

	// 查看自定义表单
	public String open() throws Exception {
		Form f = this.formService.findByTPC(type, pid, code);
		if (f == null) {
			isNew = true;
		} else {
			isNew = false;
		}
		formatTpl();
		return "page";
	}

	// 编辑自定义表单
	public String edit() throws Exception {
		isNew = false;
		formatTpl();
		return "page";
	}

	// 保存自定义表单
	public String save() throws Exception {
		JSONObject formInfoJO = new JSONObject(this.formInfo);
		JSONArray formDataJA = new JSONArray(this.formData);
		JSONObject jo = new JSONObject();
		
		this.customFormService.save(formInfoJO, formDataJA);
		jo.put("success", true);
		jo.put("msg", "保存成功");
		this.json = jo.toString();
		return "json";
	}

	// 删除自定义表单
	public String delete() throws Exception {
		Json _json = new Json();
		try {
			if (this.type != null && !this.type.equals("") && this.pid != 0L
					&& this.code != null && !this.code.equals("")) { // option不为空
				this.customFormService.delete(type, pid, code);

			} else if (this.id != null) {// 删除一条
				this.customFormService.delete(this.id);

			} else if (this.ids != null && this.ids.length() > 0) {// 删除一批
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.ids.split(","));
				this.customFormService.delete(ids);
			} else {
				throw new CoreException("must set property id or ids");
			}

			_json.put("success", true);
			_json.put("msg", getText("form.delete.success"));
			json = _json.toString();
			return "json";
		} catch (PermissionDeniedException e) {
			// 执行没有权限的操作
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (InnerLimitedException e) {
			// 删除内置对象
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (NotExistsException e) {
			// 执行没有权限的操作
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (ConstraintViolationException e) {
			// 违反约束关联引发的异常
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (Exception e) {
			// 其他异常
			dealOtherDeleteException(_json, e);
		}
		_json.put("success", false);
		json = _json.toString();

		return "json";
	}

	/**
	 * 获取删除操作的异常提示信息
	 * 
	 * @return
	 */
	protected String getDeleteExceptionMsg(Exception e) {
		if (e instanceof PermissionDeniedException) {
			return getText("exception.delete.permissionDenied");
		} else if (e instanceof InnerLimitedException) {
			return getText("exception.delete.innerLimited");
		} else if (e instanceof NotExistsException) {
			return getText("exception.delete.notExists");
		} else if (e instanceof ConstraintViolationException) {
			return getText("exception.delete.constraintViolation");
		} else {
			return e.getMessage();
		}
	}

	/**
	 * 删除操作平台没有处理的异常的默认处理
	 * 
	 * @param json
	 * @param e
	 */
	protected void dealOtherDeleteException(Json json, Exception e) {
		if ((e.getCause() != null && e.getCause() instanceof org.hibernate.exception.ConstraintViolationException)
				|| (e.getCause().getCause() != null && e.getCause().getCause() instanceof org.hibernate.exception.ConstraintViolationException)) {
			// 违反约束关联引发的异常
			json.put("msg", getText("exception.delete.constraintViolation"));
			json.put("e", e.getClass().getSimpleName());
		} else {
			// 其他异常
			json.put("msg", e.toString());
			json.put("e", e.getClass().getSimpleName());
		}
	}

	/**
	 * 格式化模板 
	 * @param form
	 * @throws JSONException
	 */
	private void formatTpl() throws JSONException {
		Form form = null;
		// 构建格式化模板参数
		Map<String, Object> args = new HashMap<String, Object>();
		if (isNew) { //表单为新建状态时
			//设置模板参数
			setCreatedTplAgs(args);
			// 设置表单信息
			String formInfoJsonStr = setCreatedFormInfo();
			args.put("form_info", formInfoJsonStr);
		} else { //表单为编辑状态时
			form = this.formService.findByTPC(type, pid,code);
			//设置模板参数
			setEditedTplAgs(args, form);
			// 设置表单信息
			String formInfoJsonStr = setEditedFormInfo(form);
			args.put("form_info", formInfoJsonStr);

			// 设置表单字段
			List<Field> fields = this.fieldService.findList(form);
			if (fields != null && fields.size() != 0) {
				for (Field f : fields) {
					if (f.getType().equals("string")) { // 如果为字段的值为string类型
						args.put(
								f.getName(),
								StringUtils.convertValueByType(f.getType(),
										f.getValue()));
					} else {// 如果为字段的值为其他类型
						if (f.getValue().equals("")) {// 如果为字段的值为其他类型，并且为空字符串
							args.put(f.getName(), StringUtils
									.convertValueByType("string", ""));
						} else {
							args.put(f.getName(), StringUtils
									.convertValueByType(f.getType(),
											f.getValue()));
						}
					}
				}
			}
		}

		// 设置额外参数
		if (this.extraData != null && !this.extraData.equals("")) {
			JSONArray extraDataJA = new JSONArray(this.extraData);
			for (int i = 0; i < extraDataJA.length(); i++) {
				JSONObject jo = extraDataJA.getJSONObject(i);
				args.put(
						jo.getString("name"),
						StringUtils.convertValueByType(jo.getString("type"),
								jo.getString("value")));
			}
		}

		addSystemContextParam(args);
		formatHtml(this.tpl, args);
	}

	/**
	 * 新建时设置模板参数
	 * 
	 * @param args
	 */
	private void setCreatedTplAgs(Map<String, Object> args) {
		SystemContext context = (SystemContext) this.getContext();
		ActorHistory author = context.getUserHistory();
		String fileDate = DateUtils.formatCalendar2Second(Calendar
				.getInstance());
		String uid = this.idGeneratorService.next(Form.ATTACH_TYPE);

		args.put("form_author", author.getName());
		args.put("form_fileDate", fileDate);
		args.put("form_uid", uid);
		args.put("form_status", BCConstants.STATUS_DRAFT);
		args.put("form_isNew", true);
		args.put("context", context);
	}

	/**
	 * 编辑时设置模板参数
	 * 
	 * @param args
	 * @param form
	 */
	private void setEditedTplAgs(Map<String, Object> args, Form form) {
		SystemContext context = (SystemContext) this.getContext();
		ActorHistory author = form.getAuthor();
		ActorHistory modifier = form.getModifier();
		String fileDate = DateUtils.formatCalendar2Second(form.getFileDate());
		String modifiedDate = DateUtils.formatCalendar2Second(form
				.getModifiedDate());
		String uid = form.getUid();
		String type = form.getType();
		String code = form.getCode();
		Long pid = form.getPid();
		String subject = form.getSubject();

		args.put("context", context);
		args.put("form_author", author.getName());
		args.put("form_fileDate", fileDate);
		args.put("form_modifier", modifier.getName());
		args.put("form_modifiedDate", modifiedDate);
		args.put("form_uid", uid);
		args.put("form_status", form.getStatus());
		args.put("form_isNew", false);
		args.put("form_type", type);
		args.put("form_code", code);
		args.put("form_pid", pid);
		args.put("form_subject", subject);
		args.put("form_id", id);
		args.put("form_tpl", tpl);
	}
	/**
	 * 新建编辑时设置表单信息
	 * 
	 * @param form
	 * @throws JSONException
	 */
	private String setEditedFormInfo(Form form) throws JSONException {
		ActorHistory author = form.getAuthor();
		ActorHistory modifier = form.getModifier();
		String fileDate = DateUtils.formatCalendar2Second(form.getFileDate());
		String modifiedDate = DateUtils.formatCalendar2Second(form
				.getModifiedDate());
		String uid = form.getUid();
		String type = form.getType();
		String code = form.getCode();
		Long pid = form.getPid();
		String subject = form.getSubject();

		// 设置${from_info}参数对应的值
		JSONObject infoJson = new JSONObject();
		infoJson.put("author", author.getName());
		infoJson.put("fileDate", fileDate);
		infoJson.put("modifier", modifier.getName());
		infoJson.put("modifiedDate", modifiedDate);
		infoJson.put("uid", uid);
		infoJson.put("status", form.getStatus());
		infoJson.put("isNew", false);
		infoJson.put("type", type);
		infoJson.put("code", code);
		infoJson.put("pid", pid);
		infoJson.put("tpl", tpl);
		infoJson.put("subject", subject);
		infoJson.put("id", id);

		return infoJson.toString();
	}

	/**
	 * 新建时设置表单信息
	 * 
	 * @throws JSONException
	 */
	private String setCreatedFormInfo() {
		SystemContext context = (SystemContext) this.getContext();
		ActorHistory author = context.getUserHistory();
		String fileDate = DateUtils.formatCalendar2Second(Calendar
				.getInstance());
		String uid = this.idGeneratorService.next(Form.ATTACH_TYPE);

		// 设置${from_info}参数对应的值
		Json infoJson = new Json();
		infoJson.put("subject", subject);
		infoJson.put("type", type);
		infoJson.put("code", code);
		infoJson.put("pid", pid);
		infoJson.put("tpl", tpl);
		infoJson.put("uid", uid);
		infoJson.put("isNew", true);
		infoJson.put("status", BCConstants.STATUS_DRAFT);
		infoJson.put("authorId", author.getId());
		infoJson.put("fileDate", fileDate);

		return infoJson.toString();
	}
}
