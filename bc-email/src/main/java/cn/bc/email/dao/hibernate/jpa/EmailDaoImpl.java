package cn.bc.email.dao.hibernate.jpa;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.email.dao.EmailDao;
import cn.bc.email.domain.Email;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 邮件Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class EmailDaoImpl extends HibernateCrudJpaDao<Email> implements
		EmailDao {
	protected final Log logger = LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void mark(Long[] ids, Long receiver_id, boolean read) {
		String sql = "UPDATE bc_email_to et SET read_=? WHERE et.receiver_id = ?";
		sql+=" and et.pid in(";
		
		for(int i=0;i<ids.length;i++){
			sql+=(i>0?",":"")+ids[i];
		}
		sql+=")";
		
		if (logger.isDebugEnabled()) {
			logger.debug("sql=" + sql);
			logger.debug("receiver_id=" + receiver_id);
			logger.debug("read=" + read);
		}
		
		this.jdbcTemplate.update(sql,
				new Object[] {read, receiver_id});
	}

	public void mark4read(Long receiver_id) {
		String sql = "UPDATE bc_email_to et SET read_=true WHERE et.receiver_id = ? and et.read_=false";
		if (logger.isDebugEnabled()) {
			logger.debug("sql=" + sql);
			logger.debug("receiver_id=" + receiver_id);
		}
		this.jdbcTemplate.update(sql,
				new Object[] {receiver_id});
	}
}
