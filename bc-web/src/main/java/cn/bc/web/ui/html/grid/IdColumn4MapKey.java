package cn.bc.web.ui.html.grid;

/**
 * 针对从Map中获取数据的id列
 * 
 * @author dragon
 * 
 */
public class IdColumn4MapKey extends IdColumn {
	private String originValueExpression;// 原始值表达式的值

	public String getOriginValueExpression() {
		return originValueExpression;
	}

	/**
	 * @param id
	 *            数据库的列名，如"t.name"
	 * @param valueExpression
	 *            从Map中获取值的Spring表达式的简略写法，如传入"name"，内部自动包装为"['name']"
	 */
	public IdColumn4MapKey(String id, String valueExpression) {
		super();
		this.setId(id);
		this.originValueExpression = valueExpression;
		this.setValueExpression(wrap4ve(valueExpression));
	}

	/**
	 * 表达式的封装，如传入"name"，返回"['name']"
	 * 
	 * @param field
	 * @return
	 */
	public static String wrap4ve(String field) {
		return "['" + field + "']";// 针对Map数据的封装
	}
}
