/**
 * 
 */
package cn.bc.investigate.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.investigate.dao.QuestionItemDao;
import cn.bc.investigate.domain.QuestionItem;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 调查问卷Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class QuestionItemDaoImpl extends HibernateCrudJpaDao<QuestionItem>
		implements QuestionItemDao {

	@SuppressWarnings("unused")
	private Object jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}