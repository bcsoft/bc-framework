package cn.bc.template.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.template.dao.TemplateDao;
import cn.bc.template.domain.Template;

/**
 * DAO接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateDaoImpl extends HibernateCrudJpaDao<Template> implements
		TemplateDao {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Template loadByCode(String code) {
		return this.createQuery().condition(new EqualsCondition("code", code))
				.singleResult();
	}

	public boolean isUnique(Long currentId, String code) {
		Condition c;
		if (currentId != null) {
			c = new EqualsCondition("code", code);
		} else {
			c = new AndCondition().add(new EqualsCondition("code", code)).add(
					new NotEqualsCondition("id", currentId));
		}

		return this.createQuery().condition(c).count() > 0;
	}

	// == 以下为待清理的接口 ==

	public List<Map<String, String>> findOneTemplateById(Integer id) {
		String sql = "select t.type_,t.name,t.template_file_name,t.content";
		sql += " from bc_template t";
		sql += " where t.id=?";
		return HibernateJpaNativeQuery.executeNativeSql(this.getJpaTemplate(),
				sql, new Object[] { id }, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> map = new HashMap<String, String>();
						int i = 0;
						map.put("type", rs[i++].toString());
						map.put("name", rs[i++].toString());
						Object otfname = rs[i++];
						Object ocontent = rs[i++];
						if (otfname != null) {
							map.put("tfname", otfname.toString());
						}
						if (ocontent != null) {
							map.put("content", ocontent.toString());
						}
						return map;
					}

				});
	}

	public List<Map<String, String>> findOneTemplateByCode(String code) {
		String sql = "select t.type_,t.name,t.template_file_name,t.content";
		sql += " from bc_template t";
		sql += " where t.code=?";
		return HibernateJpaNativeQuery.executeNativeSql(this.getJpaTemplate(),
				sql, new Object[] { code },
				new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> map = new HashMap<String, String>();
						int i = 0;
						map.put("type", rs[i++].toString());
						map.put("name", rs[i++].toString());
						Object otfname = rs[i++];
						Object ocontent = rs[i++];
						if (otfname != null) {
							map.put("tfname", otfname.toString());
						}
						if (ocontent != null) {
							map.put("content", ocontent.toString());
						}
						return map;
					}
				});
	}

	public int countTemplateFileName(String fileName) {
		int count = 0;
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*)").append(" from bc_template t")
				.append(" where t.template_file_name=?");
		args.add(fileName);
		// jdbc 查询
		count = this.jdbcTemplate.queryForInt(sql.toString(), args.toArray());
		return count;
	}
}
