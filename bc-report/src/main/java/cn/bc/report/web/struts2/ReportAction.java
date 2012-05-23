/**
 * 
 */
package cn.bc.report.web.struts2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.MixCondition;
import cn.bc.core.util.TemplateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.docs.domain.Attach;
import cn.bc.identity.web.SystemContext;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.report.domain.ReportHistory;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.report.service.ReportHistoryService;
import cn.bc.report.service.ReportTemplateService;
import cn.bc.report.service.RunReportTemplateJobBean;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridExporter;
import cn.bc.web.ui.html.grid.GridFooter;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.PageSizeGroupButton;
import cn.bc.web.ui.html.grid.SeekGroupButton;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 报表执行Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(ReportAction.class);
	public String code;// 报表模板的编码
	private ReportTemplateService reportTemplateService;// 报表模板服务
	private ReportHistoryService reportHistoryService;
	private TemplateService templateService;
	private ReportTemplate tpl;// 报表模板
	private JSONObject config;// 报表模板的详细配置

	@Autowired
	public void setReportHistoryService(
			ReportHistoryService reportHistoryService) {
		this.reportHistoryService = reportHistoryService;
	}

	@Autowired
	public void setReportTemplateService(
			ReportTemplateService reportTemplateService) {
		this.reportTemplateService = reportTemplateService;
	}

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	private JSONObject getConfig() {
		if (config != null)
			return config;

		this.initConfig();
		return config;
	}

	private void initConfig() {
		// 初始化配置信息
		this.tpl = this.reportTemplateService.loadByCode(this.code);
		if (this.tpl == null) {
			throw new CoreException("指定的模板不存在:code=" + this.code);
		}
		// this.tpl = new ReportTemplate();
		// this.tpl.setName("每日登录帐号数统计");
		//
		// this.tpl.setConfig("{type: 'sql'," + "columns: ["
		// + "    {type:'id',id: 'id', width: 40, el:'id'},"
		// + "    {id: 'logday', label: '登录日', width: 100, el:'logday'},"
		// + "    {id: 'count', label: '登录帐号数', width: 100, el:'count'},"
		// + "    {id: 'names', label: '登录账号', el:'names'}" + "],"
		// + "sql: 'tpl:accountLoginStat4DaySQL',"
		// + "condition: 'tpl:testConditionForm',"
		// + "export: 'tpl:accountLoginStat4DayTemplate',"
		// + "width: 600,height: 400}");
		this.config = tpl.getConfigJson();

		// 避免空指针引用
		if (this.config == null)
			this.config = new JSONObject();
	}

	@Override
	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		// 判断是否生成工具条
		if (!(this.getConfig().has("search") || this.getConfig().has("tb") || this
				.getConfig().has("condition")))
			return null;

		try {
			Toolbar tb = new Toolbar();

			// 添加存为历史按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-tag")
					.setText("存为历史报表").setClick("bc.report.save2history"));

			// 添加自定义的按钮
			if (this.getConfig().has("tb")) {
				JSONArray tbCfg = this.getConfig().getJSONArray("tb");
				JSONObject buttonCfg;
				ToolbarButton button;
				for (int i = 0; i < tbCfg.length(); i++) {
					buttonCfg = tbCfg.getJSONObject(i);
					button = new ToolbarButton();
					tb.addButton(button);
					if (buttonCfg.has("text"))
						button.setText(buttonCfg.getString("text"));
					if (buttonCfg.has("click"))
						button.setClick(buttonCfg.getString("click"));
					if (buttonCfg.has("icon"))
						button.setIcon(buttonCfg.getString("icon"));
					if (buttonCfg.has("id"))
						button.setId(buttonCfg.getString("id"));
					if (buttonCfg.has("icon2"))
						button.setSecondaryIcon(buttonCfg.getString("icon2"));
					if (buttonCfg.has("action"))
						button.setAction(buttonCfg.getString("action"));
					if (buttonCfg.has("title"))
						button.setTitle(buttonCfg.getString("title"));
					if (buttonCfg.has("callback"))
						button.setCallback(buttonCfg.getString("callback"));
				}
			}

			// 添加搜索按钮
			if (this.getConfig().has("search")
					|| this.getConfig().has("condition")) {
				if (tb.isEmpty())
					tb.addButton(Toolbar.getDefaultEmptyToolbarButton());
				tb.addButton(getDefaultSearchToolbarButton());
			}
			return tb;
		} catch (JSONException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	protected String getAdvanceSearchConditionsActionPath() {
		return this.getHtmlPageNamespace() + "/report/conditions";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}

	@Override
	protected String getViewActionName() {
		return "report";
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.tpl.getName();
	}

	@Override
	protected PageOption getHtmlPageOption() {
		PageOption po = super.getHtmlPageOption();
		try {
			if (this.getConfig().has("width")) {
				po.setWidth(this.config.getInt("width"));
			}
			if (this.getConfig().has("height")) {
				po.setHeight(this.config.getInt("height"));
			}
		} catch (JSONException e) {
			throw new CoreException(e.getMessage());
		}

		return po;
	}

	@Override
	protected Query<Map<String, Object>> getQuery() {
		return buildQuery(jpaTemplate, getSqlObject());
	}

	public static Query<Map<String, Object>> buildQuery(
			JpaTemplate jpaTemplate, SqlObject<Map<String, Object>> sqlObject) {
		return new HibernateJpaNativeQuery<Map<String, Object>>(jpaTemplate,
				sqlObject);
	}

	private List<Column> columns;

	@Override
	protected List<Column> getGridColumns() {
		if (columns != null)
			return columns;

		else
			return buildGridColumns(this.getConfig());
	}

	public static List<Column> buildGridColumns(JSONObject config) {
		List<Column> columns = new ArrayList<Column>();

		// 初始化列的配置信息
		JSONArray jColumns = null;
		try {
			jColumns = config.getJSONArray("columns");
		} catch (JSONException e) {
			throw new CoreException(e.getMessage());
		}
		JSONObject jcolumn = null;
		for (int i = 0; i < jColumns.length(); i++) {
			try {
				jcolumn = jColumns.getJSONObject(i);
				if (jcolumn.has("type")
						&& "id".equals(jcolumn.getString("type"))) {// IdColumn4MapKey列
					columns.add(new IdColumn4MapKey(jcolumn.getString("id"),
							jcolumn.has("el") ? jcolumn.getString("el")
									: jcolumn.getString("id")));
				} else {// 默认使用TextColumn4MapKey列
					columns.add(new TextColumn4MapKey(jcolumn.getString("id"),
							jcolumn.has("el") ? jcolumn.getString("el")
									: jcolumn.getString("id"), jcolumn
									.getString("label"),
							jcolumn.has("width") ? jcolumn.getInt("width") : 0));
				}
			} catch (JSONException e) {
				throw new CoreException(e.getMessage());
			}
		}
		return columns;
	}

	/**
	 * 执行报表
	 * 
	 * @return
	 * @throws Exception
	 */
	public String run() throws Exception {
		if (this.getConfig().has("paging")
				&& this.getConfig().getBoolean("paging")) {
			return this.paging();
		} else {
			return this.list();
		}
	}

	@Override
	public String paging() throws Exception {
		this.initConfig();
		return super.paging();
	}

	@Override
	public String list() throws Exception {
		this.initConfig();
		return super.list();
	}

	@Override
	protected List<Map<String, Object>> findList() {
		return this.getQuery().list();
	}

	@Override
	protected Page<Map<String, Object>> findPage() {
		return this.getQuery().page(this.getPage().getPageNo(),
				this.getPage().getPageSize());
	}

	@Override
	public String export() throws Exception {
		this.initConfig();
		return super.export();
	}

	/**
	 * 将报表运行结果存为历史
	 * 
	 * @return
	 * @throws Exception
	 */
	public String save2history() throws Exception {
		Json json = new Json();

		this.initConfig();

		// 执行报表模板并返回生成的历史报表
		Assert.notNull(tpl, "报表任务配置的报表模板为空！");
		ReportHistory h = RunReportTemplateJobBean.buildReportHistory(this.tpl,
				"xls", this.templateService, this.jpaTemplate);

		// 设置报表历史的一些信息
		h.setAuthor(((SystemContext) this.getContext()).getUserHistory());
		h.setSourceType("用户生成");
		h.setSourceId(this.tpl.getId());

		// 保存报表历史
		this.reportHistoryService.save(h);

		json.put("success", true);
		json.put("msg", "success");

		this.json = json.toString();
		return "json";
	}

	@Override
	protected GridFooter getGridFooter(Grid grid) {
		GridFooter footer = new GridFooter();

		// 刷新按钮
		footer.addButton(GridFooter
				.getDefaultRefreshButton(getText("label.refresh")));

		// 分页按钮
		if (this.getPage() != null) {
			footer.addButton(new SeekGroupButton()
					.setPageNo(this.getPage().getPageNo())
					.setPageCount(this.getPage().getPageCount())
					.setTotalCount(this.getPage().getTotalCount()));
			footer.addButton(new PageSizeGroupButton().setActiveValue(25)
					.setValues(new int[] { 25, 50, 100 })
					.setTitle(getText("label.pageSize")));
		}

		// 添加自定义的按钮
		this.extendGridFooterButton(footer);

		return footer;
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);
		if (this.code != null)
			json.put("code", this.code);
	}

	@Override
	protected GridExporter buileGridExporter(String title, String idLabel) {
		GridExporter exporter = super.buileGridExporter(title, idLabel);

		// 设置导出模板
		exporter.setTemplateFile(buildExportTemplate(this.templateService,
				this.getConfig()));

		return exporter;
	}

	public static InputStream buildExportTemplate(
			TemplateService templateService, JSONObject config) {
		if (config.has("export")) {
			try {
				String export = config.getString("export");
				if (export.startsWith("tpl:")) {
					Template t = templateService
							.loadByCode(export.substring(4));
					if (t != null
							&& t.getTemplateType().getCode().equals("xls")) {// 只能使用excel模板
						return t.getInputStream();
					} else {
						throw new CoreException(
								"template is not exists or is not excel type:export="
										+ export);
					}
				} else {
					File file = new File(Attach.DATA_REAL_PATH + "/" + export);
					try {
						return new FileInputStream(file);
					} catch (FileNotFoundException e) {
						throw new CoreException("template is not exists:file="
								+ file.getAbsolutePath());
					}
				}
			} catch (JSONException e) {
				throw new CoreException(e.getMessage());
			}
		}
		return null;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		JSONObject config = this.getConfig();
		return buildSqlObject(this.templateService, config,
				this.getGridCondition(), this.getGridColumns());
	}

	public static SqlObject<Map<String, Object>> buildSqlObject(
			TemplateService templateService, JSONObject config, Condition c,
			List<Column> columns) {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		try {
			// 解析sql
			String sql = config.getString("sql");
			Map<String, Object> params = buildParams(c);
			if (sql.startsWith("tpl:")) {
				Template tpl = templateService.loadByCode(sql.substring(4));
				if (tpl != null) {
					if (tpl.isPureText()) {
						sqlObject.setSql(tpl.getContentEx(params).trim());
					} else {
						throw new CoreException(
								"sql template is not pure text:sql=" + sql);
					}
				} else {
					throw new CoreException("sql template is not exists:sql="
							+ sql);
				}
			} else {
				sqlObject.setSql(TemplateUtils.format(sql, params).trim());
			}

			// 注入参数
			if (c != null) {
				sqlObject.setArgs(c.getValues());
			}
		} catch (JSONException e) {
			throw new CoreException(e.getMessage());
		}

		// 获取所有列的值表达式，作为数据映射的键值
		final List<String> mapKeys = new ArrayList<String>();
		for (Column column : columns) {
			if (column instanceof TextColumn4MapKey)
				mapKeys.add(((TextColumn4MapKey) column)
						.getOriginValueExpression());
			else if (column instanceof IdColumn4MapKey)
				mapKeys.add(((IdColumn4MapKey) column)
						.getOriginValueExpression());
			else
				mapKeys.add(column.getValueExpression());
		}

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				for (String key : mapKeys) {
					map.put(key, rs[i]);
					i++;
				}
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected String getDefaultExportFileName() {
		return this.tpl.getName();
	}

	@Override
	protected boolean useAdvanceSearch() {
		return this.getConfig().has("condition");
	}

	public String resultPath;

	@Override
	public String conditions() throws Exception {
		if (!this.getConfig().has("condition")) {
			throw new CoreException("unsupport method");
		}

		String condition = this.getConfig().getString("condition");
		if (condition.startsWith("tpl:")) {// 使用模板方法
			Template t = this.templateService
					.loadByCode(condition.substring(4));
			if (t.isPureText()) {
				this.json = t
						.getContentEx(buildParams(this.getGridCondition()));
				this.resultPath = "/cn/bc/web/struts2/json.ftl";
				return "freemarker";
			} else {
				throw new CoreException("指定的模板不是纯文本模板:condition=" + condition);
			}
		} else if (condition.startsWith("action:")) {// 使用自定义的action条件方法
			this.resultPath = condition.substring(7);
			if (this.resultPath.startsWith("/"))
				this.resultPath = this.resultPath.substring(1);// 去除前缀"/"
			return "redirectAction";
		} else if (condition.startsWith("jsp:")) {// 使用自定义的jsp页面
			this.resultPath = condition.substring(7);
			return SUCCESS;
		} else {// 纯文本
			this.json = condition;
			this.resultPath = "/cn/bc/web/struts2/json.ftl";
			return "freemarker";
		}
	}

	public static Map<String, Object> buildParams(Condition c) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (c != null) {
			if (c instanceof MixCondition) {
				if (!((MixCondition) c).isEmpty()) {
					params.put("condition", c.getExpression());
				}
			} else {
				params.put("condition", c.getExpression());
			}
		}
		return params;
	}

	@Override
	protected String[] getGridSearchFields() {
		if (this.getConfig().has("search")) {
			try {
				return this.getConfig().getString("search").split(",");
			} catch (JSONException e) {
			}
		}
		return null;
	}

	@Override
	protected String getHtmlPageJs() {
		String defaultJs = "/bc/report/report.js";
		// 判断是否有额外的js、css文件配置
		if (!this.getConfig().has("js"))
			return this.getContextPath() + defaultJs;

		String js;
		try {
			js = this.getConfig().getString("js");
		} catch (JSONException e) {
			js = null;
		}
		if (js == null)
			return this.getContextPath() + defaultJs;
		else
			js = defaultJs + "," + js;

		// 附加上下文路径
		String[] jss = js.split(",");
		for (int i = 0; i < jss.length; i++) {
			if (!(jss[i].startsWith("http") || jss[i].startsWith("js:") || jss[i]
					.startsWith("css:"))) {
				jss[i] = this.getContextPath() + jss[i];
			}
		}
		return StringUtils.arrayToCommaDelimitedString(jss);
	}

	@Override
	protected String getHtmlPageInitMethod() {
		if (!this.getConfig().has("initMethod"))
			return null;
		try {
			return this.getConfig().getString("initMethod");
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	protected String getGridRowLabelExpression() {
		// 不处理
		return null;
	}

	@Override
	protected String getGridDblRowMethod() {
		// 取消双击处理函数
		return null;
	}

	@Override
	protected String getFormActionName() {
		// 没有表单处理
		return null;
	}
}