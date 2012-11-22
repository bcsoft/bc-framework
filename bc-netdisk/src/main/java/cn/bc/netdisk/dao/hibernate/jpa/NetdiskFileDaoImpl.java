package cn.bc.netdisk.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.netdisk.dao.NetdiskFileDao;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.netdisk.domain.NetdiskShare;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

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

	public Serializable[] getMyselfAndChildFileId(Long id) {
		// String sql = "with recursive n as("
		// + " select * from bc_netdisk_file where id =" + id + " union"
		// + " select f.* from bc_netdisk_file f,n where f.pid=n.id"
		// + " ) select string_agg(id||'',',') from n";

		String sql = "select getMyselfAndChildFileId(" + id + ")";
		logger.debug("sql" + sql + " id: " + id);
		List<Map<String, Object>> fileIds = this.jdbcTemplate.queryForList(sql);
		if (fileIds.get(0).get("getMyselfAndChildFileId") == null) {
			return null;
		} else {
			String ids = fileIds.get(0).get("getMyselfAndChildFileId")
					.toString();
			return cn.bc.core.util.StringUtils.stringArray2LongArray(ids
					.split(","));
		}

	}

	public Serializable[] getMyselfAndParentsFileId(Long id) {
		// String sql = "with recursive n as("
		// + " select * from bc_netdisk_file where id =" + id + " union"
		// + " select f.* from bc_netdisk_file f,n where f.id=n.pid"
		// + " ) select string_agg(id||'',',') from n";
		String sql = "select getMyselfAndParentsFileId(" + id + ")";
		List<Map<String, Object>> fileIds = this.jdbcTemplate.queryForList(sql);
		if (fileIds.get(0).get("getMyselfAndParentsFileId") == null) {
			return null;
		} else {
			String ids = fileIds.get(0).get("getMyselfAndParentsFileId")
					.toString();
			return cn.bc.core.util.StringUtils.stringArray2LongArray(ids
					.split(","));
		}

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

	public NetdiskShare getNetdiskShare(Long aid, Long pid) {
		NetdiskShare netdiskShare = null;
		List<?> list = null;
		String hql = "select n from NetdiskShare n where n.aid=? and n.netdiskFile.id=?";
		logger.debug("hql" + hql + " aid: " + aid + " pid： " + pid);
		list = this.getJpaTemplate().find(hql, new Object[] { aid, pid });
		if (list.size() > 0) {
			netdiskShare = (NetdiskShare) list.get(0);
			return netdiskShare;
		} else {
			return netdiskShare;
		}
	}

	public Serializable[] getUserSharFileId2All(Long userId) {
		String sql = "select getUserSharFileId2All(" + userId + ")";
		List<Map<String, Object>> fileIds = this.jdbcTemplate.queryForList(sql);
		if (fileIds.get(0).get("getUserSharFileId2All") == null) {
			return null;
		} else {
			String ids = fileIds.get(0).get("getUserSharFileId2All").toString();
			return cn.bc.core.util.StringUtils.stringArray2LongArray(ids
					.split(","));
		}
	}

	public List<Map<String, Object>> findOwnerFolder(Long ownerId, Long pid) {
		if (ownerId == null)
			return new ArrayList<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,f.pid,f.type_,f.name from bc_netdisk_file f");
		sql.append(" where f.type_ = ? and f.author_id = ? and f.folder_type = ?");
		args.add(NetdiskFile.TYPE_FOLDER);
		args.add(ownerId);
		args.add(NetdiskFile.FOLDER_TYPE_PERSONAL);
		if (pid == null) {
			sql.append(" and f.pid is null");
		} else {
			sql.append(" and f.pid = ?");
			args.add(pid);
		}
		sql.append(" order by f.status_,f.order_,f.file_date desc");
		sqlObject.setSql(sql.toString());
		sqlObject.setArgs(args);// 注入参数

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> m = new HashMap<String, Object>();
				int i = 0;
				m.put("id", rs[i++]);
				m.put("status", rs[i++]);
				m.put("pid", rs[i++]);
				m.put("type", rs[i++]);
				m.put("name", rs[i++]);
				return m;
			}
		});
		return new HibernateJpaNativeQuery<Map<String, Object>>(
				getJpaTemplate(), sqlObject).list();
	}

	public List<Map<String, Object>> findShareRootFolders(Long sharerId) {
		if (sharerId == null)
			return new ArrayList<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,f.pid,f.type_,f.name from bc_netdisk_file f");
		sql.append(" where f.type_ = ? and f.id in(select pid from bc_netdisk_share where aid =?)");
		sql.append(" and not exists(select 1 from bc_netdisk_file f1 where f.id=f1.id and f1.pid in (select pid from bc_netdisk_share where aid =?))");
		args.add(NetdiskFile.TYPE_FOLDER);
		args.add(sharerId);
		args.add(sharerId);
		sql.append(" order by f.status_,f.order_,f.file_date desc");
		sqlObject.setSql(sql.toString());
		sqlObject.setArgs(args);// 注入参数

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> m = new HashMap<String, Object>();
				int i = 0;
				m.put("id", rs[i++]);
				m.put("status", rs[i++]);
				m.put("pid", rs[i++]);
				m.put("type", rs[i++]);
				m.put("name", rs[i++]);
				return m;
			}
		});
		return new HibernateJpaNativeQuery<Map<String, Object>>(
				getJpaTemplate(), sqlObject).list();
	}

	public Long[] getUserPublicFileId() {
		String sql = "select getPublicFileId()";
		List<Map<String, Object>> fileIds = this.jdbcTemplate.queryForList(sql);
		if (fileIds.get(0).get("getPublicFileId") == null) {
			return null;
		} else {
			String ids = fileIds.get(0).get("getPublicFileId").toString();
			return cn.bc.core.util.StringUtils.stringArray2LongArray(ids
					.split(","));
		}
	}

	public List<Map<String, Object>> findChildFolder(Long pid) {
		if (pid == null)
			return new ArrayList<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,f.pid,f.type_,f.name from bc_netdisk_file f");
		sql.append(" where f.type_ = ? and f.pid = ?");
		args.add(NetdiskFile.TYPE_FOLDER);
		args.add(pid);
		sql.append(" order by f.status_,f.order_,f.file_date desc");
		sqlObject.setSql(sql.toString());
		sqlObject.setArgs(args);// 注入参数

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> m = new HashMap<String, Object>();
				int i = 0;
				m.put("id", rs[i++]);
				m.put("status", rs[i++]);
				m.put("pid", rs[i++]);
				m.put("type", rs[i++]);
				m.put("name", rs[i++]);
				return m;
			}
		});
		return new HibernateJpaNativeQuery<Map<String, Object>>(
				getJpaTemplate(), sqlObject).list();
	}

	public List<Map<String, Object>> findPublicRootFolder() {
		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,f.pid,f.type_,f.name from bc_netdisk_file f");
		sql.append(" where f.type_ = ? and f.folder_type = ? and pid is null");
		args.add(NetdiskFile.TYPE_FOLDER);
		args.add(NetdiskFile.FOLDER_TYPE_PUBLIC);
		sql.append(" order by f.status_,f.order_,f.file_date desc");
		sqlObject.setSql(sql.toString());
		sqlObject.setArgs(args);// 注入参数

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> m = new HashMap<String, Object>();
				int i = 0;
				m.put("id", rs[i++]);
				m.put("status", rs[i++]);
				m.put("pid", rs[i++]);
				m.put("type", rs[i++]);
				m.put("name", rs[i++]);
				return m;
			}
		});
		return new HibernateJpaNativeQuery<Map<String, Object>>(
				getJpaTemplate(), sqlObject).list();
	}
}