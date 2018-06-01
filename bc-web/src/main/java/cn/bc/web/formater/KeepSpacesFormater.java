package cn.bc.web.formater;

/**
 * 保留空格并使用等宽字体的渲染
 */
public class KeepSpacesFormater extends AbstractFormater<Object> implements ExportText {
  @Override
  public Object format(Object context, Object value) {
    if (value == null || !(value instanceof String))
      return null;
    else
      return "<pre style='padding:0;margin:0'>" + value + "</pre>";
  }

  @Override
  public String getExportText(Object context, Object value) {
    return value == null ? null : value.toString();
  }
}