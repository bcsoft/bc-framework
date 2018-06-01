package cn.bc.form.service;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.form.dao.FieldDao;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.FieldLog;
import cn.bc.form.domain.Form;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.List;

/**
 * 表单字段Service
 *
 * @author hwx
 */

public class FieldServiceImpl extends DefaultCrudService<Field> implements FieldService {
  private FieldDao fieldDao;
  private FieldLogService fieldLogService;

  @Autowired
  public void setFieldLogService(FieldLogService fieldLogService) {
    this.fieldLogService = fieldLogService;
  }

  @Autowired
  public void setFromDao(FieldDao fieldDao) {
    this.setCrudDao(fieldDao);
    this.fieldDao = fieldDao;
  }

  public List<Field> findList(Form form) {
    return this.createQuery().condition(new EqualsCondition("form", form)).list();
  }

  public Field findByPidAndName(Form form, String name) {
    AndCondition ac = new AndCondition();
    ac.add(new EqualsCondition("form", form));
    ac.add(new EqualsCondition("name", name));
    if (this.createQuery().condition(ac).count() == 0) {
      return null;
    } else {
      return this.createQuery().condition(ac).list().get(0);
    }
  }

  @Override
  public Field save(Field entity) {
    ActorHistory actor = SystemContextHolder.get().getUserHistory();
    FieldLog fieldLog = new FieldLog();
    fieldLog.setField(entity);
    fieldLog.setValue(entity.getValue());
    fieldLog.setUpdator(actor);
    fieldLog.setUpdateTime(Calendar.getInstance());
    fieldLog.setBatchNo(DateUtils.formatCalendar(
      Calendar.getInstance(), "yyyyMMddHmmssSSSS"));
    this.fieldLogService.save(fieldLog);
    return super.save(entity);
  }
}
