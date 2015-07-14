/**
 *
 */
package cn.bc.log.web.struts2;

import cn.bc.db.jdbc.SqlObject;
import cn.bc.orm.jpa.JpaNativeQuery;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 系统日志统计Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SyslogStatsAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	public List<Object[]> data;

	@PersistenceContext
	protected EntityManager entityManager;

	// 按日统计登录帐号数
	public String statsByDay() throws Exception {
		// 获取统计信息
		data = new JpaNativeQuery<>(entityManager, getSqlObject()).list();

		return SUCCESS;
	}

	protected SqlObject<Object[]> getSqlObject() {
		SqlObject<Object[]> sqlObject = new SqlObject<>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select logday as logday,count(*) as count from (");
		sql.append("select distinct h.actor_id as id,h.actor_name as name,to_char(l.file_date, 'YYYY-MM-DD') as logday");
		sql.append(" from bc_log_system l");
		sql.append(" inner join bc_identity_actor_history h on h.id=l.author_id");
		sql.append(" where l.type_ in (0,3) order by logday desc");
		sql.append(" ) ds");
		sql.append(" group by logday");
		sql.append(" order by logday desc");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		return sqlObject;
	}
}