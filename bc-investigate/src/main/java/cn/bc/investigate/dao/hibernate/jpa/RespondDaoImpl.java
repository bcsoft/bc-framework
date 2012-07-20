/**
 * 
 */
package cn.bc.investigate.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.investigate.dao.RespondDao;
import cn.bc.investigate.domain.Respond;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 用户作答Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class RespondDaoImpl extends HibernateCrudJpaDao<Respond> implements
RespondDao {

	@SuppressWarnings("unused")
	private Object jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}