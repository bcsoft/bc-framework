package cn.bc.form.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.form.dao.FieldDao;
import cn.bc.form.domain.Field;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 表单字段Dao实现
 * 
 * @author hwx
 * 
 */
public class FieldDaoImpl extends HibernateCrudJpaDao<Field> implements
		FieldDao {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<Map<String, Object>> fieldList(Long formId) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select name_, value_ from BC_FORM_FIELD ");
		sql.append(" where pid=");

		if (formId != null && formId > 0) {
			sql.append(formId);
		} else {
			sql.append(-1);
		}

		List<Map<String, Object>> result = this.jdbcTemplate.queryForList(sql.toString());
		return result;
	}

}
