package cn.bc.template.service;

import cn.bc.core.service.CrudService;
import cn.bc.template.domain.TemplateType;

import java.util.List;
import java.util.Map;

/**
 * 模板类型Service接口
 *
 * @author dragon
 */
public interface TemplateTypeService extends CrudService<TemplateType> {
  /**
   * 加载一个模板类型
   *
   * @param code 编码
   * @return
   */
  public TemplateType loadByCode(String code);

  /**
   * 判断指定的编码是否唯一
   *
   * @param currentId 当前模板的id
   * @param code      当前模板要使用的编码
   * @return
   */
  public boolean isUniqueCode(Long currentId, String code);

  /**
   * 查找模板类型
   *
   * @param isEnabled 查找状态正常的模板类型：是，否
   * @return
   */
  public List<Map<String, String>> findTemplateTypeOption(boolean isEnabled);

  /**
   * 查找模板类型
   *
   * @param isEnabled 查找状态正常的模板类型：是，否
   * @return key:id和 value:name
   */
  public List<Map<String, String>> findTemplateTypeOptionRtnId(boolean isEnabled);

}
