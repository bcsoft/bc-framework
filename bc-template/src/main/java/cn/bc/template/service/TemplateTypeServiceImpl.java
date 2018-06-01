package cn.bc.template.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.template.dao.TemplateTypeDao;
import cn.bc.template.domain.TemplateType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Service接口的实现
 *
 * @author lbj
 */
public class TemplateTypeServiceImpl extends DefaultCrudService<TemplateType> implements
  TemplateTypeService {
  //private static Log logger = LogFactory.getLog(TemplateTypeServiceImpl.class);

  private TemplateTypeDao templateTypeDao;

  @Autowired
  public void setTemplateTypeDao(TemplateTypeDao templateTypeDao) {
    this.templateTypeDao = templateTypeDao;
    this.setCrudDao(templateTypeDao);
  }

  public boolean isUniqueCode(Long currentId, String code) {
    return this.templateTypeDao.isUniqueCode(currentId, code);
  }

  public List<Map<String, String>> findTemplateTypeOption(boolean isEnabled) {
    return this.templateTypeDao.findTemplateTypeOption(isEnabled);
  }

  public TemplateType loadByCode(String code) {
    return this.templateTypeDao.loadByCode(code);
  }

  public List<Map<String, String>> findTemplateTypeOptionRtnId(
    boolean isEnabled) {
    return this.templateTypeDao.findTemplateTypeOptionRtnId(isEnabled);
  }
}
