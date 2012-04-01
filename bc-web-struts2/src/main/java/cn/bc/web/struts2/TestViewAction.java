package cn.bc.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;

/**
 * 测试用的JPA视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TestViewAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = -5470443095051689297L;

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select 1,logday,count(*) as c,string_agg(name,',') as names FROM (");
		sql.append("SELECT distinct h.actor_id as id,h.actor_name as name,to_char(l.file_date, 'YYYY-MM-DD') as logday");
		sql.append(" FROM bc_log_system l");
		sql.append(" inner join bc_identity_actor_history h on h.id=l.author_id");
		sql.append(" where l.type_ in (0,3) order by logday desc");
		sql.append(" ) ds");
		sql.append(" where ds.id > 11");
		sql.append(" group by logday");
		sql.append(" order by logday desc");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("logday", rs[i++]);
				map.put("c", rs[i++]);
				map.put("names", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return null;
	}

	@Override
	protected String[] getGridSearchFields() {
		return null;
	}

	@Override
	protected String getGridDblRowMethod() {
		return null;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("id", "id"));
		columns.add(new TextColumn4MapKey("logday", "logday", "登录日期", 130)
				.setDir(Direction.Desc).setValueFormater(
						new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("c", "c", "登录账号数", 130));
		columns.add(new TextColumn4MapKey("names", "names", "登录用户"));
		return columns;
	}

	@Override
	protected String getFormActionName() {
		return null;
	}
}
