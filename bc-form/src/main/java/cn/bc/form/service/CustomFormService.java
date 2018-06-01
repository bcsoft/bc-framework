package cn.bc.form.service;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 自定义表单Service接口
 *
 * @author lbj & hwx
 */
public interface CustomFormService {

  /**
   * 保存自定义表单
   *
   * @param formInfoJO 表单信息
   * @param formDataJA 表单字段信息
   * @throws Exception
   */
  void save(JSONObject formInfoJO, JSONArray formDataJA) throws Exception;

  /**
   * 根据code、pid、type，删除自定义表单
   *
   * @param code 其他模块调用此模块时，使用的编码
   * @param pid  其他模块调用此模块时，该模块记录的id
   * @param type 类别
   */
  void delete(String type, long pid, String code);

  /**
   * 根据表单id删除自定义表单
   *
   * @param id 表单id
   */
  void delete(Long id);

  /**
   * 根据表单id数组，批量删除自定义表单
   *
   * @param ids 表单id数组
   */
  void delete(Long[] ids);
}
