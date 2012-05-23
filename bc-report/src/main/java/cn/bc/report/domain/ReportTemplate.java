/**
 * 
 */
package cn.bc.report.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.MixCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.TemplateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.docs.domain.Attach;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.FileEntityImpl;
import cn.bc.report.service.ReportService;
import cn.bc.template.domain.Template;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridExporter;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;

/**
 * 报表模板
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_REPORT_TEMPLATE")
public class ReportTemplate extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(ReportTemplate.class);

	private int status;// 状态：0-正常,1-禁用
	private String orderNo;// 排序号
	private String category;// 所属分类，如"营运系统/发票统计"
	private String name;// 名称
	private String code;// 编码，全局唯一
	private String desc;// 备注
	/**
	 * 详细配置
	 * <p>
	 * 必须使用标准的Json格式进行配置，格式为：{columns:[...],sql:"...",condition:"...",search:
	 * "...",export:"...",width:100,height:100}，各个参数详细说明如下：
	 * </p>
	 * <ul>
	 * <li>columns -
	 * 列配置，数组类型，数组元素为标准json格式，格式为：{type:"id",id:"...",label:"...",width
	 * :100,el:"..."}，各参数说明如下;
	 * <ul>
	 * <li>
	 * type - [可选]列类型，如果为id列，务必配置为第一个元素，且指定type:"id"，内部将会使用IdColumn4MapKey类实现；
	 * 没有指定type，则内部默认使用TextColumn4MapKey类来实现</li>
	 * <li>
	 * id - 列标识，对应sql语句中的选择项，如"select t.name from XX t"中的"t.name"</li>
	 * <li>
	 * label - 列头显示的标题文字</li>
	 * <li>
	 * width - 列的宽度</li>
	 * <li>
	 * el - 获取列值的spring表达式简写，由于列内部默认使用了TextColumn4MapKey的实现类，故一般对应Map中的键值</li>
	 * </ul>
	 * <li>
	 * sql -
	 * 获取报表数据的sql语句，可以直接撰写标准的sql语句，也可以配置为从模板库中获取（格式为"tpl:[模板的编码][:模板的版本号]"，
	 * 没有版本号时使用当前版本）； 当使用到搜索功能时，可以在模板中使用特殊的${condition}参数进行参数控制，如
	 * "select t.name from XX t where id=1 $if{condition != null}and ${condition}$end"
	 * ，当condition的值为"t.type>3"时，最终的sql为
	 * "select t.name from XX t where id=1 and t.type>3"
	 * ；当condition为空时，最终的sql为"select t.name from XX t where id=1"</li>
	 * </li>
	 * <li>
	 * condition - 报表高级搜索条件的配置，支持4种配置模式：
	 * <ul>
	 * <li>直接写html代码</li>
	 * <li>从模板库中获取html代码，格式为"tpl:[模板的编码][:模板的版本号]"，没有版本号时使用当前版本</li>
	 * <li>使用预定义的jsp页面，格式为"jsp:[文件路径]"，如"jsp:/path/to/conditions.jsp"，路径必须带前缀"/"
	 * </li>
	 * <li>使用预定义的action请求，格式为"action:[action路径]"，如"action:/path/to/yourAction"</li>
	 * </ul>
	 * </li>
	 * <li>
	 * export - 导出报表数据的Excel模板配置，支持2种配置模式：
	 * <ul>
	 * <li>从模板库中获取Excel模板，格式为"tpl:[模板的编码][:模板的版本号]"，没有版本号时使用当前版本</li>
	 * <li>直接指定Excel文件的路径"，如"path/to/excel.xls
	 * "，路径是相对于Attach.DATA_REAL_PATH下的相对路径，不能带前缀"/"</li>
	 * </ul>
	 * </li>
	 * <li>
	 * search - 模糊搜索条件对应的sql查询项，多个项间用逗号连接，如"t.name,t.code"</li>
	 * <li>
	 * width - 报表视图的宽度</li>
	 * <li>
	 * height - 报表视图的高度</li>
	 * <li>
	 * paging - [可选]报表视图是否分页，true或false，默认不分页</li>
	 * <li>
	 * js - [可选]附加特殊的js文件，如"/path/to/my.js"，路径必须带前缀"/"，多个路径用逗号连接</li>
	 * <li>
	 * initMethod - [可选]页面加载后所调用js文件中的初始化方法名称</li>
	 * <li>
	 * tb -
	 * [可选]自定义工具条按钮，数组类型，数组元素为标准json格式，格式为：{text:"...",id:"...",click:"...",
	 * action:"...",icon:"...",title:"...",callback:"..."}，各参数说明如下;
	 * <ul>
	 * <li>
	 * text - 按钮显示的名称</li>
	 * <li>
	 * click - 点击按钮调用的处理函数的名称</li>
	 * <li>
	 * id - [可选]按钮标识</li>
	 * <li>
	 * icon - [可选]按钮图标的样式</li>
	 * <li>
	 * title - [可选]按钮的鼠标提示信息</li>
	 * <li>
	 * action - [可选]内置antion的名称</li>
	 * <li>
	 * callback - [可选]点击按钮的回调函数</li>
	 * </ul>
	 * </li>
	 * </ul>
	 */
	private String config;
	private Set<Actor> users;// 使用人，为空代表所有人均可使用

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "BC_REPORT_TEMPLATE_ACTOR", joinColumns = @JoinColumn(name = "TID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "AID", referencedColumnName = "ID"))
	@OrderBy("orderNo asc")
	public Set<Actor> getUsers() {
		return users;
	}

	public void setUsers(Set<Actor> users) {
		this.users = users;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@javax.persistence.Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@javax.persistence.Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String order) {
		this.orderNo = order;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@javax.persistence.Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
		this.configJson = null;
	}

	private JSONObject configJson;// 缓存变量

	/**
	 * 获取配置的json对象
	 * 
	 * @return
	 */
	@Transient
	public JSONObject getConfigJson() {
		if (configJson != null)
			return configJson;

		if (this.getConfig() == null || this.getConfig().length() == 0) {
			this.configJson = null;
			return this.configJson;
		}

		try {
			configJson = new JSONObject(this.getConfig().replaceAll("\\s", " "));// 替换换行、回车等符号为空格
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			this.configJson = null;
		}
		return configJson;
	}

	private List<Column> columns;// 缓存变量

	/**
	 * 获取列配置
	 * 
	 * @return
	 */
	@Transient
	public List<Column> getConfigColumns() {
		if (columns != null)
			return columns;

		JSONObject config = this.getConfigJson();
		List<Column> columns = new ArrayList<Column>();
		if (config == null || !config.has("columns"))
			return columns;

		// 初始化列的配置信息
		JSONArray jColumns = null;
		try {
			jColumns = config.getJSONArray("columns");
		} catch (JSONException e) {
			throw new CoreException(e.getMessage(), e);
		}
		JSONObject jColumn = null;
		for (int i = 0; i < jColumns.length(); i++) {
			try {
				jColumn = jColumns.getJSONObject(i);
				if (jColumn.has("type")
						&& "id".equals(jColumn.getString("type"))) {// IdColumn4MapKey列
					columns.add(new IdColumn4MapKey(jColumn.getString("id"),
							jColumn.has("el") ? jColumn.getString("el")
									: jColumn.getString("id")));
				} else {// 默认使用TextColumn4MapKey列
					columns.add(new TextColumn4MapKey(jColumn.getString("id"),
							jColumn.has("el") ? jColumn.getString("el")
									: jColumn.getString("id"), jColumn
									.getString("label"),
							jColumn.has("width") ? jColumn.getInt("width") : 0));
				}
			} catch (JSONException e) {
				throw new CoreException(e.getMessage());
			}
		}
		return columns;
	}

	/**
	 * 获取配置的导出文件模板流
	 * 
	 * @param reportService
	 * @return
	 */
	public InputStream getConfigExportTemplate(ReportService reportService) {
		JSONObject config = this.getConfigJson();
		if (config == null || !config.has("export"))
			return null;

		try {
			String export = config.getString("export");
			if (export.startsWith("tpl:")) {
				Template tpl = reportService.loadTemplate(export.substring(4));
				if (tpl != null) {
					return tpl.getInputStream();
				} else {
					throw new CoreException("template is not exists:export="
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
			throw new CoreException(e.getMessage(), e);
		}
	}

	public SqlObject<Map<String, Object>> getConfigSqlObject(
			ReportService reportService, Condition condition) {
		JSONObject config = this.getConfigJson();
		if (config == null || !config.has("sql"))
			return null;

		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		try {
			// 解析sql
			String sql = config.getString("sql");
			Map<String, Object> params = buildParams(condition);
			if (sql.startsWith("tpl:")) {
				Template tpl = reportService.loadTemplate(sql.substring(4));
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
			if (condition != null) {
				sqlObject.setArgs(condition.getValues());
			}
		} catch (JSONException e) {
			throw new CoreException(e.getMessage(), e);
		}

		// 获取所有列的值表达式，作为数据映射的键值
		final List<String> mapKeys = new ArrayList<String>();
		for (Column column : this.getConfigColumns()) {
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

	public static Map<String, Object> buildParams(Condition condition) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (condition != null) {
			if (condition instanceof MixCondition) {
				if (!((MixCondition) condition).isEmpty()) {
					params.put("condition", condition.getExpression());
				}
			} else {
				params.put("condition", condition.getExpression());
			}
		}
		return params;
	}

	/**
	 * 执行报表并生成一个历史报表对象
	 * 
	 * @param reportService
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public ReportHistory run2history(ReportService reportService,
			Condition condition) throws Exception {
		// 初始化历史报表对象
		Calendar now = Calendar.getInstance();
		ReportHistory h = new ReportHistory();
		h.setStartDate(now);// 开始时间

		JSONObject config = this.getConfigJson();
		Assert.notNull(config, "报表模板的详细配置为空！");

		// 附件保存的二级子目录
		String dateDir = new SimpleDateFormat("yyyyMM").format(now.getTime());

		// 附件的扩展名
		String extension = config.has("extension") ? config
				.getString("extension") : "xls";

		// 附件保存的文件名(不含路径)
		String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(now
				.getTime()) + "." + extension;

		// 附件保存的绝对路径
		String realpath = Attach.DATA_REAL_PATH + "/"
				+ ReportHistory.DATA_SUB_PATH + "/" + dateDir + "/" + fileName;

		// 创建附件保存的目录
		File file = new File(realpath);
		if (!file.getParentFile().exists()) {
			if (logger.isWarnEnabled()) {
				logger.warn("mkdir=" + file.getParentFile().getAbsolutePath());
			}
			file.getParentFile().mkdirs();
		}

		// 导出报表结果到文件
		List<Column> columns = this.getConfigColumns();
		GridExporter exporter = new GridExporter();
		exporter.setIdLabel("序号");
		exporter.setTitle(this.getName());
		exporter.setColumns(columns);// 列配置
		exporter.setData(reportService.createSqlQuery(
				this.getConfigSqlObject(reportService, condition)).list());// 数据
		exporter.setTemplateFile(this.getConfigExportTemplate(reportService));// 导出数据的模板
		FileOutputStream out = new FileOutputStream(file);
		if (logger.isDebugEnabled())
			logger.debug("runReportTemplate:"
					+ DateUtils.getWasteTime(now.getTime()));
		exporter.exportTo(out);

		// 历史报表参数
		h.setEndDate(Calendar.getInstance());// 完成时间
		h.setSuccess(true);
		h.setCategory(this.getCategory());// 分类
		h.setPath(dateDir + "/" + fileName); // 附件路径
		h.setSubject(this.getName());// 标题
		h.setSourceType("用户生成");
		h.setSourceId(this.getId());

		return h;
	}
}
