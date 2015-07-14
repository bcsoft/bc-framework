/**
 *
 */
package cn.bc.web.struts2;

import cn.bc.BCConstants;
import cn.bc.Context;
import cn.bc.core.Entity;
import cn.bc.core.Page;
import cn.bc.core.SetEntityClass;
import cn.bc.core.exception.*;
import cn.bc.core.service.CrudService;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Entity的CRUD通用Action
 *
 * @author dragon
 */
public class EntityAction<K extends Serializable, E extends Entity<K>> extends
		ActionSupport implements SetEntityClass<E>, SessionAware, RequestAware {
	private static final long serialVersionUID = 1L;
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private CrudService<E> crudService;
	private Long id;
	private E e; // entity的简写
	private List<? extends Object> es; // entities的简写,非分页页面用
	private String ids; // 批量删除的id，多个id间用逗号连接
	private Component html; // 后台生成的html页面
	private Class<E> entityClass;
	private Page<? extends Object> page; // 分页页面用
	public String search; // 搜索框输入的文本
	public String contextPath; // 系统部署的路径，如"/bc"
	public String sort; // grid的排序配置，格式为"filed1 asc,filed2 desc,..."
	protected Map<String, Object> session;
	protected Map<String, Object> request;
	/**
	 * 表单页面 data-option 属性的配置
	 */
	public PageOption pageOption;
	public long ts = new Date().getTime();// 时间戳

	/**
	 * @deprecated 请使用 pageOption 代替
	 */
	public PageOption getFormPageOption() {
		return pageOption;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public Context getContext() {
		return (Context) this.session.get(Context.KEY);
	}

	@SuppressWarnings("unchecked")
	public EntityAction() {
		// 这个需要子类中指定T为实际的类才有效(指定接口也不行的)
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getActualTypeArguments()[1];
			if (type instanceof Class) {
				this.entityClass = (Class<E>) type;
				if (logger.isInfoEnabled())
					logger.info("auto judge entityClass to '" + this.entityClass + "' [" + this.getClass() + "]");
			}
		}

		if (ServletActionContext.getRequest() != null)
			contextPath = ServletActionContext.getRequest().getContextPath();
	}

	/**
	 * 判断表单是否只读的方法，需要权限控制时由基类复写
	 *
	 * @return
	 */
	public boolean isReadonly() {
		return false;
	}

	public Page<? extends Object> getPage() {
		return page;
	}

	public void setPage(Page<? extends Object> page) {
		this.page = page;
	}

	protected Class<? extends E> getEntityClass() {
		return this.entityClass;
	}

	protected String getEntityConfigName() {
		return this.getEntityClass().getSimpleName();
	}

	public void setEntityClass(Class<E> clazz) {
		this.entityClass = clazz;
	}

	public CrudService<E> getCrudService() {
		return crudService;
	}

	public void setCrudService(CrudService<E> crudService) {
		this.crudService = crudService;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public E getE() {
		if (e == null) {
			// 这是使用泛型没有办法之举：ERROR InstantiatingNullHandler Could not create
			// and/or set value back on to object
			// at
			// com.opensymphony.xwork2.spring.SpringObjectFactory.buildBean(SpringObjectFactory.java:169)
			try {
				e = this.getEntityClass().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return e;
	}

	public void setE(E entity) {
		this.e = entity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<? extends Object> getEs() {
		return es;
	}

	public void setEs(List<? extends Object> entities) {
		this.es = entities;
	}

	public Component getHtml() {
		return html;
	}

	public void setHtml(Component page) {
		this.html = page;
	}

	public String execute() throws Exception {
		logger.debug("do in method execute.");
		return "success";
	}

	// 新建表单
	public String create() throws Exception {
		// 初始化E
		this.setE(createEntity());

		// 初始化表单的配置信息
		this.pageOption = buildFormPageOption(true);

		// 初始化表单的其他配置
		this.initForm(true);

		this.afterCreate(this.getE());

		return "form";
	}

	protected E createEntity() {
		return this.getCrudService().create();
	}

	/**
	 * 在调用create初始化entity之后、调用buildFormPageOption方法之前调用的方法，给基类一个扩展的处理
	 */
	protected void afterCreate(E entity) {
	}

	// 编辑表单
	public String edit() throws Exception {
		e = loadEntity();
		this.pageOption = buildFormPageOption(true);

		// 初始化表单的其他配置
		this.initForm(true);

		this.afterEdit(e);
		return "form";
	}

	/**
	 * 根据请求的实体标识参数加载实体对象
	 */
	protected E loadEntity() {
		return this.getCrudService().load(this.getId());
	}

	/**
	 * 初始化表单的其他配置，如下拉框列表等
	 *
	 * @param editable 是否按照可编辑方式执行表单的初始化：create、edit-true,open-false
	 */
	protected void initForm(boolean editable) throws Exception {
	}

	/**
	 * 在调用edit初始化entity之后、调用buildFormPageOption方法之前调用的方法，给基类一个扩展的处理
	 */
	protected void afterEdit(E entity) {

	}

	// 只读表单
	public String open() throws Exception {
		e = loadEntity();

		// 强制表单只读
		this.pageOption = buildFormPageOption(false);

		// 初始化表单的其他配置
		this.initForm(false);

		this.afterOpen(e);
		return "formr";
	}

	/**
	 * 在调用open初始化entity之后、调用buildFormPageOption方法之前调用的方法，给基类一个扩展的处理
	 */
	protected void afterOpen(E entity) {
	}

	/**
	 * 通过浏览器的代理判断多文件上传是否必须使用flash方式
	 */
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

	// X-Requested-With:

	/**
	 * 判断当前请求是否是ajax请求
	 *
	 * @return
	 */
	protected boolean isAjaxRequest() {
		// TODO IE
		return "XMLHttpRequest".equalsIgnoreCase(ServletActionContext.getRequest().getHeader("X-Requested-With"));
	}

	// 保存
	public String save() throws Exception {
		this.beforeSave(e);
		this.getCrudService().save(e);
		this.afterSave(e);
		return "saveSuccess";
	}

	/**
	 * 在调用save之前调用的方法，给基类一个扩展的处理
	 */
	protected void beforeSave(E entity) {
	}

	/**
	 * 在调用save之后调用的方法，给基类一个扩展的处理
	 */
	protected void afterSave(E entity) {
	}

	public String json;

	// 删除
	public String delete() throws Exception {
		Json _json = new Json();
		try {
			if (this.getId() != null) {// 删除一条
				this.getCrudService().delete(this.getId());
			} else {// 删除一批
				if (this.getIds() != null && this.getIds().length() > 0) {
					Long[] ids = cn.bc.core.util.StringUtils
							.stringArray2LongArray(this.getIds().split(","));
					this.getCrudService().delete(ids);
				} else {
					throw new CoreException("must set property id or ids");
				}
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
	 */
	protected void dealOtherDeleteException(Json json, Exception e) {
		if ((e.getCause() != null && e.getCause().getClass().getSimpleName().equals("ConstraintViolationException"))
				|| (e.getCause().getCause() != null && e.getCause().getCause().getClass().getSimpleName().equals("ConstraintViolationException"))) {
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
	 * 构建表单页面的对话框初始化配置
	 *
	 * @deprecated 请使用 buildPageOption(boolean) 代替
	 */
	protected PageOption buildFormPageOption() {
		return buildPageOption(false);
	}

	/**
	 * @deprecated 请使用 buildPageOption(boolean) 代替
	 */
	protected PageOption buildFormPageOption(boolean editable) {
		return buildPageOption(editable);
	}

	/**
	 * 构建表单的对话框初始化配置
	 *
	 * @param editable 是否为可编辑表单的配置,当调用create、edit方法时为true，调用open方法时为false
	 */
	protected PageOption buildPageOption(boolean editable) {
		PageOption pageOption = new PageOption().setMinWidth(250).setMinHeight(200).setModal(false);

		if (this.useFormPrint()) pageOption.setPrint("default.form");

		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", readonly);
		} else {
			pageOption.put("readonly", true);
		}

		// 添加按钮
		buildFormPageButtons(pageOption, editable);

		return pageOption;
	}

	/**
	 * @deprecated 请使用 buildPageButtons(PageOption, boolean) 代替
	 */
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		buildPageButtons(pageOption, editable);
	}

	/**
	 * 添加表单操作按钮，默认为保存按钮
	 *
	 * @param pageOption buildPageOption(editable)方法初始化好的对象
	 * @param editable   是否为可编辑表单的配置,当调用create、edit方法时为true，调用open方法时为false
	 */
	protected void buildPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();

		if (this.useFormPrint()) {
			// 添加打印按钮
			pageOption.addButton(this.getDefaultPrintButtonOption());
		}

		if (editable && !readonly) {
			// 添加默认的保存按钮
			pageOption.addButton(this.getDefaultSaveButtonOption());
		}
	}

	/**
	 * 是否使用表单打印功能
	 */
	protected boolean useFormPrint() {
		return false;
	}

	/**
	 * 创建默认的表单保存按钮
	 */
	protected ButtonOption getDefaultSaveButtonOption() {
		return new ButtonOption(getText("label.save"), "save").setId("bcSaveBtn");
	}

	/**
	 * 创建默认的表单打印按钮
	 */
	protected ButtonOption getDefaultPrintButtonOption() {
		return new ButtonOption(getText("label.print"), "print").setId("bcPrintBtn");
	}

	/**
	 * 获取访问action的前缀。 struts配置文件中package节点的namespace属性去除最后面的bean名称的部分
	 * 如namespace="/bc/duty"，则这里应返回"/bc"
	 *
	 * @deprecated 使用 getPageNamespace() 的默认行为代替
	 */
	protected String getActionPathPrefix() {
		return "/bc";
	}

	/**
	 * 获取 struts 的 XML 配置文件中此 Action 配置的 namespace 的值相对于web应用上下文路径的相对路径
	 * <p>如 &lt;package name="XXX" extends="XXX" namespace="/bc/actor"&gt;...&lt;/package&gt;
	 * 则此方法返回 "bc/actor" 而不是 "/bc/actor"</p>
	 *
	 * @return 包路径
	 */
	public String getActionNamespace() {
		String ns = ServletActionContext.getActionMapping().getNamespace();
		if (ns.startsWith("/")) return ns.substring(1);
		else return ns;
	}

	/**
	 * 是否启用兼容模式
	 * <p>子类不想使用兼容模式时，推荐在属性配置文件中添加 quirksMode=false 配置。
	 * 也可以复写此方法返回 false。新增此方法是为了使旧代码不需要修改也可以正常使用。
	 * </p>
	 *
	 * @return 如果子类没有复写、属性文件有没有配置quirksMode则默认返回true
	 */
	protected boolean isQuirksMode() {
		return !"false".equals(getText("quirksMode"));
	}

	/**
	 * 页面命名空间
	 */
	public String getPageNamespace() {
		if (isQuirksMode())
			return getContextPath() + this.getActionPathPrefix() + "/" + StringUtils.uncapitalize(getEntityConfigName());
		else
			return getActionNamespace();
	}

	/**
	 * 对话框的标题
	 */
	protected String getPageTitle() {
		return this.getText(StringUtils.uncapitalize(getEntityConfigName()) + ".title");
	}

	/**
	 * 页面引用的js css文件配置
	 *
	 * @desc 不要复写此方法，应使用 addJsCss(List<String> container) 为页面添加 js、css 文件的加载
	 */
	public String getPageJsCss() {
		List<String> container = new ArrayList<>();
		addJsCss(container);
		if (!container.isEmpty()) return new JSONArray(container).toString();
		else return null;
	}

	/**
	 * 向页面添加额外的js、css文件
	 * <p>调用此方法生成的data-js属性为json数组格式，页面将使用requireJs加载这些js、css文件</p>
	 *
	 * @param container 已初始化好的容器
	 */
	protected void addJsCss(List<String> container) {
		// Do nothing
	}

	/**
	 * 获取访问该ation的上下文路径
	 */
	protected String getContextPath() {
		return ServletActionContext.getRequest().getContextPath();
	}

	/**
	 * 页面加载后的调用js初始化方法
	 *
	 * @deprecated 请直接写在页面的 data-initMethod 属性内
	 */
	protected String getIniMethod() {
		return null;
	}

	/**
	 * 页面需要另外加载的css文件，逗号连接多个文件
	 *
	 * @deprecated 请直接写在页面的 data-js 属性内
	 */
	protected String getCss() {
		return null;
	}

	/**
	 * 页面需要另外加载的js文件，逗号连接多个文件
	 *
	 * @deprecated 请直接写在页面的 data-js 属性内
	 */
	protected String getJs() {
		return null;
	}

	/**
	 * 获取Entity的状态值转换列表
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> statuses = new LinkedHashMap<>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("entity.status.enabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("entity.status.disabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DELETED),
				getText("entity.status.deleted"));
		return statuses;
	}
}