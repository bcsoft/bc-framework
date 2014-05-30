/**
 * 
 */
package cn.bc.report.web.struts2;

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
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.Query;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.report.chart.hightcharts.ChartOption;
import cn.bc.report.chart.hightcharts.Series;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.report.service.ReportService;
import cn.bc.template.domain.Template;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridExporter;
import cn.bc.web.ui.html.grid.GridFooter;
import cn.bc.web.ui.html.grid.PageSizeGroupButton;
import cn.bc.web.ui.html.grid.SeekGroupButton;
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
	private ReportService reportService;
	private ReportTemplate tpl;// 报表模板
	private JSONObject config;// 报表模板的详细配置

	@Autowired
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	private JSONObject getConfig() {
		if (config != null)
			return config;

		this.initConfig();
		return config;
	}

	private void initConfig() {
		// 初始化配置信息
		this.tpl = this.reportService.loadReportTemplate(this.code);
		if (this.tpl == null) {
			throw new CoreException("指定的模板不存在:code=" + this.code);
		}
		this.config = tpl.getConfigJson();

		// 避免空指针引用
		if (this.config == null) {
            this.config = new JSONObject();
        }

        // 默认提供存为历史报表按钮
        if(!this.config.has("history")){
            try {
                this.config.put("history", true);
            } catch (JSONException e) {
            }
        }
	}

	@Override
	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		// 判断是否生成工具条
		if (!(this.getConfig().has("search") || this.getConfig().has("tb")
                || this.getConfig().has("condition")
                || this.getConfig().has("history")))
			return null;

		try {
			Toolbar tb = new Toolbar();

			// 默认添加存为历史按钮
            if (!this.getConfig().has("history") || this.getConfig().getBoolean("history")) {
                tb.addButton(new ToolbarButton().setIcon("ui-icon-tag")
                    .setText("存为历史报表").setClick("bc.report.save2history"));
            }

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
	protected String getFormActionName() {
		return "report";
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
		return this.reportService.createSqlQuery(getSqlObject());
	}

	private List<Column> columns;

	@Override
	protected List<Column> getGridColumns() {
		if (columns != null)
			return columns;

		else
			return this.tpl.getConfigColumns();
	}

	public ChartOption chartOption;

	/**
	 * 执行报表
	 * 
	 * @return
	 * @throws Exception
	 */
	public String run() throws Exception {
		// 报表配置
		JSONObject config = this.getConfig();

		// 报表类型：默认为数据报表(type=data)
		String type = config.has("type") ? config.getString("type") : "data";
		if ("data".equals(type)) {// 数据报表
			if (config.has("paging") && config.getBoolean("paging")) {
				return this.paging();
			} else {
				return this.list();
			}
		} else if ("chart".equals(type)) {// 图形报表
			// 初始化图表配置
			chartOption = ChartOption.getDefaultChartOption(config
					.has("chartOption") ? config.getJSONObject("chartOption")
					: null);

			// 构建图表的数据信息
			initChartData(chartOption);
			return "chart";
		} else {
			throw new CoreException("unsupport report type");
		}
	}

	private void initChartData(ChartOption chartOption) throws JSONException {
		// 获取数据
		List<Map<String, Object>> list = this.findList();

		// 转换成图表的数据格式
		int xIndex = 0;
		int yIndex = 1;
		if (chartOption.getChart().isInverted()) {
			xIndex = 1;
			yIndex = 0;
		}
		boolean hasXAxisCategories = chartOption.getXAxis().has("categories");
		boolean hasSeriesData = false;
		if (chartOption.getSeries().length() > 0
				&& chartOption.getSeries().getJSONObject(0).has("data")) {
			hasSeriesData = true;
		}
		JSONArray xdata = new JSONArray();
		JSONArray ydata = new JSONArray();
		Object[] values;
		for (Map<String, Object> map : list) {
			values = map.values().toArray();
			xdata.put(values[xIndex]);
			ydata.put(values[yIndex]);
		}
		if (!hasXAxisCategories) {
			chartOption.getXAxis().setCategories(xdata);
		}
		if (!hasSeriesData) {
			if (chartOption.getSeries().length() > 0) {
				Series s = new Series(chartOption.getSeries().getJSONObject(0));
				s.setData(ydata);
				chartOption.getSeries().put(0, s);
			} else {
				Series s = new Series();
				s.setData(ydata);
				chartOption.getSeries().put(s);
			}
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
	public String data() throws Exception {
		this.initConfig();
		return super.data();
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
		try {
			this.reportService.runReportTemplate2history(this.code,
					this.getGridCondition());
			json.put("success", true);
			json.put("msg", "success");
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg", e.getMessage());
		}

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
    protected void extendGridExtrasData(JSONObject json) throws JSONException {
		super.extendGridExtrasData(json);
		if (this.code != null)
			json.put("code", this.code);
	}

	@Override
	protected GridExporter buileGridExporter(String title, String idLabel) {
		GridExporter exporter = super.buileGridExporter(title, idLabel);

		// 设置导出模板
		exporter.setTemplateFile(this.tpl
				.getConfigExportTemplate(this.reportService));

		return exporter;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		return this.tpl.getConfigSqlObject(reportService,
				this.getGridCondition());
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
			Template t = this.reportService
					.loadTemplate(condition.substring(4));
			if (t.isPureText()) {
				this.json = t.getContentEx(ReportTemplate.buildParams(this
						.getGridCondition()));
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
			this.resultPath = condition.substring(4);
			return SUCCESS;
		} else {// 纯文本
			this.json = condition;
			this.resultPath = "/cn/bc/web/struts2/json.ftl";
			return "freemarker";
		}
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
}