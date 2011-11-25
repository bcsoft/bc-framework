package cn.bc.sync.service;

import cn.bc.core.service.CrudService;
import cn.bc.sync.domain.SyncBase;

/**
 * 同步服务
 * 
 * @author rongjih
 * 
 */
public interface SyncBaseService extends CrudService<SyncBase> {
	/**
	 * 判断是否已有同步记录
	 * 
	 * @param syncType
	 *            同步信息的类型
	 * @param syncId
	 *            同步信息的标识符
	 * @return 如果曾经有相应的同步记录返回true，否则返回false
	 */
	boolean hadSync(String syncType, String syncId);

	/**
	 * 获取信息的现有同步记录
	 * 
	 * @param syncType
	 *            同步信息的类型
	 * @param syncId
	 *            同步信息的标识符
	 * @return
	 */
	SyncBase load(String syncType, String syncId);
}
