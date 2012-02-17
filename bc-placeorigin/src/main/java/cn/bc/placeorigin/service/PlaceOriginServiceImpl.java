package cn.bc.placeorigin.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.placeorigin.dao.PlaceOriginDao;
import cn.bc.placeorigin.domain.PlaceOrigin;

/**
 * 籍贯Service接口的实现
 * 
 * @author lbj
 * 
 */
public class PlaceOriginServiceImpl extends DefaultCrudService<PlaceOrigin>
		implements PlaceOriginService {
	 private PlaceOriginDao placeOriginDao;

	@Autowired
	public void setPlaceOriginDao(PlaceOriginDao placeOriginDao) {
		 this.placeOriginDao = placeOriginDao;
		this.setCrudDao(placeOriginDao);
	}

	public String findPname(Long pid) {
		PlaceOrigin p=this.placeOriginDao.findPname(pid);
		if(p!=null){
			return p.getName();
		}
		return null;
	}

}
