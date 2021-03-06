package cn.bc.template.engine;

import cn.bc.core.util.FreeMarkerUtils;

import java.util.Map;

/**
 * 使用FreeMarker的模板引擎实现
 *
 * @author dragon
 */
public class FreeMarkerEngine implements TemplateEngine<String> {
  // private static Log logger = LogFactory.getLog(TemplateEngineImpl.class);

  /**
   * @param source 基于Freemarker语法的模板
   * @param args
   * @return
   */
  public String render(String source, Map<String, Object> args) {
    return FreeMarkerUtils.format(source, args);
  }
}