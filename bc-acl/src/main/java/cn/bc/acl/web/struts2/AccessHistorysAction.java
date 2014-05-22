package cn.bc.acl.web.struts2;

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
import org.springframework.stereotype.Controller;

import cn.bc.acl.domain.AccessActor;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.option.OptionConstants;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 访问历史视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AccessHistorysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(AccessHistorysAction.class);
	public Long pid;//访问对象id
	public Long ahid;//历史对象问题
	public Boolean isFromDoc=false;//判断是否从对象中查询
	
	private OptionService optionService;
	
	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}
	
	
	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("a.access_date",Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.doc_id h_doc_id,a.doc_type h_doc_type,a.doc_name h_doc_name,a.access_date,a.src,a.url,a.role");
		sql.append(",b.actor_name h_actor");
		sql.append(",c.doc_id,c.doc_type,c.doc_name");
		sql.append(" from bc_acl_history a");
		sql.append(" inner join bc_identity_actor_history b on b.id=a.ahid");
		sql.append(" left join bc_acl_doc c on c.id=a.pid");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("hDocId", rs[i++]);
				map.put("hDocType", rs[i++]);
				map.put("hDocName", rs[i++]);
				map.put("accessDate", rs[i++]);
				map.put("source", rs[i++]);
				map.put("url", rs[i++]);
				map.put("role", rs[i++]);
				map.put("hActor", rs[i++]);
				map.put("docId", rs[i++]);
				map.put("docType", rs[i++]);
				map.put("docName", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}
	

	@Override
	protected List<Column> getGridColumns() {
 		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.doc_name", "hDocName",
				getText("accessHistroy.docName"),250).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.doc_type", "hDocType",
				getText("accessHistroy.docType"),150).setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getCategorys())));
		columns.add(new TextColumn4MapKey("a.doc_id", "hDocId",
				getText("accessHistroy.docId"), 80).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.actor_name", "hActor",
				getText("accessHistroy.actor"), 60).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.access_date", "accessDate",
				getText("accessHistroy.accessTime"),120)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm"))
				.setUseTitleFromLabel(true));	
		columns.add(new TextColumn4MapKey("a.src", "source",
				getText("accessHistroy.source"), 60).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.url", "url",
				getText("accessHistroy.url")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.role", "role",
				getText("accessHistroy.ownRole"), 60).setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(){
					
					@SuppressWarnings("unchecked")
					@Override
					public Object format(Object context, Object value) {
						if(((Map<String, Object>)context).get("role")==null)return null;
						String role = ((Map<String, Object>)context).get("role").toString();
						String _role = "";
						if(AccessActor.ROLE_TRUE.equals(role.substring(role.length()-1))){
							_role = getRoleValues().get("1");
						}
						
						if(AccessActor.ROLE_TRUE.equals(role.substring(role.length()-2,role.length()-1))){
							if(_role.length()>0){
								_role+=",";
							}
							
							_role += getRoleValues().get("2");
						}
						
						return _role;
					}
				}));
		
		if(!this.isFromDoc){
			columns.add(new TextColumn4MapKey("c.doc_name", "docName",
					getText("accessHistroy.aclDocName"),250).setUseTitleFromLabel(true));
			columns.add(new TextColumn4MapKey("c.doc_type", "docType",
					getText("accessHistroy.aclDocType"),150).setUseTitleFromLabel(true)
					.setValueFormater(new KeyValueFormater(getCategorys())));
			columns.add(new TextColumn4MapKey("c.doc_id", "docId",
					getText("accessHistroy.aclDocId"), 80).setUseTitleFromLabel(true));	
		}
		
		return columns;
	}
	
	private Map<String, String> getRoleValues() {
		Map<String, String> m = new HashMap<String, String>();
		m.put("1",getText("accessControl.way.read"));
		m.put("2",getText("accessControl.way.edit"));
		return m;
	}
	
	private Map<String, String> getCategorys() {
		return optionService.findActiveOptionItemByGroupKey(OptionConstants.OPERATELOG_PTYPE);
	}


	@Override
	protected String getGridRowLabelExpression() {
		return null;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.src","a.doc_id","a.doc_type","a.doc_name","c.doc_id","c.doc_type","c.doc_name", "b.actor_name"};
	}

	@Override
	protected String getFormActionName() {
		return "accessHistory";
	}
	
	@Override
	protected String getGridDblRowMethod() {
		return "";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		tb.addButton(new ToolbarButton()
				.setText(getText("label.read")));
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());
		return tb;
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		AndCondition ac = new AndCondition();

		if(this.pid!=null){
			ac.add(new EqualsCondition("a.pid", this.pid));
		}
		
		if(this.ahid!=null){
			ac.add(new EqualsCondition("a.ahid", this.ahid));
		}
		
		return ac.isEmpty()?null:ac;
	}
	
	@Override
    protected void extendGridExtrasData(JSONObject json) throws JSONException {
		if(this.pid!=null){
			json.put("pid", this.pid);
		}
		
		if(this.ahid!=null){
			json.put("ahid", this.ahid);
		}
		
		json.put("isFromDoc",this.isFromDoc);
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
	
	public JSONArray categorys;// 所属模块

	@Override
	protected void initConditionsFrom() throws Exception {
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = optionService
				.findOptionItemByGroupKeys(new String[] {OptionConstants.OPERATELOG_PTYPE});
		
		categorys = OptionItem.toLabelValues(optionItems.get(OptionConstants.OPERATELOG_PTYPE));
	}

	// ==高级搜索代码结束==

	
}
