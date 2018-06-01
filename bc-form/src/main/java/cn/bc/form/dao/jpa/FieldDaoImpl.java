package cn.bc.form.dao.jpa;

import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.form.dao.FieldDao;
import cn.bc.form.domain.Field;
import cn.bc.orm.jpa.JpaCrudDao;
import org.springframework.util.Assert;

import java.util.List;

public class FieldDaoImpl extends JpaCrudDao<Field> implements FieldDao {
  public List<Field> findByParent(Long pid) {
    Assert.notNull(pid, "pid couldn't be empty");
    return this.createQuery().condition(new EqualsCondition("form.id", pid)).list();
  }
}