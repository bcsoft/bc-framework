package cn.bc.form.service;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.StringUtils;
import cn.bc.form.dao.FieldDao;
import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.Form;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 自定义表单Service
 *
 * @author hwx
 */

public class FormServiceImpl extends DefaultCrudService<Form> implements FormService {
  private FormDao formDao;
  @Autowired
  private FieldDao fieldDao;

  @Autowired
  public void setFormDao(FormDao formDao) {
    this.setCrudDao(formDao);
    this.formDao = formDao;
  }

  public Form load(String type, String code, Long pid, Float ver) {
    Assert.hasText(type, "type couldn't be empty");
    Assert.hasText(code, "code couldn't be empty");
    Assert.notNull(pid, "pid couldn't be empty");
    Assert.notNull(ver, "ver couldn't be empty");

    AndCondition and = new AndCondition();
    and.add(new EqualsCondition("type", type));
    and.add(new EqualsCondition("code", code));
    and.add(new EqualsCondition("pid", pid));
    and.add(new EqualsCondition("ver", ver));
    and.add(new OrderCondition("ver", Direction.Desc));
    Form form = this.createQuery().condition(and).singleResult();
    if (form != null) {
      form.setFields(this.fieldDao.findByParent(form.getId()));
    }
    return form;
  }

  public Form save(String type, String code, Long pid, Float ver, Map<String, Object> data) {
    ActorHistory modifier = SystemContextHolder.get().getUserHistory();
    Calendar now = Calendar.getInstance();
    // 获取现有表单，不存在就新建一个
    Form form = this.load(type, code, pid, ver);
    boolean isNew = form == null;
    if (isNew) {
      form = new Form();
      form.setType(type);
      form.setCode(code);
      form.setPid(pid);
      form.setVer(ver);
      form.setStatus(Form.STATUS_ENABLED);
      //form.setSubject((String)((Map<String, Object>)data.get("subject")).get("value"));
      //form.setTpl((String)((Map<String, Object>)data.get("tpl")).get("value"));

      SystemContext context = SystemContextHolder.get();
      form.setAuthor(context.getUserHistory());
      form.setFileDate(Calendar.getInstance());
    }
    form.setModifier(modifier);
    form.setModifiedDate(now);

    // 构建数据
    Map<String, Field> fmap = new HashMap<String, Field>();
    if (!isNew) {// 获取现有的字段数据
      for (Field ff : form.getFields()) {
        fmap.put(ff.getName(), ff);
      }
    }
    Field field;
    String fname, fvalue, ftype, fscope, flabel;
    Object cfg_o;
    Map<String, Object> cfg;
    Set<Field> changedFields = new HashSet<Field>();// 用于记录有变动的字段数据
    for (Map.Entry<String, Object> e : data.entrySet()) {
      fname = e.getKey();
      cfg_o = e.getValue();
      if (cfg_o instanceof Map) {// 详细配置：{value:"aa",type:"string",scope:"form"}
        cfg = (Map<String, Object>) cfg_o;
        fscope = cfg.containsKey("scope") ? (String) cfg.get("scope") : "field";
        fvalue = cfg.containsKey("value") ? (String) cfg.get("value") : null;
        ftype = cfg.containsKey("type") ? (String) cfg.get("type") : "string";
        if ("form".equalsIgnoreCase(fscope)) {// 表单属性处理
          try {
            BeanUtils.setProperty(form, fname, StringUtils.convertValueByType(ftype, fvalue));
          } catch (Exception e1) {
            throw new CoreException(e1);
          }
        } else {// 表单字段处理
          flabel = cfg.containsKey("label") ? (String) cfg.get("label") : null;
          field = getOrCreateField(form, fmap, fname);
          field.setType(ftype);
          field.setLabel(flabel);

          // 新建字段或更新字段值
          if (field.isNew() || (fvalue == null && field.getValue() != null)
            || !fvalue.equals(field.getValue())) {
            field.setValue(fvalue);
            changedFields.add(field);
          }
        }
      } else {// 简易配置：【字符串值】
        field = getOrCreateField(form, fmap, fname);
        field.setType("string");

        // 新建字段或更新字段值
        fvalue = (cfg_o != null ? cfg_o.toString() : null);
        if (field.isNew() || (fvalue == null && field.getValue() != null)
          || !fvalue.equals(field.getValue())) {
          field.setValue(fvalue);
          changedFields.add(field);
        }
      }
    }

    // 设置变更字段的最后更新人信息
    for (Field f : changedFields) {
      f.setUpdator(modifier);
      f.setUpdateTime(now);
    }

    // 保存
    this.formDao.save(form);
    this.fieldDao.save(changedFields);

    return form;
  }

  // 获取现有的field或创建一个新的
  private Field getOrCreateField(Form form, Map<String, Field> fmap, String name) {
    Field field;
    if (fmap.containsKey(name)) {// 现有
      field = fmap.get(name);
    } else {// 新建
      field = new Field();
      field.setForm(form);
      field.setName(name);
      fmap.put(name, field);
      form.getFields().add(field);
    }
    return field;
  }

  public void delete(String type, String code, Long pid, Float ver) {
    this.formDao.delete(type, code, pid, ver);
  }

  public Float getNewestVer(String type, String code, Long pid) {
    return this.formDao.getNewestVer(type, code, pid);
  }

  public Form findByTPC(String type, Long pid, String code) {
    AndCondition ac = new AndCondition();
    ac.add(new EqualsCondition("type", type));
    ac.add(new EqualsCondition("pid", pid));
    ac.add(new EqualsCondition("code", code));
    if (this.createQuery().condition(ac).count() == 0) {
      return null;
    } else {
      return this.createQuery().condition(ac).list().get(0);
    }

  }

  public List<Form> findList(String type, Long pid) {
    AndCondition ac = new AndCondition();
    ac.add(new EqualsCondition("type", type));
    ac.add(new EqualsCondition("pid", pid));
    if (this.createQuery().condition(ac).count() == 0) {
      return null;
    } else {
      return this.createQuery().condition(ac).list();
    }
  }

  public List<Form> findList(String type) {
    AndCondition ac = new AndCondition();
    ac.add(new EqualsCondition("type", type));
    if (this.createQuery().condition(ac).count() == 0) {
      return null;
    } else {
      return this.createQuery().condition(ac).list();
    }
  }
}