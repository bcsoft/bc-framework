package cn.bc.web.ui.html.grid;

/**
 * 针对从Map中获取数据的文本列
 *
 * @author dragon
 */
public class TextColumn4MapKey extends TextColumn {
  private String originValueExpression;// 原始值表达式的值

  public String getOriginValueExpression() {
    return originValueExpression;
  }

  /**
   * @param id              数据库的列名，如"t.name"
   * @param valueExpression 从Map中获取值的Spring表达式的简略写法，如传入"name"，内部自动包装为"['name']"
   * @param label           列头显示的名称
   */
  public TextColumn4MapKey(String id, String valueExpression, String label) {
    this.setId(id);
    this.originValueExpression = valueExpression;
    this.setValueExpression(IdColumn4MapKey.wrap4ve(valueExpression));
    this.setLabel(label);
  }

  /**
   * @param id              数据库的列名，如"t.name"
   * @param valueExpression 从Map中获取值的Spring表达式的简略写法，如传入"name"，内部自动包装为"['name']"
   * @param label           列头显示的名称
   * @param width           列的宽度
   */
  public TextColumn4MapKey(String id, String valueExpression, String label,
                           int width) {
    this.setId(id);
    this.originValueExpression = valueExpression;
    this.setValueExpression(IdColumn4MapKey.wrap4ve(valueExpression));
    this.setLabel(label);
    this.setWidth(width);
  }
}
