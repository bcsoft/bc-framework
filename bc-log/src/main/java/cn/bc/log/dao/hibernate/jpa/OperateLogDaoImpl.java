package cn.bc.log.dao.hibernate.jpa;

import java.util.List;

import cn.bc.log.dao.OperateLogDao;
import cn.bc.log.domain.OperateLog;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 操作日志Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class OperateLogDaoImpl extends HibernateCrudJpaDao<OperateLog> implements
		OperateLogDao {

	@SuppressWarnings("unchecked")
	public List<OperateLog> find(String ptype, String pid) {
		return this.getJpaTemplate().find(
				"from Worklog wl where wl.ptype=? and wl.pid=?", ptype, pid);
	}
}
