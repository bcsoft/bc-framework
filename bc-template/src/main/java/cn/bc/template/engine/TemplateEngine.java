package cn.bc.template.engine;

import java.util.Map;

/**
 * 模板引擎渲染抽象化接口
 *
 * @author dragon
 */
public interface TemplateEngine<T extends Object> {
  /**
   * 模板渲染
   *
   * @param code 模板编码或模板内容，如果为模板编码则可以带版本号
   * @param args 格式化参数
   * @return 模板渲染后的结果
   */
  public T render(String code, Map<String, Object> args);
}
