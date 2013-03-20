package cn.bc.email.dao.hibernate.jpa;

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
}
