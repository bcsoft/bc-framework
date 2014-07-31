package cn.bc.form.struts2;

import cn.bc.core.util.JsonUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;
import cn.bc.form.service.FormService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.engine.TemplateEngine;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import com.opensymphony.xwork2.ActionSupport;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 渲染业务对象表单Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RenderFormAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	public String type;		// 表单业务类别
	public String code;		// 表单业务编码
	public Long pid;		// 表单业务ID
	public Float ver;		// 版本
	public String subject;	// 表单标题名称
	public String tpl;		// 模板编码
	public String role;		// 对表单进行编辑需要的角色，使用"|"连接多个角色编码代表或关系，使用"+"连接多个角色编码代表和关系
	/**
	 * 创建自定义表单时的额外参数,使用标准Json数组格式[{name: "n", value: "1", type: "int"}, ...]
	 */
	public String data;
	public boolean readonly;    // 是否强制只读显示，默认自动根据 role 参数的值判断
	public boolean replace;        // 如果现存表单中含有data参数中的数据，是否优先使用data中的数据，默认false

	public String json;        // ajax返回数据封装
	public String html;        //返回的html

	@Autowired
	protected FormService formService;
	@Autowired
	protected TemplateEngine templateEngine;
	@Autowired
	private IdGeneratorService idGeneratorService;

	/** 获取页面的基本配置参数 */
	protected PageOption getPageOption(boolean readonly) {
		PageOption pageOption = new PageOption();
		pageOption.setWidth(400).setMinWidth(300);
		pageOption.setHeight(300).setMinHeight(250);

		this.addPageButton(pageOption, readonly);
		return pageOption;
	}

	/** 获取页面的命名空间路径 */
	protected String getPageNamespace() {
		return null;
	}

	/**
	 * 为页面添加操作按钮
	 * @param pageOption 窗口配置
	 * @param readonly 只读标记
	 */
	protected void addPageButton(PageOption pageOption, boolean readonly) {
		// 默认添加确认按钮
		if(!readonly) pageOption.addButton( new ButtonOption(getText("label.ok"), null, "bc.cform.onOk"));
	}

	// 渲染表单
	public String render() throws Exception {
		// 判断编辑权限
		boolean readonly = this.readonly || !this.hasRole();

		// 获取现有表单，不存在就新建一个
		Form form = this.formService.load(type, code, pid, ver);
		if (form == null) {
			form = this.createForm();
		}else{
			// 传入参数优先的处理
			if(this.replace){
				replaceFormProperty(form, readonly);
			}
		}

		// 构建格式化模板需要的参数
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("form", form);// 使用form关键字记录表单的全局参数值
		args.put("readonly", readonly);// 标记是否表单只读

		//-- 现有的表单字段参数
		Map<String, Object> fields = this.convertFieldsValue(form.getFields());

		//-- 额外传入的参数
		Map<String, Object> extraFiledParams = new HashMap<String, Object>();
		if (this.data != null && !this.data.isEmpty()) {
			Collection<Map<String, Object>> extraDataConfig = JsonUtils.toCollection(this.data);
			for (Map<String, Object> m : extraDataConfig) {
				extraFiledParams.put((String) m.get("name")
						, StringUtils.convertValueByType((String) m.get("type"), (String) m.get("value")));
			}
		}
		this.addExtraFieldParams(extraFiledParams, readonly);

		//-- 按是否传入参数优先合并额外传入的参数
		boolean hasKeyValue;
		for (Map.Entry<String, Object> e : extraFiledParams.entrySet()) {
			hasKeyValue = fields.containsKey(e.getKey());
			if(hasKeyValue){
				if(this.replace) fields.put(e.getKey(), e.getValue());// extraData优先的处理
			}else{
				fields.put(e.getKey(), e.getValue());
			}
		}

		// 存入表单字段的数据到上下文中
		for(Map.Entry<String, Object> ff : fields.entrySet()){
			args.put(ff.getKey(), ff.getValue());
		}

		// 添加系统上下文参数
		this.addContextParams(args, readonly);

		// 格式化模板
		this.html = (String) this.templateEngine.render(form.getTpl(), args);

		return "page";
	}

	/**
	 * 添加系统上下文参数
	 * @param args 参数添加到的地方
	 * @param readonly 只读标记
	 */
	protected Map<String, Object> addContextParams(Map<String, Object> args, boolean readonly) {
		Map<String, Object> ctx = new HashMap<String, Object>();
		SystemContext context = SystemContextHolder.get();
		ctx.put("role", this.role);
		ctx.put("readonly", readonly);
		ctx.put("replace", this.replace);
		ctx.put("ctx", context);
		ctx.put("root", context.getAttr(SystemContext.KEY_HTMLPAGENAMESPACE));
		ctx.put("ts", context.getAttr(SystemContext.KEY_APPTS));

		ctx.put("pageOption", this.getPageOption(readonly));
		ctx.put("jscss", this.getPageJsCss((String) context.getAttr(SystemContext.KEY_HTMLPAGENAMESPACE), readonly));
		ctx.put("initMethod", this.getPageInitMethod());
		ctx.put("namespace", this.getPageNamespace());

		args.put("ctx", ctx);

		return ctx;
	}

	/** 获取页面的初始化方法 */
	protected String getPageInitMethod() {
		return "bc.cform.init";
	}

	/** 获取页面需要添加的js、css文件 */
	protected String getPageJsCss(String contextPath, boolean readonly) {
		Collection<String> jscss = new ArrayList<String>();
		addHtmlPageJsCss(jscss, contextPath, readonly);
		return org.springframework.util.StringUtils.collectionToCommaDelimitedString(jscss);
	}

	/**
	 * 向页面添加额外的js、css文件
	 * @param jscss 已初始化好的集合
	 * @param contextPath 上下文路径
	 */
	protected void addHtmlPageJsCss(Collection<String> jscss, String contextPath, boolean readonly) {
		// 默认添加自己的API
		jscss.add(contextPath + "/bc/form/api.js");
	}

	/**
	 * 添加额外的参数
	 * @param extraFiledParams 参数添加到的地方
	 * @param readonly 只读标记
	 */
	protected void addExtraFieldParams(Map<String, Object> extraFiledParams, boolean readonly) {
		// Do nothing
	}

	/**
	 * 传入参数优先的处理
	 * @param form 表单
	 * @param readonly 只读标记
	 */
	protected void replaceFormProperty(Form form, boolean readonly) {
		form.setTpl(this.tpl);
		form.setSubject(this.subject);
	}

	// 初始化一个新的Form对象
	protected Form createForm() {
		Form form = new Form();
		form.setType(this.type);
		form.setCode(this.code);
		form.setPid(this.pid);
		form.setSubject(this.subject);
		form.setUid(this.idGeneratorService.next(Form.ATTACH_TYPE));
		form.setVer(this.ver == null ? 1 : this.ver);// 默认版本1
		form.setTpl(this.tpl);
		form.setStatus(Form.STATUS_ENABLED);

		SystemContext context = SystemContextHolder.get();
		form.setAuthor(context.getUserHistory());
		form.setFileDate(Calendar.getInstance());
		form.setModifier(form.getAuthor());
		form.setModifiedDate(form.getFileDate());

		return form;
	}

	// 转换表单字段的值
	protected Map<String, Object> convertFieldsValue(List<Field> fields) {
		Map<String, Object> m = new HashMap<String, Object>();
		if (fields == null || fields.isEmpty())
			return m;

		for (Field f : fields) {
			m.put(f.getName(), StringUtils.convertValueByType(f.getType(), f.getValue()));
		}
		return m;
	}

	// 判断当前用户是否满足额外的角色要求
	public boolean hasRole() {
		if (this.role == null || this.role.isEmpty()) {
			return false;
		}

		// 处理 | 和 +
		SystemContext context = SystemContextHolder.get();
		if (this.role.contains("|")) {// 任意其中一个角色的情况
			return !context.hasAnyOneRole(this.role.replace("|", ","));
		} else {// 必须拥有全部角色的情况
			String[] roles = this.role.split("\\+");
			for (String r : roles) {
				if (!context.hasAnyRole(r)) {
					return false;
				}
			}
			return true;
		}
	}

	// 删除表单
	public String delete() throws Exception {
		JSONObject json = new JSONObject();
		try{
			Assert.hasText(this.type, "没有指定 type 参数！");
			Assert.hasText(this.code, "没有指定 code 参数！");
			Assert.notNull(this.pid, "没有指定 pid 参数！");
			Assert.notNull(this.ver, "没有指定 ver 参数！");

			this.formService.delete(this.type, this.code, this.pid, this.ver);

			json.put("success", true);
			json.put("msg", "删除成功！");
		}catch (Exception e){
			json.put("success", false);
			json.put("msg", e.getMessage());
		}
		this.json = json.toString();
		return "json";
	}

	// 保存表单
	public String save() throws Exception {
		JSONObject json = new JSONObject();
		try{
			Assert.hasText(this.type, "没有指定 type 参数！");
			Assert.hasText(this.code, "没有指定 code 参数！");
			Assert.notNull(this.pid, "没有指定 pid 参数！");
			Assert.notNull(this.ver, "没有指定 ver 参数！");

			Map<String, Object> data = JsonUtils.toMap(this.data);
			Form form = this.formService.save(this.type, this.code, this.pid, this.ver, data);

			json.put("success", true);
			json.put("id", form.getId());
			json.put("msg", "保存成功！");
		}catch (Exception e){
			json.put("success", false);
			json.put("msg", e.getMessage());
		}
		this.json = json.toString();
		return "json";
	}
}
