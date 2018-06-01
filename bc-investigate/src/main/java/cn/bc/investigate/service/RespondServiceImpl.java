/**
 *
 */
package cn.bc.investigate.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.investigate.dao.RespondDao;
import cn.bc.investigate.domain.Respond;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户作答的实现
 *
 * @author zxr
 */
public class RespondServiceImpl extends DefaultCrudService<Respond> implements
  RespondService {
  private RespondDao respondDao;

  public RespondDao getRespondDao() {
    return respondDao;
  }

  @Autowired
  public void setRespondDao(RespondDao respondDao) {
    this.respondDao = respondDao;
    this.setCrudDao(respondDao);
  }

}