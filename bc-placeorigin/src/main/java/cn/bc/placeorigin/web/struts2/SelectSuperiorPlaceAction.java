package cn.bc.placeorigin.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.placeorigin.domain.PlaceOrigin;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择籍贯Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectSuperiorPlaceAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	public String types;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED);

	@Override
	protected String getClickOkMethod() {
		return "bc.superiorPlaceSelectDialog.clickOk";
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id as id,a.code as code, a.type_ as type,a.status_ as status");
		sql.append(",a.name as name,a.pname as pname");
		sql.append(" from bc_placeorigin a");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("code", rs[i++]);
				map.put("type", rs[i++]);
				map.put("status", rs[i++]);
				map.put("name", rs[i++]);
				map.put("pname", rs[i]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		// 类型
		columns.add(new TextColumn4MapKey("a.type_", "type",
				getText("placeorigin.type"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getTypes())));
		// 编码
		columns.add(new TextColumn4MapKey("a.code", "code",
			getText("placeorigin.code"), 80).setUseTitleFromLabel(true));
		// 名称
		columns.add(new TextColumn4MapKey("a.name", "name",
			getText("placeorigin.name"), 120).setUseTitleFromLabel(true));
		// 上级
		columns.add(new TextColumn4MapKey("p.name", "pname",
			getText("placeorigin.pname")).setUseTitleFromLabel(true));
		return columns;
	}

	// 类型值转换
	private Map<String, String> getTypes() {
		Map<String, String> mstatus = new HashMap<String, String>();
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_COUNTRY_LEVEL),
				getText("placeorigin.type.contry"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_PROVINCE_LEVEL),
				getText("placeorigin.type.province"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_PLACE_LEVEL),
				getText("placeorigin.type.place"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_COUNTY_LEVEL),
				getText("placeorigin.type.county"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_TOWNSHIP_LEVEL),
				getText("placeorigin.type.township"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_VILLAGE_LEVEL),
				getText("placeorigin.type.village"));
		return mstatus;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(500).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.code", "a.name","a.pname" };
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认的排序方法
		return new OrderCondition("a.code", Direction.Asc);
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectSuperiorPlace");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/placeOrigin/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition andCondition=new AndCondition();
		if(status!=null&&status.length()>0){
			andCondition.add(new EqualsCondition("a.status_", Integer.parseInt(status)));
		}
		
		if(types!=null&&types.length()>0){
			if(types.indexOf(",")==-1){
				andCondition.add(new EqualsCondition("a.type_", Integer.valueOf(types)));
			}else{
				andCondition.add(new InCondition("a.type_",StringUtils.stringArray2IntegerArray(types.split(","))));
			}
		}
		return andCondition;
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if(status!=null&&status.length()>0){
			json.put("status", status);
		}
		if(types!=null&&types.length()>0){
			json.put("types", types);
		}
		return json;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("selectSuperiorPlace.title");
	}
}