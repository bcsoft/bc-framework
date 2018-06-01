package cn.bc.identity.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.ActorDao;
import cn.bc.identity.dao.ActorHistoryDao;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.dto.DepartmentByActorDto4MiniInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

/**
 * Actor隶属信息的变动历史Service接口的实现
 *
 * @author dragon
 */
public class ActorHistoryServiceImpl extends DefaultCrudService<ActorHistory> implements ActorHistoryService {
  private static Logger logger = LoggerFactory.getLogger(ActorHistoryServiceImpl.class);
  private ActorDao actorDao;
  private ActorHistoryDao actorHistoryDao;

  public void setActorDao(ActorDao actorDao) {
    this.actorDao = actorDao;
  }

  public void setActorHistoryDao(ActorHistoryDao actorHistoryDao) {
    this.actorHistoryDao = actorHistoryDao;
    this.setCrudDao(actorHistoryDao);
  }

  public ActorHistory loadCurrent(Long actorId) {
    ActorHistory current = actorHistoryDao.loadCurrent(actorId);
    if (current == null) {
      current = new ActorHistory();

      // 加载Actor
      Actor actor = this.actorDao.load(actorId);
      if (actor == null)
        return null;
      current.setActorType(actor.getType());
      current.setActorId(actorId);
      current.setName(actor.getName());
      current.setStartDate(null);
      current.setEndDate(null);
      current.setCreateDate(Calendar.getInstance());
      current.setPcode(actor.getPcode());
      current.setPname(actor.getPname());

      // 加载直接上级
      List<Actor> belongs = this.actorDao.findBelong(actorId,
        new Integer[]{Actor.TYPE_DEPARTMENT, Actor.TYPE_UNIT});
      if (belongs != null && !belongs.isEmpty()) {
        // TODO: 多个隶属关系的处理
        if (belongs.size() > 1)
          logger.warn("this actor has more than one belong.just use the first one! size="
            + belongs.size());
        Actor belong = belongs.get(0);
        current.setUpperId(belong.getId());
        current.setUpperName(belong.getName());

        // 加载所属单位
        Actor unit = this.loadUnit(belong);
        if (unit != null) {
          current.setUnitId(unit.getId());
          current.setUnitName(unit.getName());
        }
      }

      // 保存一个新的历史
      current = this.actorHistoryDao.save(current);
    }
    return current;
  }

  // 递归向上查找Actor所属的单位
  private Actor loadUnit(Actor belong) {
    if (belong.getType() == Actor.TYPE_UNIT)
      return belong;

    belong = this.actorDao.loadBelong(belong.getId(), new Integer[]{
      Actor.TYPE_DEPARTMENT, Actor.TYPE_UNIT});
    if (belong.getType() != Actor.TYPE_UNIT) {
      return loadUnit(belong);
    } else {
      return belong;
    }
  }

  public ActorHistory loadByCode(String actorCode) {
    return actorHistoryDao.loadByCode(actorCode);
  }

  public List<String> findNames(List<String> actorCode) {
    return actorHistoryDao.findNames(actorCode);
  }

  @Override
  public List<DepartmentByActorDto4MiniInfo> findDepartmentMiniInfoByActors(Long[] actorHistoryIds) {
    return actorHistoryDao.findDepartmentMiniInfoByActors(actorHistoryIds);
  }
}
