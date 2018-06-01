package cn.bc.form.service;

import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.form.dao.FieldLogDao;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.FieldLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 审计日志Service
 *
 * @author hwx
 */

public class FieldLogServiceImpl extends DefaultCrudService<FieldLog> implements
  FieldLogService {
  private FieldLogDao fieldLogDao;

  @Autowired
  public void setFieldLogDao(FieldLogDao fieldLogDao) {
    this.setCrudDao(fieldLogDao);
    this.fieldLogDao = fieldLogDao;
  }

  public List<FieldLog> findList(Field field) {
    return this.createQuery().condition(new EqualsCondition("field", field)).list();
  }

  public void delete(List<FieldLog> fieldLogs) {
    if (fieldLogs != null && fieldLogs.size() != 0) {
      Long[] fieldLogIds = new Long[fieldLogs.size()];
      for (int i = 0; i < fieldLogs.size(); i++) {
        fieldLogIds[i] = fieldLogs.get(i).getId();
      }
      this.delete(fieldLogIds);
    }

  }
}
