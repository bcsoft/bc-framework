package cn.bc.netdisk.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

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
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

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

	public Serializable[] getChildIdsById(Long id) {
		// String sql = "with recursive n as("
		// + " select * from bc_netdisk_file where id =" + id + " union"
		// + " select f.* from bc_netdisk_file f,n where f.pid=n.id"
		// + " ) select string_agg(id||'',',') from n";

		String sql = "select getMyselfAndChildFileId(" + id + ")";
		logger.debug("sql" + sql + " id: " + id);
		List<Map<String, Object>> fileIds = this.jdbcTemplate.queryForList(sql);
		String ids = fileIds.get(0).get("getMyselfAndChildFileId").toString();
		return cn.bc.core.util.StringUtils
				.stringArray2LongArray(ids.split(","));

	}

	public Serializable[] getMyselfAndParentsFileId(Long id) {
		// String sql = "with recursive n as("
		// + " select * from bc_netdisk_file where id =" + id + " union"
		// + " select f.* from bc_netdisk_file f,n where f.id=n.pid"
		// + " ) select string_agg(id||'',',') from n";
		String sql = "select getMyselfAndParentsFileId(" + id + ")";
		List<Map<String, Object>> fileIds = this.jdbcTemplate.queryForList(sql);
		String ids = fileIds.get(0).get("getMyselfAndParentsFileId").toString();
		return cn.bc.core.util.StringUtils
				.stringArray2LongArray(ids.split(","));

	}

	public Serializable[] getUserSharFileId(Long id) {
		String sql = "select getUserSharFileId(" + id + ")";
		List<Map<String, Object>> fileIds = this.jdbcTemplate.queryForList(sql);
		if (fileIds.get(0).get("getUserSharFileId") == null) {
			return null;
		} else {
			String ids = fileIds.get(0).get("getUserSharFileId").toString();
			return cn.bc.core.util.StringUtils.stringArray2LongArray(ids
					.split(","));
		}
	}
}
