/**
 * 
 */
package cn.bc.report.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.Query;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 报表执行Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RunReportAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String code;// 报表模板的编码
	private JSONObject config;// 报表模板的详细配置

	private JSONObject getConfig() {
		if (config != null)
			return config;

		this.initConfig();
		return config;
	}

	@Override
	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		Toolbar tb = new Toolbar();
		tb.addButton(Toolbar.getDefaultEmptyToolbarButton());

		// 搜索按钮
		tb.addButton(getDefaultSearchToolbarButton());
		return tb;
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}

	@Override
	protected String getViewActionName() {
		return "runReport";
	}

	@Override
	protected String getHtmlPageTitle() {
		return "报表测试标题";
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// po.set
		return po;
	}

	@Override
	protected Query<Map<String, Object>> getQuery() {
		return new HibernateJpaNativeQuery<Map<String, Object>>(jpaTemplate,
				getSqlObject());
	}

	private List<Column> columns;

	@Override
	protected List<Column> getGridColumns() {
		if (columns != null)
			return columns;

		// 初始化列的配置信息
		JSONArray jColumns = null;
		try {
			jColumns = this.getConfig().getJSONArray("columns");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jcolumn = null;
		for (int i = 0; i < jColumns.length(); i++) {
			try {
				jcolumn = jColumns.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				jcolumn = null;
			}
		}

		columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.name", "name", "姓名", 100));
		columns.add(new TextColumn4MapKey("a.pname", "pname", "上级"));

		return columns;
	}

	@Override
	public String list() throws Exception {
		return super.list();
	}

	private void initConfig() {
		// 初始化配置信息
		// TODO 加载模板配置

		// TODO 获取详细配置信息
		try {
			this.config = new JSONObject(
					"{type: 'sql',"
							+ "columns: ["
							+ "    {id: 'a.id', label: 'ID', width: 40, el:'name'},"
							+ "    {id: 'a.name', label: '名称', width: 100, el:'name'},"
							+ "    {id: 'a.pname', label: '上级', el:'pname'}"
							+ "],"
							+ "sql: 'select a.id as id,a.name as name,a.pname as pname from bc_identity_actor a order by a.code',"
							+ "conditionForm: 'tpl:testConditionForm',"
							+ "exportTpl: 'tpl:testExportTemplate',"
							+ "ui: 'data',width: 600,height: 400}");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		// StringBuffer sql = new StringBuffer();
		// sql.append("select a.id as id,a.name as name,a.pname as pname");
		// sql.append(" from bc_identity_actor a");
		// sql.append(" order by a.code");
		try {
			sqlObject.setSql(this.getConfig().getString("sql"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 注入参数
		sqlObject.setArgs(null);

		// 获取所有列的值表达式
		final List<String> mapKeys = new ArrayList<String>();
		for (Column column : this.getGridColumns()) {
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
	protected String[] getGridSearchFields() {
		// 不处理
		return null;
	}

	@Override
	protected String getGridRowLabelExpression() {
		// 不处理
		return null;
	}

	@Override
	protected String getFormActionName() {
		// 没有表单处理
		return null;
	}

	@Override
	protected String getGridDblRowMethod() {
		// 取消双击处理函数
		return null;
	}
}