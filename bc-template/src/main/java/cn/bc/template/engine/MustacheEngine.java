package cn.bc.template.engine;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * 使用Mustache的模板引擎实现
 *
 * @author dragon
 */
public class MustacheEngine implements TemplateEngine<String> {
  /**
   * @param source 基于Mustache语法的模板
   * @param args   格式化参数
   * @return
   */
  public String render(String source, Map<String, Object> args) {
    StringWriter writer = new StringWriter();
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile(new StringReader(source), "default");
    mustache.execute(writer, args);
    writer.flush();
    return writer.toString();
  }
}