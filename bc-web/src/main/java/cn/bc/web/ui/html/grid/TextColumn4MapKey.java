package cn.bc.web.ui.html.grid;

/**
 * 针对从Map中获取数据的文本列
 * 
 * @author dragon
 * 
 */
public class TextColumn4MapKey extends AbstractColumn {
	/**
	 * @param id
	 *            数据库的列名，如"name"
	 * @param label
	 *            列头显示的名称
	 */
	public TextColumn4MapKey(String id, String label) {
		this.setId(id);
		this.setValueExpression(wrap4ve(id));// 默认表达式与id相同
		this.setLabel(label);
	}

	/**
	 * @param id
	 *            数据库的列名，如"name"
	 * @param label
	 *            列头显示的名称
	 * @param width
	 *            列的宽度
	 */
	public TextColumn4MapKey(String id, String label, int width) {
		this.setId(id);
		this.setValueExpression(wrap4ve(id));// 默认表达式与id相同
		this.setLabel(label);
		this.setWidth(width);
	}

	/**
	 * @param id
	 *            数据库的列名，如"t.name"
	 * @param valueExpression
	 *            从Map中获取值的Spring表达式的简略写法，如传入"name"，内部自动包装为"['name']"
	 * @param label
	 *            列头显示的名称
	 */
	public TextColumn4MapKey(String id, String valueExpression, String label) {
		this.setId(id);
		this.setValueExpression(wrap4ve(valueExpression));// 默认表达式与id相同
		this.setLabel(label);
	}

	/**
	 * @param id
	 *            数据库的列名，如"t.name"
	 * @param valueExpression
	 *            从Map中获取值的Spring表达式的简略写法，如传入"name"，内部自动包装为"['name']"
	 * @param label
	 *            列头显示的名称
	 * @param width
	 *            列的宽度
	 */
	public TextColumn4MapKey(String id, String valueExpression, String label,
			int width) {
		this.setId(id);
		this.setValueExpression(wrap4ve(valueExpression));// 默认表达式与id相同
		this.setLabel(label);
		this.setWidth(width);
	}

	/**
	 * 表达式的封装，如传入"name"，返回"['name']"
	 * 
	 * @param field
	 * @return
	 */
	private String wrap4ve(String field) {
		return "['" + field + "']";// 针对Map数据的封装
	}
}
