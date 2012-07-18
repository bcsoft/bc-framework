/**
 * 
 */
package cn.bc.investigate.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.investigate.dao.AnswerDao;
import cn.bc.investigate.domain.Answer;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 用户作答的内容Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class AnswerDaoImpl extends HibernateCrudJpaDao<Answer> implements
		AnswerDao {

	@SuppressWarnings("unused")
	private Object jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}