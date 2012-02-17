package cn.bc.placeorigin.dao;

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
}
