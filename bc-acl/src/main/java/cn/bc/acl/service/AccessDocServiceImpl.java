package cn.bc.acl.service;

import cn.bc.acl.dao.AccessDocDao;
import cn.bc.acl.domain.AccessActor;
import cn.bc.acl.domain.AccessDoc;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.ActorDao;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 访问对象Service接口的默认实现
 *
 * @author lbj
 */
public class AccessDocServiceImpl extends DefaultCrudService<AccessDoc> implements AccessDocService {
  private AccessDocDao accessDocDao;
  private AccessActorService accessActorService;
  private static Log logger = LogFactory.getLog(AccessDocServiceImpl.class);
  @Autowired
  private ActorDao actorDao;

  @Autowired
  public void setAccessDocDao(AccessDocDao accessDocDao) {
    this.setCrudDao(accessDocDao);
    this.accessDocDao = accessDocDao;
  }

  @Autowired
  public void setAccessActorService(AccessActorService accessActorService) {
    this.accessActorService = accessActorService;
  }

  public AccessDoc save4AccessActors(AccessDoc entity, List<AccessActor> accessActors) {
    if (entity == null) throw new CoreException("entity is null!");
    if (accessActors == null) throw new CoreException("accessActors is null!");

    if (!entity.isNew())
      //先清空已保存的访问者
      this.accessActorService.delete(this.accessActorService.findByPid(entity.getId()));

    //保存对象
    AccessDoc e = this.accessDocDao.save(entity);

    //循环保存访问者
    for (AccessActor aa : accessActors) {
      aa.setAccessDoc(e);
      this.accessActorService.save(aa);
    }

    return e;
  }

  @Override
  public void delete(Serializable id) {
    AccessDoc e = this.accessDocDao.load(id);
    //先清空已保存的访问者
    this.accessActorService.delete(this.accessActorService.findByPid(e.getId()));
    //删除对象
    this.accessDocDao.delete(id);
  }

  public AccessDoc load(String docId, String docType) {
    return this.accessDocDao.load(docId, docType);
  }

  public boolean saveConfig(String docType, String docId, String docName, Collection<Map<String, Object>> details) {
    // debug
    if (logger.isDebugEnabled()) {
      logger.debug("docType=" + docType);
      logger.debug("docId=" + docId);
      logger.debug("docName=" + docName);
      logger.debug("details=" + StringUtils.collectionToCommaDelimitedString(details));
    }

    Calendar now = Calendar.getInstance();
    ActorHistory author = SystemContextHolder.get().getUserHistory();

    // 获取现有配置，如果不存在就创建新的配置
    AccessDoc accessDoc = this.accessDocDao.load(docId, docType);
    List<AccessActor> currentAccessActors;// 原有的访问者配置
    if (accessDoc != null) {
      currentAccessActors = this.accessActorService.findByPid(accessDoc.getId());
      if (logger.isDebugEnabled())
        logger.debug("update exists AccessDoc.");
    } else {
      if (logger.isDebugEnabled())
        logger.debug("create new AccessDoc.");
      currentAccessActors = new ArrayList<AccessActor>();
      accessDoc = new AccessDoc();
      accessDoc.setDocType(docType);
      accessDoc.setDocId(docId);
      accessDoc.setDocName(docName);

      accessDoc.setFileDate(now);
      accessDoc.setAuthor(author);
    }
    accessDoc.setModifiedDate(now);
    accessDoc.setModifier(author);

    Map<String, AccessActor> currentAccessActorsMap = new HashMap<String, AccessActor>();
    for (AccessActor a : currentAccessActors) {
      currentAccessActorsMap.put(a.getActor().getId().toString(), a);
    }

    // 处理配置的变动
    String id, role;
    AccessActor aa;
    Actor actor;
    int i = 0;
    boolean changed = false;
    List<AccessActor> newAccessActors = new ArrayList<AccessActor>();// 新的访问者配置
    if (details != null && !details.isEmpty()) {
      for (Map<String, Object> m : details) {
        id = m.get("id").toString();
        role = m.get("role").toString();
        if (currentAccessActorsMap.containsKey(id)) {// 更新现有的
          aa = currentAccessActorsMap.get(id);
          if (!role.equals(aa.getRole()) || i != aa.getOrderNo()) {// 有修改
            aa.setOrderNo(i);
            aa.setRole(role);
            aa.setModifier(author);
            aa.setModifiedDate(now);
            changed = true;
          }
        } else {// 创建新的
          aa = new AccessActor();
          aa.setAccessDoc(accessDoc);
          aa.setOrderNo(i);
          aa.setRole(role);

          actor = this.actorDao.load(new Long(id));
          aa.setActor(actor);

          aa.setModifier(author);
          aa.setModifiedDate(now);
          changed = true;
        }
        newAccessActors.add(aa);
        i++;
      }
    }

    // 获取要删除的 AccessActor
    List<AccessActor> toDeleteAccessActors = new ArrayList<AccessActor>();
    for (AccessActor t : currentAccessActors) {
      if (!newAccessActors.contains(t)) {
        toDeleteAccessActors.add(t);
        changed = true;
      }
    }

    // 保存更改
    this.accessDocDao.save(accessDoc);
    this.accessActorService.delete(toDeleteAccessActors);
    this.accessActorService.save(newAccessActors);

    return changed;
  }
}