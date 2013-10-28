package cn.bc.form.struts2;

import java.util.ArrayList;
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
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.SpringUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.core.util.TemplateUtils;
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
import cn.bc.template.util.FreeMarkerUtils;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 自定义表单CRUD入口Action
 * 
 * @author lbj
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
	public AttachWidget attachsUI;
	public String json;

	/**
	 * 模板编码 如果含字符":"，则进行分拆，前面部分为编码， 后面部分为版本号，如果没有字符":"，将获取当前状态为正常的版本后格式化
	 */
	public String tpl;

	/**
	 * 自定义表单数据，使用标准的Json数据格式：[{name:"",value:"",type:"int|long|string|date|..."}
	 * ]
	 */
	public String formData;

	/**
	 * 自定义表单信息，使用标准的Json数据格式：[{id:"",uid:"",type:"",status:"",subject:"",tpl:"",
	 * authorId:"",fileDate:"",modifierId:"",modifiedDate:""}]
	 */
	public String formInfo;

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

	public Context getContext() {
		return (Context) this.session.get(Context.KEY);
	}

	public boolean isReadonly() {
		return false;
	}

	private void formatHtml(String tpl_, Map<String, Object> args) {
		@SuppressWarnings("unchecked")
		TemplateEngine<String> eng = (TemplateEngine<String>) SpringUtils
				.getBean("templateEngine");
		this.html = eng.render(tpl_, args);
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
	public String render() {
		JSONObject formInfoJO;
		String type = "";
		Long pid = 0L;
		String code = "";
		try {
			formInfoJO = new JSONObject(this.formInfo);
			type = formInfoJO.getString("type");
			pid = formInfoJO.getLong("pid");
			code = formInfoJO.getString("code");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Form f = this.formService.findForm(type, pid, code);
		JSONObject jo = new JSONObject();
		if (f == null) {
			try {
				jo.put("success", false);
				jo.put("msg", "不存在此记录");
				this.json = jo.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				jo.put("success", true);
				jo.put("msg", "存在此记录");
				jo.put("id", f.getId());
				this.json = jo.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "json";
	}

	// 创建自定义表单
	public String create() throws Exception {
		// 根据模板编码，调用相应的模板处理后输出格式化好的前台表单HTML代码
		String content = this.templateService.getContent(this.tpl);
		List<String> keys = TemplateUtils.findMarkers(content);
		Map<String, Object> args = new HashMap<String, Object>();

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

		// 设置${from_info}参数对应的值
		Json infoArgs = new Json();
		infoArgs.put("uid", uid);
		infoArgs.put("status", BCConstants.STATUS_DRAFT);
		infoArgs.put("authorId", author.getId());
		infoArgs.put("fileDate", fileDate);
		args.put("form_info", infoArgs.toString());

		addSystemContextParam(args);
		formatHtml(this.tpl, args);
		return "page";
	}

	// 保存自定义表单
	public String save() throws Exception {

		JSONObject formInfoJO = new JSONObject(this.formInfo);
		JSONArray formDataJA = new JSONArray(this.formData);

		ActorHistory actor = SystemContextHolder.get().getUserHistory();

		Form form = null;
		List<Field> fields = new ArrayList<Field>();
		// 新建保存
		if (formInfoJO.isNull("id")) {
			// 表单信息处理
			form = new Form();
			form.setPid(formInfoJO.getLong("pid"));
			form.setUid(formInfoJO.getString("uid"));
			form.setType(formInfoJO.getString("type"));
			form.setCode(formInfoJO.getString("code"));
			form.setStatus(formInfoJO.getInt("status"));
			form.setSubject(formInfoJO.getString("subject"));
			form.setTpl(formInfoJO.getString("tpl"));
			form.setAuthor(actor);
			form.setFileDate(DateUtils.getCalendar(formInfoJO
					.getString("fileDate")));
			form.setModifier(actor);
			form.setModifiedDate(Calendar.getInstance());

			// 表单字段处理
			for (int i = 0; i < formDataJA.length(); i++) {
				Field field = new Field();
				JSONObject formDataJO = (JSONObject) formDataJA.get(i);
				field.setName(formDataJO.getString("name"));
				field.setType(formDataJO.getString("type"));
				field.setValue(formDataJO.getString("value"));
				if (formDataJO.isNull("label")) {
					field.setLabel("");
				} else {
					field.setLabel(formDataJO.getString("label"));
				}
				fields.add(field);
			}
		} else {// 编辑保存
			// 表单信息处理
			form = this.formService.load(formInfoJO.getLong("id"));
			form.setModifier(actor);
			form.setModifiedDate(Calendar.getInstance());
			// 表单字段处理
			for (int i = 0; i < formDataJA.length(); i++) {
				JSONObject formDataJO = (JSONObject) formDataJA.get(i);
				Field field = this.fieldService.findByPidAndName(form,
						formDataJO.getString("name"));
				if (field != null) {
					field.setValue(formDataJO.getString("value"));
				} else {
					field = new Field();
					field.setName(formDataJO.getString("name"));
					field.setType(formDataJO.getString("type"));
					field.setValue(formDataJO.getString("value"));
					if (formDataJO.isNull("label")) {
						field.setLabel("");
					} else {
						field.setLabel(formDataJO.getString("label"));
					}
				}
				fields.add(field);
			}
		}

		JSONObject jo = new JSONObject();
		this.customFormService.save(form, fields, jo);

		jo.put("success", true);
		jo.put("msg", "保存成功");
		this.json = jo.toString();
		return "json";
	}

	// 编辑自定义表单
	public String edit() throws Exception {
		if (this.id == null) {
			throw new CoreException("Must set property id!");
		}

		// 根据自定义表单id，获取相应的自定义表单表单对象，根据表单字段参数格式化模板后生成的前台表单HTML代码
		Form form = this.formService.load(this.id);
		// 获取模板文本
		String content = this.templateService.getContent(form.getTpl());

		// 构建格式化模板参数
		Map<String, Object> args = new HashMap<String, Object>();
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
		infoJson.put("subject", subject);
		infoJson.put("id", id);
		args.put("form_info", infoJson.toString());

		// 获取表单字段属性
		List<Field> fields = this.fieldService.findList(form);
		if (fields != null && fields.size() != 0) {
			for (Field f : fields) {
				args.put(f.getName(),StringUtils.convertValueByType(f.getType(),f.getValue()));
			}
		}

		addSystemContextParam(args);
		formatHtml(this.tpl, args);
		return "page";
	}

	// 查看自定表单
	public String open() throws Exception {

		// 构建附件控件
		attachsUI = buildAttachsUI(false, true);
		return "page";
	}

	protected AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		/*
		 * // 构建附件控件 String ptype = "bulletin.main"; String puid =
		 * this.getE().getUid(); boolean readonly = forceReadonly ? true :
		 * this.isReadonly(); AttachWidget attachsUI =
		 * AttachWidget.defaultAttachWidget(isNew, readonly, isFlashUpload(),
		 * this.attachService, ptype, puid);
		 * 
		 * // 上传附件的限制 attachsUI.addExtension(getText("app.attachs.extensions"))
		 * .setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
		 * .setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		 */
		return attachsUI;
	}

	/** 通过浏览器的代理判断多文件上传是否必须使用flash方式 */
	public static boolean isFlashUpload() {
		// TODO Opera;
		return isIE();
	}

	/**
	 * 判断客户端的浏览器是否是IE浏览器
	 * 
	 * @return
	 */
	public static boolean isIE() {
		return ServletActionContext.getRequest().getHeader("User-Agent")
				.toUpperCase().indexOf("MSIE") != -1;
	}
}
