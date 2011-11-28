package cn.bc.sync.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.sync.domain.SyncBase;

/**
 * 同步Dao
 * 
 * @author rongjih
 * 
 */
public interface SyncBaseDao extends CrudDao<SyncBase> {
	/**
	 * 判断是否已有同步记录
	 * 
	 * @param syncType
	 *            同步信息的类型
	 * @param syncCode
	 *            同步信息的标识符
	 * @return 如果曾经有相应的同步记录返回true，否则返回false
	 */
	boolean hadSync(String syncType, String syncCode);

	/**
	 * 获取信息的现有同步记录
	 * 
	 * @param syncType
	 *            同步信息的类型
	 * @param syncCode
	 *            同步信息的标识符
	 * @return
	 */
	SyncBase load(String syncType, String syncCode);
}
