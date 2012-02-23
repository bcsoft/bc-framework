package cn.bc.placeorigin.dao;

import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.placeorigin.domain.PlaceOrigin;

/**
 * 籍贯Dao接口
 * 
 * @author lbj
 * 
 */
public interface PlaceOriginDao extends CrudDao<PlaceOrigin> {
	/**
	 * 通过PID找到上级的对象
	 * 
	 * @param pid
	 * @return 上级的对象
	 */
	PlaceOrigin findPname(Long pid);
	
	/**
	 * 根据编码获得籍贯对象
	 * 
	 * @param core 编码
	 * @return 保存籍贯对象的集合
	 */
	List<PlaceOrigin> findPlaceOrigin(String code);
}
