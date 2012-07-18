/**
 * 
 */
package cn.bc.investigate.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.investigate.dao.GradeDao;
import cn.bc.investigate.domain.Grade;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 评分Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class GradeDaoImpl extends HibernateCrudJpaDao<Grade> implements
		GradeDao {

	@SuppressWarnings("unused")
	private Object jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}