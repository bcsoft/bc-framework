/**
 * 
 */
package cn.bc.investigate.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.investigate.dao.QuestionDao;
import cn.bc.investigate.domain.Question;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 问题Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class QuestionDaoImpl extends HibernateCrudJpaDao<Question> implements
		QuestionDao {

	@SuppressWarnings("unused")
	private Object jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}