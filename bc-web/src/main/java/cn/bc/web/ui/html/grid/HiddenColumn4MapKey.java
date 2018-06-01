package cn.bc.web.ui.html.grid;

/**
 * 针对从Map中获取数据的隐藏列
 *
 * @author dragon
 */
public class HiddenColumn4MapKey extends HiddenColumn {
  /**
   * @param id              列名，如"name"
   * @param valueExpression 从Map中获取值的Spring表达式的简略写法，如传入"name"，内部自动包装为"['name']"
   */
  public HiddenColumn4MapKey(String id, String valueExpression) {
    super(id, IdColumn4MapKey.wrap4ve(valueExpression));
  }
}
