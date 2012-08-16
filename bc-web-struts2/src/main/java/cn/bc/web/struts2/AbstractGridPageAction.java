/**
 * 
 */
package cn.bc.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.BCConstants;
import cn.bc.core.Page;
import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.MixCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.query.condition.impl.QlCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.GridFooter;
import cn.bc.web.ui.html.grid.GridHeader;
import cn.bc.web.ui.html.grid.PageSizeGroupButton;
import cn.bc.web.ui.html.grid.SeekGroupButton;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.ListPage;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 视图抽象Action
 * 
 * @author dragon
 * 
 */
public abstract class AbstractGridPageAction<T extends Object> extends
		AbstractHtmlPageAction {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog("cn.bc.web.struts2.AbstractGridPageAction");
	public boolean multiple = true;// 是否允许多选
	public List<T> es; // grid的数据
	private Page<T> page; // 分页对象
	public String search; // 搜索框输入的文本
	/**
	 * 高级搜索的条件，使用标准的json数组结构： [ { type:
	 * "string"|"int"|"long"|"date"|"startDate"|
	 * "endDate"|"calendar"|"startCalendar"|"endCalendar", ql:
	 * "field=?"|"field>=?"|"field<=?"|"field like ?"|"field in (?,?,...)"|...,
	 * value: 值|[值1,值2,...] } ]
	 * 
	 */
	public String search4advance;
	public String sort; // grid的排序配置，格式为"filed1 asc,filed2 desc,..."

	public Page<T> getPage() {
		return page;
	}

	public void setPage(Page<T> page) {
		this.page = page;
	}

	// == 子类必须复写的方法

	/** grid数据的查询服务 */
	protected abstract Query<T> getQuery();

	/** 计算grid数据行标签信息的表达式 */
	protected abstract String getGridRowLabelExpression();

	/** 查询条件中要匹配的域 */
	protected abstract String[] getGridSearchFields();

	/** 表格的列配置 */
	protected abstract List<Column> getGridColumns();

	// == Action方法

	@Override
	public String execute() throws Exception {
		return this.list();
	}

	// Action：无分页Grid
	public String list() throws Exception {
		Date startTime = new Date();
		// 根据请求的条件查找信息
		this.es = this.findList();

		// 构建页面的html
		this.html = buildHtmlPage();

		logger.info("list耗时：" + DateUtils.getWasteTime(startTime));
		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
	}

	// Action：分页
	public String paging() throws Exception {
		Date startTime = new Date();
		// 首次请求时page对象为空，需要初始化一下
		if (this.page == null)
			this.page = new Page<T>();
		if (this.page.getPageSize() < 1)
			this.page.setPageSize(Integer
					.parseInt(getText("app.grid.pageSize")));

		// 根据请求的条件查找分页信息
		this.page = this.findPage();
		this.es = page.getData();

		// 构建页面的html
		this.html = buildHtmlPage();

		logger.info("paging耗时：" + DateUtils.getWasteTime(startTime));
		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
	}

	// Action：仅获取表格的数据信息部分
	public String data() throws Exception {
		Date startTime = new Date();
		if (this.page != null) {// 分页的处理
			// 根据请求的条件查找分页信息
			this.page = this.findPage();
			this.es = this.page.getData();

			// 构建页面的html
			this.html = getGridData(this.getGridColumns());
		} else {// 非分页的处理
			// 根据请求的条件查找分页信息
			this.es = this.findList();

			// 构建页面的html
			this.html = getGridData(this.getGridColumns());
		}

		logger.info("data耗时：" + DateUtils.getWasteTime(startTime));
		// 返回全局的global-results：在cn/bc/web/struts2/struts.xml中定义的
		return "page";
	}

	// == 默认实现的方法

	/**
	 * 根据请求的条件查找列表对象
	 * 
	 * @return
	 */
	protected List<T> findList() {
		return this.getQuery().condition(this.getGridCondition()).list();
	}

	/**
	 * 根据请求的条件查找分页信息对象
	 * 
	 * @return
	 */
	protected Page<T> findPage() {
		return this.getQuery().condition(this.getGridCondition())
				.page(page.getPageNo(), page.getPageSize());
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		ListPage listPage = new ListPage();

		// 设置页面参数
		listPage.setNamespace(
				getHtmlPageNamespace() + "/" + getViewActionName())
				.addJs(getHtmlPageJs()).setTitle(this.getHtmlPageTitle())
				.setInitMethod(getHtmlPageInitMethod()).setType("list")
				.setOption(getHtmlPageOption().toString()).setBeautiful(false)
				.addClazz("bc-page");
		listPage.setCreateUrl(getCreateUrl()).setDeleteUrl(getDeleteUrl())
				.setEditUrl(this.getEditUrl()).setOpenUrl(this.getOpenUrl())
				.setAttr("data-name", getText(getFormActionName()));

		// 附加头部信息
		listPage.addChild(getHtmlPageHeader());

		// 附加Grid
		listPage.addChild(getHtmlPageGrid());

		// 附加页脚信息
		listPage.addChild(getHtmlPageFooter());

		// 额外参数配置
		Json json = this.getGridExtrasData();
		if (json != null)
			listPage.setAttr("data-extras", json.toString());

		return listPage;
	}

	/** 附加头部信息 */
	protected Component getHtmlPageHeader() {
		// 默认为工具条
		return getHtmlPageToolbar();
	}

	/** 附加页脚信息 */
	protected Component getHtmlPageFooter() {
		return null;
	}

	/** 获取视图action的简易名称 */
	protected String getViewActionName() {
		return getFormActionName() + "s";
	}

	/** 获取表单action的简易名称 */
	protected abstract String getFormActionName();

	/** 编辑的url */
	protected String getEditUrl() {
		return getHtmlPageNamespace() + "/" + this.getFormActionName()
				+ "/edit";
	}

	/** 查看的url */
	protected String getOpenUrl() {
		return getHtmlPageNamespace() + "/" + this.getFormActionName()
				+ "/open";
	}

	/** 删除的url */
	protected String getDeleteUrl() {
		return getHtmlPageNamespace() + "/" + this.getFormActionName()
				+ "/delete";
	}

	/** 新建的url */
	protected String getCreateUrl() {
		return getHtmlPageNamespace() + "/" + this.getFormActionName()
				+ "/create";
	}

	protected Grid getHtmlPageGrid() {
		Grid grid = new Grid();

		// grid的列配置
		List<Column> columns = this.getGridColumns();
		grid.setColumns(columns);

		// grid列头部分
		grid.setGridHeader(this.getGridHeader(columns));

		// grid数据部分
		grid.setGridData(this.getGridData(columns));

		// grid远程排序控制
		grid.setRemoteSort("true"
				.equalsIgnoreCase(getText("app.grid.remoteSort")));

		// 多选及双击行编辑
		grid.setSingleSelect(!this.multiple).setDblClickRow(
				this.getGridDblRowMethod());

		// 分页条
		grid.setFooter(getGridFooter(grid));

		return grid;
	}

	/** 获取表格数据的额外请求参数 */
	protected Json getGridExtrasData() {
		Json json = new Json();
		this.extendGridExtrasData(json);
		return json.isEmpty() ? null : json;
	}

	/**
	 * 扩展表格数据的额外请求参数
	 * 
	 * @param json
	 *            已经初始化好的Json对象
	 */
	protected void extendGridExtrasData(Json json) {
		// 注意put入到json的key要与action的字段名一致
	}

	/** 获取表格双击行的js处理函数名 */
	protected String getGridDblRowMethod() {
		return this.isReadonly() ? "bc.page.open" : "bc.page.edit";
	}

	/**
	 * 构建表格列头
	 * 
	 * @return
	 */
	protected GridHeader getGridHeader(List<Column> columns) {
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
	protected GridData getGridData(List<Column> columns) {
		GridData data = new GridData() {
			public String getRowClass(List<? extends Object> data,
					Object rowData, int index, int type) {
				return getGridRowClass(data, rowData, index, type);
			}
		};
		if (this.page != null) {
			data.setPageNo(page.getPageNo());
			data.setPageCount(page.getPageCount());
			data.setTotalCount(page.getTotalCount());
		}
		data.setData(this.rebuildGridData(this.es));
		data.setColumns(columns);
		data.setRowLabelExpression(getGridRowLabelExpression());
		// data.setName(getText(StringUtils.uncapitalize(getEntityConfigName())));
		return data;
	}

	/**
	 * 对视图的数据执行特殊的处理，默认不作任何处理
	 * 
	 * @param data
	 *            视图的数据
	 * @return 处理后的数据
	 */
	protected List<T> rebuildGridData(List<T> data) {
		return data;
	}

	/**
	 * 获取数据行需要附加的特殊样式
	 * 
	 * @param data
	 *            整个grid的数据
	 * @param rowData
	 *            此行包含的数据
	 * @param index
	 *            行的索引号
	 * @param type
	 *            0-左侧固定列的行,1-右侧数据列的行
	 * @return 返回空将不附加特殊样式
	 */
	protected String getGridRowClass(List<? extends Object> data,
			Object rowData, int index, int type) {
		return null;
	}

	/** 构建视图页面表格底部的工具条 */
	protected GridFooter getGridFooter(Grid grid) {
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

		// 添加自定义的按钮
		this.extendGridFooterButton(footer);

		return footer;
	}

	/**
	 * 添加自定义的Grid底部工具条按钮
	 * 
	 * @param gridFooter
	 *            已初始化好的GridFooter
	 */
	protected void extendGridFooterButton(GridFooter gridFooter) {

	}

	/** grid数据的查询条件 */
	protected Condition getGridCondition() {
		return new AndCondition().add(getGridSpecalCondition())
				.add(getGridSearchCondition()).add(getGridOrderCondition());
	}

	/**
	 * 构建排序条件
	 * 
	 * @return
	 */
	protected OrderCondition getGridOrderCondition() {
		if (this.sort == null || this.sort.length() == 0)
			return getGridDefaultOrderCondition();

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
	protected OrderCondition getGridDefaultOrderCondition() {
		return null;
	}

	/**
	 * 构建特殊的条件，通常用于子类复写
	 * 
	 * @return
	 */
	protected Condition getGridSpecalCondition() {
		return null;
	}

	/**
	 * 构建简易+高级的查询条件
	 * 
	 * @return
	 */
	protected MixCondition getGridSearchCondition() {
		return ConditionUtils.mix2AndCondition(
				this.getGridSearchCondition4Simple(),
				this.getGridSearchCondition4Advance());
	}

	/**
	 * 构建简易查询条件：对应默认的简易搜索条件
	 * 
	 * @return
	 */
	protected MixCondition getGridSearchCondition4Simple() {
		if (this.search == null || this.search.trim().length() == 0)
			return null;
		this.search = this.search.trim().replaceAll("＋", "+");

		// 用空格分隔多个查询条件值的处理
		String[] values;
		boolean isOr = this.search.indexOf("+") == -1;
		if (isOr)
			values = this.search.split(" ");// “A空格B”查询
		else
			values = this.search.split("\\+");// “A+B”查询

		// 用空格分隔多个查询条件的值的处理
		String[] likeFields = this.getGridSearchFields();
		if (likeFields == null || likeFields.length == 0)
			return null;

		// 添加模糊查询条件
		// TODO 添加更复杂的查询处理，参考google的搜索格式
		if (isOr) {// “A空格B”查询
			// ----(f1 like v1 or f1 like v2 or ..) or (f2 like v1 or f2 like v2
			// or ..) or ..
			OrCondition or = new OrCondition();
			for (String field : likeFields) {
				for (String value : values) {
					or.add(getGridSearchCondition4OneField(field, value));
				}
			}

			// 用括号将多个or条件括住
			or.setAddBracket(true);
			if (logger.isDebugEnabled()) {
				logger.debug("simpleConditions:" + or.toString());
			}
			return or;
		} else {// “A+B”查询
			// ----(f1 like v1 or f1 like v2 or ..) and (f2 like v1 or f2 like
			// v2 or ..) and ..
			AndCondition and = new AndCondition();
			// 用括号将多个and条件括住
			and.setAddBracket(true);

			OrCondition or;
			for (String value : values) {
				or = new OrCondition();
				for (String field : likeFields) {
					or.add(getGridSearchCondition4OneField(field, value));
				}

				// 用括号将多个or条件括住
				or.setAddBracket(true);
				and.add(or);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("simpleConditions:" + and.toString());
			}
			return and;
		}
	}

	/**
	 * 构建单个查询条件的方法
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	protected LikeCondition getGridSearchCondition4OneField(String field,
			String value) {
		return new LikeCondition(field, value);
	}

	/**
	 * 构建高级查询条件
	 * 
	 * @return
	 */
	protected MixCondition getGridSearchCondition4Advance() {
		if (this.search4advance == null
				|| this.search4advance.trim().length() == 0)
			return null;
		this.search4advance = this.search4advance.trim();
		try {
			JSONArray jsons = new JSONArray(this.search4advance);
			if (jsons.length() == 0)
				return null;

			AndCondition and = new AndCondition();
			and.setAddBracket(true);
			JSONObject json;
			Object value;
			Object[] values;
			String type;
			String ql;
			JSONArray value1;
			boolean isLike;
			List<Object> args = new ArrayList<Object>();
			int c;
			for (int i = 0; i < jsons.length(); i++) {
				json = jsons.getJSONObject(i);
				value = json.get("value");
				type = json.getString("type");
				ql = json.getString("ql");
				isLike = ql.toLowerCase().indexOf("like") != -1;
				if (value instanceof JSONArray) {// 多个值的数组
					value1 = (JSONArray) value;
					args = new ArrayList<Object>();

					if ("multi".equals(type)) {// 带?[num]标记的多值特殊处理
						// 转换占位参数的值
						Object[] multiArgs = new Object[value1.length()];
						for (int j = 0; j < value1.length(); j++) {
							json = value1.getJSONObject(j);
							multiArgs[j] = QlCondition.convertValue(json
									.getString("type"),
									json.getString("value"),
									json.has("like") ? json.getBoolean("like")
											: false);
						}

						// 按照?[num]的位置生成相应的参数列表
						String regex = "\\?\\d+";// 匹配?[num]模式
						Pattern p = Pattern.compile(regex);
						Matcher m = p.matcher(ql);
						while (m.find()) {
							args.add(multiArgs[Integer.parseInt(m.group()
									.substring(1))]);
						}

						// 替换所有?[num]为?
						ql = ql.replaceAll("\\?\\d+", "\\?");
					} else {// 常规处理
						for (int j = 0; j < value1.length(); j++) {
							args.add(QlCondition.convertValue(type,
									value1.getString(j), isLike));
						}
					}
					and.add(new QlCondition(ql, args));
				} else {// 单个值的字符串
					c = StringUtils.countMatches(ql, "?");
					values = new Object[c];
					String likeType = json.has("likeType") ? json
							.getString("likeType") : null;

					// 多个值用空格分开
					String[] vs = ((String) value).split(" ");

					if (vs.length == 1) {
						// 自定义like类型
						if (isLike)
							value = convertByLikeType(likeType, (String) value);
						
						// 转换值为指定的类型
						value = QlCondition.convertValue(type, (String) value,
								isLike);
						
						// 如果查询语句中有多个?号，就将值复制出多个来
						for (int j = 0; j < values.length; j++) {
							values[j] = value;
						}
						and.add(new QlCondition(ql, values));
					} else if (vs.length > 1) {
						OrCondition or = new OrCondition();
						or.setAddBracket(true);
						for (String v : vs) {
							// 自定义like类型
							if (isLike)
								v = convertByLikeType(likeType, v);
							
							// 转换值为指定的类型
							v = (String) QlCondition.convertValue(type,
									v, isLike);
							
							// 如果查询语句中有多个?号，就将值复制出多个来
							values = new Object[c];
							for (int j = 0; j < values.length; j++) {
								values[j] = v;
							}
							or.add(new QlCondition(ql, values));
						}
						and.add(or);
					}
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("advanceConditions:" + and.toString());
			}
			return and;
		} catch (JSONException e) {
			logger.error("can't convert to JSONArray:search4advance="
					+ this.search4advance);
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private String convertByLikeType(String likeType, String value) {
		if (value == null || value.length() == 0)
			return value;
		if ("right".equalsIgnoreCase(likeType)) {
			return "%" + value;
		} else if ("left".equalsIgnoreCase(likeType)) {
			return value + "%";
		} else {
			return value;
		}
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText(this.getFormActionName() + ".title");
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return getHtmlPageToolbar(false);
	}

	/**
	 * @param useDisabledReplaceDelete
	 *            控制是使用删除按钮还是禁用按钮
	 * @return
	 */
	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		Toolbar tb = new Toolbar();

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(getDefaultEditToolbarButton());

			if (useDisabledReplaceDelete) {
				// 禁用按钮
				tb.addButton(getDefaultDisabledToolbarButton());
			} else {
				// 删除按钮
				tb.addButton(getDefaultDeleteToolbarButton());
			}
		}

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	/**
	 * 创建默认的删除按钮
	 * 
	 * @return
	 */
	protected Button getDefaultDeleteToolbarButton() {
		return Toolbar.getDefaultDeleteToolbarButton(getText("label.delete"));
	}

	/**
	 * 创建默认的禁用按钮
	 * 
	 * @return
	 */
	protected Button getDefaultDisabledToolbarButton() {
		return Toolbar
				.getDefaultDisabledToolbarButton(getText("label.disabled"));
	}

	/**
	 * 创建默认的编辑按钮
	 * 
	 * @return
	 */
	protected Button getDefaultEditToolbarButton() {
		return Toolbar.getDefaultEditToolbarButton(getText("label.edit"));
	}

	/**
	 * 创建默认的新建按钮
	 * 
	 * @return
	 */
	protected Button getDefaultCreateToolbarButton() {
		return Toolbar.getDefaultCreateToolbarButton(getText("label.create"));
	}

	/**
	 * 创建默认的查看按钮
	 * 
	 * @return
	 */
	protected Button getDefaultOpenToolbarButton() {
		return Toolbar.getDefaultOpenToolbarButton(getText("label.read"));
	}

	/**
	 * 创建默认的搜索按钮
	 * 
	 * @return
	 */
	protected Button getDefaultSearchToolbarButton() {
		if (this.useAdvanceSearch()) {
			return Toolbar.getDefaultAdvanceSearchToolbarButton(
					getText("title.click2search"),
					getText("title.click2advanceSearch"),
					this.getAdvanceSearchConditionsActionPath());
		} else {
			return Toolbar
					.getDefaultSearchToolbarButton(getText("title.click2search"));
		}
	}

	/**
	 * 获取Entity的状态值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("entity.status.disabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("entity.status.enabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DELETED),
				getText("entity.status.deleted"));
		return statuses;
	}

	// ==高级搜索代码开始==

	/**
	 * 获取高级搜索条件框的action路径，默认为“[上下文路径]/[子系统路径]/[domain类名的小写]/conditions”
	 * 
	 * @return
	 */
	protected String getAdvanceSearchConditionsActionPath() {
		return this.getHtmlPageNamespace() + "/" + this.getFormActionName()
				+ "s/conditions";
	}

	/**
	 * 是否使用高级搜索，默认为false
	 * 
	 * @return
	 */
	protected boolean useAdvanceSearch() {
		return false;
	}

	public String getAdvanceSearchConditionsJspPath() {
		return BCConstants.NAMESPACE + "/" + this.getFormActionName();
	}

	/**
	 * 高级搜索条件窗口
	 * 
	 * @return
	 * @throws Exception
	 */
	public String conditions() throws Exception {
		// 加载条件窗口的可选项列表信息
		this.initConditionsFrom();

		return SUCCESS;
	}

	/**
	 * 初始化高级及搜索条件窗口的可选项列表信息
	 */
	protected void initConditionsFrom() throws Exception {

	}

	// ==高级搜索代码结束==
}
