/**
 * 
 */
package cn.bc.subscribe.web.struts2.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.acl.domain.AccessActor;
import cn.bc.acl.service.AccessService;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.NotInCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.query.condition.impl.QlCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.web.SystemContext;
import cn.bc.subscribe.domain.Subscribe;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 选择订阅Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectSubscribeAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	
	public String selected;//已经选择的订阅 id 多个逗号隔开
	
	private AccessService accessService;
	
	@Autowired
	public void setAccessService(AccessService accessService) {
		this.accessService = accessService;
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：排序号
		return new OrderCondition("s.order_");
	}
	
	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id,s.status_,s.type_,s.order_,s.subject,s.event_code");
		sql.append(" from bc_subscribe s");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
				map.put("type", rs[i++]);
				map.put("orderNo", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("eventCode", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}       

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("s.id", "id"));
		columns.add(new TextColumn4MapKey("s.order_", "orderNo",
				getText("subscribe.orderNo"), 80).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("s.subject", "subject",
				getText("subscribe.subject")).setUseTitleFromLabel(true));
		columns.add(new HiddenColumn4MapKey("subject", "subject"));
		columns.add(new HiddenColumn4MapKey("eventCode", "eventCode"));
		return columns;
	}
	
	@Override
	protected String[] getGridSearchFields() {
		return new String[]{"s.subject"};
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("subscribe.select.title");
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(400);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectSubscribe");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/subscribe/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition ac=new AndCondition();
		//状态必须为 使用中
		ac.add(new EqualsCondition("s.status_",BCConstants.STATUS_ENABLED));

		//排除已添加的订阅
		if(this.selected!=null && this.selected.length()>0)
			ac.add(new NotInCondition("s.id", 
					StringUtils.stringArray2LongArray(this.selected.split(","))));
		
		//访问监控已配置的权限
		ac.add(this.aclCondition());
		
		return ac;
	}
	
	private Condition aclCondition(){
		// 查找当前登录用户条件
		SystemContext context = (SystemContext) this.getContext();
		//当前用户
		Actor actor=context.getUser();
		List<AccessActor> aas =this.accessService.findByDocType(actor.getId(), Subscribe.class.getSimpleName());
		
		//还没配置访问监控的条件 ,排除访问监控配置了0位用户访问的情况
		String sql = "not exists(select 1 from bc_acl_doc a"
				+" inner join bc_acl_actor aa on aa.pid=a.id"
				+" where a.doc_id = (s.id||'')"
				+" and a.doc_type='"+Subscribe.class.getSimpleName()+"'"
				+" )";
		QlCondition qlc = new QlCondition(sql);
		
		if(aas == null||aas.size() == 0)
			return qlc;
		
		//用户拥有的访问监控权限的ids
		List<Long> ids=new ArrayList<Long>();
		
		for(AccessActor aa:aas){
			ids.add(Long.valueOf(aa.getAccessDoc().getDocId()));
		}
		
		if(ids == null||ids.size() == 0)return qlc;
		
		OrCondition or = new OrCondition();
		or.add(qlc);
		or.add(new InCondition("s.id",ids));
		or.setAddBracket(true);
		
		return or;
	}




	@Override
	protected String getClickOkMethod() {
		return "bc.subscribeSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}
}
