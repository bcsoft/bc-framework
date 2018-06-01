package cn.bc.acl.service;

import cn.bc.acl.dao.AccessActorDao;
import cn.bc.acl.domain.AccessActor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 访问控制Service接口的默认实现
 *
 * @author dragon
 */
public class AccessServiceImpl implements AccessService {
  private AccessActorDao accessActorDao;
  private AccessActorService accessActorService;

  @Autowired
  public void setAccessActorService(AccessActorService accessActorService) {
    this.accessActorService = accessActorService;
  }


  @Autowired
  public void setAccessActorDao(AccessActorDao accessActorDao) {
    this.accessActorDao = accessActorDao;
  }


  public List<AccessActor> findByAid(Long id) {
    return this.accessActorDao.findByAid(id);
  }

  public List<AccessActor> findByDocType(Long id, String docType) {
    return this.accessActorDao.findByDocType(id, docType);
  }


  public List<AccessActor> findByDocId(Long id, String docId) {
    return this.accessActorDao.findByDocId(id, docId);
  }


  public List<AccessActor> findByDocIdType(Long id, String docId,
                                           String docType) {
    return this.accessActorDao.findByDocIdType(id, docId, docType);
  }

  public String findRolw(String docId, String docType, Long aid) {
    AccessActor aa = this.accessActorService.load(docId, docType, aid, null);
    if (aa == null) return null;
    return aa.getRole();
  }

  public boolean hasRolw(String docId, String docType, Long aid, String wildcard) {
    AccessActor aa = this.accessActorService.load(docId, docType, aid, wildcard);
    return aa != null;
  }


}