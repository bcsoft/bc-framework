package cn.bc.desktop.service;

import cn.bc.core.service.CrudService;
import cn.bc.desktop.domain.Personal;

/**
 * 个人设置Service接口
 * 
 * @author dragon
 * 
 */
public interface PersonalService extends CrudService<Personal> {
	/**
	 * 获取全局配置信息
	 * @param actorId
	 * @return
	 */
	Personal loadGlobalConfig();
	
	/**
	 * 获取actor专用的配置信息
	 * @param actorId
	 * @return
	 */
	Personal loadByActor(Long actorId);

	/**
	 * 获取actor可用的配置信息
	 * @param actorId
	 * @param autoUseGlobal 如果actor没有专用的配置是否使用全局配置
	 * @return
	 */
	Personal loadByActor(Long actorId, boolean autoUseGlobal);
}
