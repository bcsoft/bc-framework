package cn.bc.placeorigin.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.placeorigin.domain.PlaceOrigin;

/**
 * 籍贯Service接口
 * 
 * @author dragon
 * 
 */
public interface PlaceOriginService extends CrudService<PlaceOrigin> {
	/**
	 * 通过PID找到上级名称
	 * 
	 * @param pid
	 * @return 上级的名称
	 */
	String findPname(Long pid);
	
	/**
	 * 根据编码获得籍贯对象
	 * 
	 * @param core 编码
	 * @return 保存籍贯对象的集合
	 */
	List<PlaceOrigin> findPlaceOrigin(String core);
}
