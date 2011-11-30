package cn.bc.log.dao.hibernate.jpa;

import java.util.List;

import cn.bc.log.dao.WorklogDao;
import cn.bc.log.domain.Worklog;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 工作日志Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class WorklogDaoImpl extends HibernateCrudJpaDao<Worklog> implements
		WorklogDao {

	@SuppressWarnings("unchecked")
	public List<Worklog> find(String ptype, String pid) {
		return this.getJpaTemplate().find(
				"from Worklog wl where wl.ptype=? and wl.pid=?", ptype, pid);
	}
}
