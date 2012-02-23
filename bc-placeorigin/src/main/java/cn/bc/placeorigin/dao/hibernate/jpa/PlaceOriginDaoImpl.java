package cn.bc.placeorigin.dao.hibernate.jpa;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.BCConstants;

import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.placeorigin.dao.PlaceOriginDao;
import cn.bc.placeorigin.domain.PlaceOrigin;

/**
 * 籍贯DAO接口的实现
 * 
 * @author lbj
 * 
 */
public class PlaceOriginDaoImpl extends HibernateCrudJpaDao<PlaceOrigin>
		implements PlaceOriginDao {
	private static Log logger = LogFactory.getLog(PlaceOriginDaoImpl.class);
	
	
	public PlaceOrigin findPname(Long pid) {
		PlaceOrigin placeOrigin=null;
		String hql="from PlaceOrigin where id=? and status=?";
		List<?> list=this.getJpaTemplate().find(hql,new Object[] { pid,new Integer(BCConstants.STATUS_ENABLED)});
		if(list.size()==1){
			placeOrigin=(PlaceOrigin) list.get(0);
			return placeOrigin;
		}else if(list.size()<1){
			return null;
		}else{
			placeOrigin=(PlaceOrigin) list.get(0);
			if (logger.isDebugEnabled()) {
				logger.debug("有两个上级籍贯名称，已选择第一个");
			}
			return placeOrigin;
		}		 
	}


	@SuppressWarnings("unchecked")
	public List<PlaceOrigin> findPlaceOrigin(String code) {
		List<PlaceOrigin> pList=null;
		String hql="from PlaceOrigin where code=? and status=?";
		pList=this.getJpaTemplate().find(hql,new Object[]{code,new Integer(BCConstants.STATUS_ENABLED)});
		return pList;
	}

}
