package cn.bc.log.dao;

import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.log.domain.Worklog;

/**
 * 操作日志Dao接口
 * 
 * @author dragon
 * 
 */
public interface WorklogDao extends CrudDao<Worklog> {
	/**
	 * 获取指定文档的操作日志
	 * 
	 * @param ptype
	 *            文档类型
	 * @param pid
	 *            文档标识号
	 * @return
	 */
	List<Worklog> find(String ptype, String pid);
}
