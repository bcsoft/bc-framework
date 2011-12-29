/**
 * 
 */
package cn.bc.web.struts2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.util.StringUtils;

import cn.bc.Context;
import cn.bc.core.Entity;
import cn.bc.core.Page;
import cn.bc.core.RichEntity;
import cn.bc.core.SetEntityClass;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.GridExporter;
import cn.bc.web.ui.html.grid.GridFooter;
import cn.bc.web.ui.html.grid.GridHeader;
import cn.bc.web.ui.html.grid.IdColumn;
import cn.bc.web.ui.html.grid.PageSizeGroupButton;
import cn.bc.web.ui.html.grid.SeekGroupButton;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.ListPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.util.WebUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Entity的CRUD通用Action
 * 
 * @author dragon
 * 
 */
public class EntityAction<K extends Serializable, E extends Entity<K>> extends
		ActionSupport implements SetEntityClass<E>, SessionAware, RequestAware {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(getClass());

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
	public PageOption formPageOption;// 表单页面的Option配置
	public long ts = new Date().getTime();// 时间戳

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
					logger.info("auto judge entityClass to '"
							+ this.entityClass + "' [" + this.getClass() + "]");
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
		this.setE(this.getCrudService().create());

		// 初始化表单的配置信息
		this.formPageOption = buildFormPageOption(true);

		// 初始化表单的其他配置
		this.initForm(true);
		
		this.afterCreate(this.getE());

		return "form";
	}

	/**
	 * 在调用create初始化entity之后、调用buildFormPageOption方法之前调用的方法，给基类一个扩展的处理
	 */
	protected void afterCreate(E entity) {
	}

	// 编辑表单
	public String edit() throws Exception {
		e = this.getCrudService().load(this.getId());
		this.formPageOption = buildFormPageOption(true);

		// 初始化表单的其他配置
		this.initForm(true);

		this.afterEdit(e);
		return "form";
	}

	/**
	 * 初始化表单的其他配置，如下拉框列表等
	 * 
	 * @param editable
	 *            是否按照可编辑方式执行表单的初始化：create、edit-true,open-false
	 */
	protected void initForm(boolean editable) {
	}

	/**
	 * 在调用edit初始化entity之后、调用buildFormPageOption方法之前调用的方法，给基类一个扩展的处理
	 */
	protected void afterEdit(E entity) {

	}

	// 只读表单
	public String open() throws Exception {
		e = this.getCrudService().load(this.getId());

		// 强制表单只读
		this.formPageOption = buildFormPageOption(false);

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

	// X-Requested-With:

	/**
	 * 判断当前请求是否是ajax请求
	 * 
	 * @return
	 */
	protected boolean isAjaxRequest() {
		// TODO IE
		return "XMLHttpRequest".equalsIgnoreCase(ServletActionContext
				.getRequest().getHeader("X-Requested-With"));
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
		} catch (JpaSystemException e) {
			// 处理违反外键约束导致的删除异常，提示用户因关联而无法删除
			throw new CoreException("JpaSystemException");
		}
		return "deleteSuccess";
	}

	// 获取列表视图页面----无分页
	@Deprecated
	public String list() throws Exception {
		// 根据请求的条件查找信息
		this.es = this.findList();

		// 构建页面的html
		this.setHtml(buildHtml4List());

		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
	}

	// 获取列表视图页面----分页
	@Deprecated
	public String paging() throws Exception {
		// 首次请求时page对象为空，需要初始化一下
		if (this.page == null)
			this.page = new Page<Object>();
		if (this.page.getPageSize() < 1)
			this.page.setPageSize(Integer
					.parseInt(getText("app.grid.pageSize")));

		// 根据请求的条件查找分页信息
		this.page = this.findPage();
		this.es = page.getData();

		// 构建页面的html
		this.setHtml(buildHtml4Paging());

		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
	}

	// 仅获取表格的数据信息部分
	@Deprecated
	public String data() throws Exception {
		if (this.page != null) {// 分页的处理
			// 根据请求的条件查找分页信息
			this.page = this.findPage();
			this.es = this.page.getData();

			// 构建页面的html
			this.setHtml(buildGridData(this.buildGridColumns()));
		} else {// 非分页的处理
			// 根据请求的条件查找分页信息
			this.es = this.findList();

			// 构建页面的html
			this.setHtml(buildGridData(this.buildGridColumns()));
		}

		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
	}

	// 导出视图数据需要的变量
	public boolean exporting;// 标记当前处于导出状态
	public String exportKeys;// 要导出列的标识，用逗号连接多个
	public String fileName;// 下载的文件名
	public InputStream inputStream;// 下载文件对应的流
	public String contentType = "application/vnd.ms-excel";// 下载文件类型定义
	public int bufferSize = 4096;
	public String inputName = "inputStream";// 文件输出流定义
	public String contentDisposition;// 下载文件处理方法

	// 导出表格的数据为excel文件
	@Deprecated
	public String export() throws Exception {
		// 确定下载文件的名称(解决跨浏览器中文文件名乱码问题)
		if (fileName == null || fileName.length() == 0)
			fileName = getText("export.default.fileName")
					+ getText(StringUtils.uncapitalize(getEntityConfigName()));// 默认的文件名
		String title = fileName;
		fileName = WebUtils.encodeFileName(ServletActionContext.getRequest(),
				fileName);

		// 确定下载文件处理方法
		// contentDisposition = "inline;filename=\"" +fileName
		// +".xls\"";//在页面中打开
		contentDisposition = "attachment;filename=\"" + fileName + ".xls\"";// 以附件方式下载

		// TODO 根据导出条件构建
		if (this.page != null) {// 分页的处理
			this.page = this.findPage();
			this.es = this.page.getData();
		} else {// 非分页的处理
			this.es = this.findList();
		}

		// 导出数据到Excel
		GridExporter exporter = new GridExporter();
		exporter.setColumns(this.buildGridColumns()).setTitle(title)
				.setData(this.es).setIdLabel(getText("label.idLabel"));
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		exporter.exportTo(out);
		this.inputStream = new ByteArrayInputStream(out.toByteArray());

		return "export";

		// Content-Type:
		// 163下载.doc：application/msword
		// 163下载.xls：application/vnd.ms-excel
		// 163下载.txt：text/plain
		// 163下载.html：text/html
		// 163下载.rar|.reg：application/octet-stream
		// application/x-msdownload
	}

	/**
	 * 构建表格列头
	 * 
	 * @return
	 */
	@Deprecated
	protected GridHeader buildGridHeader(List<Column> columns) {
		GridHeader header = new GridHeader();
		header.setColumns(columns);
		header.setToggleSelectTitle(getText("title.toggleSelect"));
		return header;
	}

	/**
	 * 构建数据部分的html
	 * 
	 * @return
	 */
	@Deprecated
	protected GridData buildGridData(List<Column> columns) {
		GridData data = new GridData();
		if (this.page != null) {
			data.setPageNo(page.getPageNo());
			data.setPageCount(page.getPageCount());
			data.setTotalCount(page.getTotalCount());
		}
		data.setData(this.es);
		data.setColumns(columns);
		data.setRowLabelExpression("name");
		data.setName(getText(StringUtils.uncapitalize(getEntityConfigName())));
		return data;
	}

	/**
	 * 根据请求的条件查找列表对象
	 * 
	 * @return
	 */
	@Deprecated
	protected List<? extends Object> findList() {
		return this.getCrudService().createQuery()
				.condition(this.getCondition()).list();
	}

	/**
	 * 根据请求的条件查找分页信息对象
	 * 
	 * @return
	 */
	@Deprecated
	protected Page<? extends Object> findPage() {
		return this.getCrudService().createQuery()
				.condition(this.getCondition())
				.page(page.getPageNo(), page.getPageSize());
	}

	// 页面条件
	@Deprecated
	protected Condition getCondition() {
		return new AndCondition().add(getSpecalCondition())
				.add(getSearchCondition()).add(getOrderCondition());
	}

	/**
	 * 构建排序条件
	 * 
	 * @return
	 */
	@Deprecated
	protected OrderCondition getOrderCondition() {
		if (this.sort == null || this.sort.length() == 0)
			return getDefaultOrderCondition();

		// sort为grid的排序配置，格式为"filed1 asc,filed2 desc,..."
		String[] cfgs = this.sort.split(",");
		String[] cfg = cfgs[0].split(" ");

		OrderCondition oc = new OrderCondition(cfg[0],
				cfg.length > 1 ? (Direction.Desc.toSymbol().equalsIgnoreCase(
						cfg[1]) ? Direction.Desc : Direction.Asc)
						: Direction.Asc);

		for (int i = 1; i < cfgs.length; i++) {
			cfg = cfgs[i].split(" ");
			oc.add(cfg[0], cfg.length > 1 ? (Direction.Desc.toSymbol()
					.equalsIgnoreCase(cfg[1]) ? Direction.Desc : Direction.Asc)
					: Direction.Asc);
		}

		return oc;
	}

	/**
	 * 构建默认的排序条件，通常用于子类复写
	 * 
	 * @return
	 */
	@Deprecated
	protected OrderCondition getDefaultOrderCondition() {
		return null;
	}

	/**
	 * 构建特殊的条件，通常用于子类复写
	 * 
	 * @return
	 */
	@Deprecated
	protected Condition getSpecalCondition() {
		return null;
	}

	/**
	 * 构建查询条件
	 * 
	 * @return
	 */
	protected OrCondition getSearchCondition() {
		if (this.search == null || this.search.length() == 0)
			return null;

		// 用空格分隔多个查询条件的值的处理
		String[] values = this.search.split(" ");

		// 用空格分隔多个查询条件的值的处理
		String[] likeFields = this.getSearchFields();
		if (likeFields == null || likeFields.length == 0)
			return null;

		// 添加模糊查询条件
		// TODO 添加更复杂的查询处理，参考google的搜索格式
		OrCondition or = new OrCondition();
		for (String field : likeFields) {
			for (String value : values) {
				or.add(new LikeCondition(field, value));
			}
		}

		// 用括号将多个or条件括住
		or.setAddBracket(true);

		return or;
	}

	/**
	 * 查询条件中要匹配的域，通常用于子类复写
	 * 
	 * @return
	 */
	@Deprecated
	protected String[] getSearchFields() {
		logger.warn("please override the method 'getSearchFields' in your action!");
		return null;
	}

	/**
	 * 构建分页视图页面的html
	 * 
	 * @param page
	 *            分页信息对象
	 * @return
	 */
	@Deprecated
	protected HtmlPage buildHtml4Paging() {
		ListPage listPage = new ListPage();
		// 工具条
		listPage.setToolbar(buildToolbar());
		// 表格
		listPage.setGrid(buildGrid());
		// 参数
		listPage.setCreateUrl(getCreateUrl())
				.setDeleteUrl(getDeleteUrl())
				.setEditUrl(this.getEditUrl())
				.setOpenUrl(this.getOpenUrl())
				.setNamespace(getPageNamespace())
				.addJs(getJs())
				.addCss(getCss())
				.setTitle(this.getPageTitle())
				.setInitMethod(getIniMethod())
				.setOption(buildListPageOption().toString())
				.setBeautiful(true)
				.setAttr(
						"data-name",
						getText(StringUtils.uncapitalize(getEntityConfigName())))
				.addClazz("bc-page");
		return listPage;
	}

	/**
	 * 构建非分页视图页面的html
	 * 
	 * @return
	 */
	@Deprecated
	protected HtmlPage buildHtml4List() {
		return this.buildHtml4Paging();
	}

	/** 请使用buildFormPageOption(boolean editable)代替 */
	@Deprecated
	protected PageOption buildListPageOption() {
		return new PageOption().setMinWidth(250).setMinHeight(200)
				.setModal(false);
	}

	/** 构建表单页面的对话框初始化配置 */
	@Deprecated
	protected PageOption buildFormPageOption() {
		return buildFormPageOption(false);
	}

	/**
	 * 构建表单的对话框初始化配置
	 * 
	 * @param editable
	 *            是否为可编辑表单的配置,当调用create、edit方法时为true，调用open方法时为false
	 * @return
	 */
	protected PageOption buildFormPageOption(boolean editable) {
		PageOption pageOption = new PageOption().setMinWidth(250)
				.setMinHeight(200).setModal(false);

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
	 * 添加表单操作按钮，默认为保存按钮
	 * 
	 * @param pageOption
	 * @param editable
	 *            是否为可编辑表单的配置,当调用create、edit方法时为true，调用open方法时为false
	 */
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			// 添加默认的保存按钮
			pageOption.addButton(this.getDefaultSaveButtonOption());
		}
	}

	/** 构建视图页面的工具条 */
	@Deprecated
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(getDefaultEditToolbarButton());

			// 删除按钮
			tb.addButton(getDefaultDeleteToolbarButton());
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	// 创建默认的新建按钮
	@Deprecated
	protected Button getDefaultCreateToolbarButton() {
		return Toolbar.getDefaultCreateToolbarButton(getText("label.create"));
	}

	// 创建默认的查看按钮
	@Deprecated
	protected Button getDefaultOpenToolbarButton() {
		return Toolbar.getDefaultEditToolbarButton(getText("label.read"));
	}

	// 创建默认的编辑按钮
	@Deprecated
	protected Button getDefaultEditToolbarButton() {
		return Toolbar.getDefaultEditToolbarButton(getText("label.edit"));
	}

	// 创建默认的删除按钮
	@Deprecated
	protected Button getDefaultDeleteToolbarButton() {
		return Toolbar.getDefaultDeleteToolbarButton(getText("label.delete"));
	}

	// 创建默认的搜索按钮
	@Deprecated
	protected Button getDefaultSearchToolbarButton() {
		return Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search"));
	}

	/** 创建默认的表单保存按钮 */
	protected ButtonOption getDefaultSaveButtonOption() {
		return new ButtonOption(getText("label.save"), "save")
				.setId("bcSaveBtn");
	}

	/** 构建视图页面的表格 */
	@Deprecated
	protected Grid buildGrid() {
		List<Column> columns = this.buildGridColumns();

		// id列
		Grid grid = new Grid();
		grid.setGridHeader(this.buildGridHeader(columns));
		grid.setGridData(this.buildGridData(columns));
		grid.setRemoteSort("true"
				.equalsIgnoreCase(getText("app.grid.remoteSort")));
		grid.setColumns(columns);
		// name属性设为bean的名称
		grid.setName(getText(StringUtils.uncapitalize(getEntityConfigName())));
		// 多选及双击行编辑
		grid.setSingleSelect(false).setDblClickRow("bc.page.edit");

		// 分页条
		grid.setFooter(buildGridFooter(grid));

		return grid;
	}

	@Deprecated
	protected List<Column> buildGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn());
		return columns;
	}

	/**
	 * 判断指定的列是否应该添加
	 * <ul>
	 * 返回true的情况：
	 * <li>
	 * 1)非导出状态</li>
	 * <li>
	 * 2)导出状态，用户没有排除某些列不导出</li>
	 * <li>
	 * 3)导出状态，用户选定的某些列</li>
	 * </ul>
	 * 
	 * @param key
	 *            列的标识
	 * @return
	 */
	protected boolean useColumn(String key) {
		return !this.exporting
				|| (this.exportKeys == null || this.exportKeys.length() == 0 || this.exportKeys
						.indexOf(key) != -1);
	}

	/** 构建视图页面表格底部的工具条 */
	@Deprecated
	protected GridFooter buildGridFooter(Grid grid) {
		GridFooter footer = new GridFooter();

		// 刷新按钮
		footer.addButton(GridFooter
				.getDefaultRefreshButton(getText("label.refresh")));

		// 本地或远程排序方式切换按钮
		footer.addButton(GridFooter.getDefaultSortButton(grid.isRemoteSort(),
				getText("title.click2remoteSort"),
				getText("title.click2localSort")));

		// 分页按钮
		if (this.page != null) {
			footer.addButton(new SeekGroupButton().setPageNo(page.getPageNo())
					.setPageCount(page.getPageCount())
					.setTotalCount(page.getTotalCount()));
			footer.addButton(new PageSizeGroupButton().setActiveValue(25)
					.setValues(new int[] { 25, 50, 100 })
					.setTitle(getText("label.pageSize")));
		}

		// 导出按钮
		footer.addButton(GridFooter
				.getDefaultExportButton(getText("label.export")));

		// 打印按钮
		// footer.addButton(GridFooter.getDefaultPrintButton(getText("label.print")));

		return footer;
	}

	/**
	 * 获取访问action的前缀。 struts配置文件中package节点的namespace属性去除最后面的bean名称的部分
	 * 如namespace="/bc/duty"，则这里应返回"/bc"
	 * 
	 * @return
	 */
	protected String getActionPathPrefix() {
		return "/bc";
	}

	/** 访问的主路径 */
	protected String getPageNamespace() {
		return getContextPath() + this.getActionPathPrefix() + "/"
				+ StringUtils.uncapitalize(getEntityConfigName());
	}

	/** 对话框的标题 */
	protected String getPageTitle() {
		return this.getText(StringUtils.uncapitalize(getEntityConfigName())
				+ ".title");
	}

	/** 编辑的url */
	protected String getEditUrl() {
		return getPageNamespace() + "/edit";
	}

	/** 查看的url */
	protected String getOpenUrl() {
		return getPageNamespace() + "/open";
	}

	/** 删除的url */
	protected String getDeleteUrl() {
		return getPageNamespace() + "/delete";
	}

	/** 新建的url */
	protected String getCreateUrl() {
		return getPageNamespace() + "/create";
	}

	/** 获取访问该ation的上下文路径 */
	protected String getContextPath() {
		return ServletActionContext.getRequest().getContextPath();
	}

	/** 页面加载后的调用js初始化方法 */
	protected String getIniMethod() {
		return null;
	}

	/** 页面需要另外加载的css文件，逗号连接多个文件 */
	protected String getCss() {
		return null;
	}

	/** 页面需要另外加载的js文件，逗号连接多个文件 */
	protected String getJs() {
		return null;
	}

	/**
	 * 获取Entity的状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(RichEntity.STATUS_ENABLED),
				getText("entity.status.enabled"));
		statuses.put(String.valueOf(RichEntity.STATUS_DISABLED),
				getText("entity.status.disabled"));
		statuses.put(String.valueOf(RichEntity.STATUS_DELETED),
				getText("entity.status.deleted"));
		return statuses;
	}
}