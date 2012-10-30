package cn.bc.netdisk.dao.hibernate.jpa;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.netdisk.dao.NetdiskFileDao;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 网络文件DAO接口的实现
 * 
 * @author zxr
 * 
 */
public class NetdiskFileDaoImpl extends HibernateCrudJpaDao<NetdiskFile>
		implements NetdiskFileDao {
	protected final Log logger = LogFactory.getLog(getClass());

	public NetdiskFile findNetdiskFileByName(String name, Long pid,
			Object typeFolder, String batchNo) {

		NetdiskFile netdiskFile = null;
		List<?> list = null;
		if (typeFolder == null && pid != null) {
			String hql = "select n from NetdiskFile n where n.name=? and n.pid=? and n.batchNo=?";
			list = this.getJpaTemplate().find(hql,
					new Object[] { name, pid, batchNo });
		} else if (pid == null && typeFolder != null) {
			String hql = "select n from NetdiskFile n where n.name=? and n.type=? and n.batchNo=?";
			list = this.getJpaTemplate().find(hql,
					new Object[] { name, typeFolder, batchNo });
		} else if (pid == null && typeFolder == null) {
			String hql = "select n from NetdiskFile n where n.name=? and n.batchNo=?";
			list = this.getJpaTemplate().find(hql,
					new Object[] { name, batchNo });
		} else {
			String hql = "select n from NetdiskFile n where n.name=? and n.pid=? and n.type=? and n.batchNo=?";
			list = this.getJpaTemplate().find(hql,
					new Object[] { name, pid, typeFolder, batchNo });
		}
		if (list.size() == 1) {
			netdiskFile = (NetdiskFile) list.get(0);
			return netdiskFile;
		} else if (list.size() == 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("没有该名字的文件！");
			}
			return null;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("存在两个以上该名字的文件！返回第一个！");
			}
			netdiskFile = (NetdiskFile) list.get(0);
			return netdiskFile;
		}

	}

}
