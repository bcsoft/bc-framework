package cn.bc.log.dao.hibernate.jpa;

import cn.bc.log.dao.SyslogDao;
import cn.bc.log.domain.Syslog;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 系统日志Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class SyslogDaoImpl extends HibernateCrudJpaDao<Syslog> implements SyslogDao {
}
