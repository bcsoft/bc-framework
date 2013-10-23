package cn.bc.form.struts2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.Context;
import cn.bc.core.util.TemplateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;
import cn.bc.form.service.CustomFormService;
import cn.bc.form.service.FieldService;
import cn.bc.form.service.FormService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.service.TemplateService;
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

	// 创建自定义表单
	public String create() throws Exception {
		// 根据模板编码，调用相应的模板处理后输出格式化好的前台表单HTML代码
		String content = this.templateService.getContent(this.tpl);
		List<String> keys = TemplateUtils.findMarkers(content);
		SystemContext context = (SystemContext) this.getContext();
		Map<String, Object> args = new HashMap<String, Object>();
		// 将模板班中的参数key替换为空值
		for (int i = 0; i < keys.size(); i++) {
			args.put(keys.get(i), "");

		}
		// 设置data-form-info的值
		Json infoArgs = new Json();
		infoArgs.put("uid", this.idGeneratorService.next(Form.ATTACH_TYPE));
		infoArgs.put("status", Form.STATUS_ENABLED);
		infoArgs.put("authorId", context.getUserHistory().getId());
		infoArgs.put("fileDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(Calendar.getInstance().getTime()));
		infoArgs.put("modifierId", context.getUserHistory().getId());
		infoArgs.put("modifiedDate",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
						.getInstance().getTime()));
		// 设置模板参数的值
		args.put("form_info", infoArgs.toString());
		args.put("form_status", Form.STATUS_DRAFT);
		args.put("form_author", context.getUserHistory().getName());
		args.put("form_fileDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(Calendar.getInstance().getTime()));
		args.put("form_modifier", context.getUserHistory().getName());
		args.put("form_modifiedDate", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
		args.put("form_isNew", true);

		// 添加系统上下文和时间戳的路径到替换参数
		args.put("htmlPageNamespace",
				context.getAttr(SystemContext.KEY_HTMLPAGENAMESPACE));
		args.put("appTs", context.getAttr(SystemContext.KEY_APPTS));

		this.html = TemplateUtils.format(content, args);
		return "page";
	}

	// 保存自定义表单
	public String save() throws Exception {

		JSONObject formInfoJO = new JSONObject(this.formInfo);
		JSONArray formDataJA = new JSONArray(this.formData);

		Form form = null;
		if (formInfoJO.isNull("id")) {
			form = new Form();
			if (formInfoJO.isNull("pid")) {
				form.setPid(-1);
			} else {
				form.setPid(formInfoJO.getInt("pid"));
			}
			form.setUid(formInfoJO.getString("uid"));
			form.setType(formInfoJO.getString("type"));
			if (formInfoJO.isNull("code")) {
				form.setCode("code is null");
			} else {
				form.setCode(formInfoJO.getString("code"));
			}
			form.setStatus(formInfoJO.getInt("status"));
			form.setSubject(formInfoJO.getString("subject"));
			form.setTpl(formInfoJO.getString("tpl"));
			form.setAuthor(SystemContextHolder.get().getUserHistory());
			form.setFileDate(Calendar.getInstance());
		} else {
			form = this.formService.load(formInfoJO.getLong("id"));
		}
		form.setModifier(SystemContextHolder.get().getUserHistory());
		form.setModifiedDate(Calendar.getInstance());

		List<Field> fields = new ArrayList<Field>();
		for (int i = 0; i < formDataJA.length(); i++) {
			Field field = null;
			JSONObject formDataJO = (JSONObject) formDataJA.get(i);
			if (formDataJO.isNull("id")) {
				field = new Field();
				field.setName(formDataJO.getString("name"));
				field.setLabel("label");
				field.setType(formDataJO.getString("type"));
				field.setValue(formDataJO.getString("value"));
			} else {
				field = this.fieldService.load(formDataJO.getLong("id"));
				field.setValue(formDataJO.getString("value"));
			}

			fields.add(field);
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
		// 根据自定义表单id，获取相应的自定义表单表单对象，根据表单字段参数格式化模板后生成的前台表单HTML代码
		Form form = this.formService.load(id);
		String content = this.templateService.getContent(form.getTpl());
		List<String> keys = TemplateUtils.findMarkers(content);
		SystemContext context = (SystemContext) this.getContext();
		Map<String, Object> args = new HashMap<String, Object>();

		// 设置data-form-info的值
		Json infoArgs = new Json();
		infoArgs.put("id", form.getId());
		infoArgs.put("uid", form.getUid());
		infoArgs.put("status", form.getStatus());
		infoArgs.put("authorId", form.getAuthor().getId());
		infoArgs.put("fileDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(form.getFileDate().getTime()));
		infoArgs.put("modifierId", form.getModifier().getId());
		infoArgs.put("modifiedDate",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(form.getModifiedDate().getTime()));
		// 设置模板参数的值
		args.put("form_info", infoArgs.toString());
		args.put("form_status", form.getStatus());
		args.put("form_author", form.getAuthor().getName());
		args.put("form_fileDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(form.getFileDate().getTime()));
		args.put("form_modifier", form.getModifier().getName());
		args.put("form_modifiedDate", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(form.getModifiedDate().getTime()));
		args.put("form_isNew", false);

		// 设置字段参数
		List<Map<String, Object>> fieldList = this.fieldService.loadFields(id);
		for (int i = 0; i < fieldList.size(); i++) {
			Map<String, Object> m = fieldList.get(i);
			args.put(m.get("name_").toString(), m.get("value_"));
		}

		// 添加系统上下文和时间戳的路径到替换参数
		args.put("htmlPageNamespace",
				context.getAttr(SystemContext.KEY_HTMLPAGENAMESPACE));
		args.put("appTs", context.getAttr(SystemContext.KEY_APPTS));

		this.html = TemplateUtils.format(content, args);
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
