package cn.bc.placeorigin.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.placeorigin.dao.PlaceOriginDao;
import cn.bc.placeorigin.domain.PlaceOrigin;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 籍贯Service接口的实现
 *
 * @author lbj
 */
public class PlaceOriginServiceImpl extends DefaultCrudService<PlaceOrigin>
  implements PlaceOriginService {
  private PlaceOriginDao placeOriginDao;

  @Autowired
  public void setPlaceOriginDao(PlaceOriginDao placeOriginDao) {
    this.placeOriginDao = placeOriginDao;
    this.setCrudDao(placeOriginDao);
  }

  public PlaceOrigin loadByCode(String code) {
    return this.placeOriginDao.loadByCode(code);
  }

  public PlaceOrigin loadByIdentityCard(String cardNo) {
    return this.placeOriginDao.loadByIdentityCard(cardNo);
  }
}