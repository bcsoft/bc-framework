package cn.bc.log.dao.hibernate.jpa;

import cn.bc.log.dao.SyslogDao;
import cn.bc.log.domain.Syslog;
import cn.bc.orm.jpa.JpaCrudDao;

/**
 * 系统日志Dao接口的实现
 *
 * @author dragon
 */
public class SyslogDaoImpl extends JpaCrudDao<Syslog> implements SyslogDao {
}
