package cn.bc.log.dao.hibernate.jpa;

import cn.bc.log.dao.OperateLogDao;
import cn.bc.log.domain.OperateLog;
import cn.bc.orm.jpa.JpaCrudDao;

import java.util.List;

/**
 * 操作日志Dao接口的实现
 *
 * @author dragon
 */
public class OperateLogDaoImpl extends JpaCrudDao<OperateLog> implements OperateLogDao {
	public List<OperateLog> find(String ptype, String pid) {
		return executeQuery("from Worklog wl where wl.ptype=? and wl.pid=?", new Object[]{ptype, pid});
	}
}