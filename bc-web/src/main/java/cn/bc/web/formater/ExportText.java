package cn.bc.web.formater;

/**
 * 导出文本接口
 *
 * @author dragon
 */
public interface ExportText {
  /**
   * 获取要导出的文本值
   *
   * @param context 上下文
   * @param value   原始值
   * @return
   */
  String getExportText(Object context, Object value);
}
