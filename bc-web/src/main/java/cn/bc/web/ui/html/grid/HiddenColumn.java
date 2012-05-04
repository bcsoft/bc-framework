package cn.bc.web.ui.html.grid;

import cn.bc.core.query.condition.Direction;
import cn.bc.web.formater.Formater;

/**
 * 隐藏列
 * 
 * @author rongjih
 * 
 */
public class HiddenColumn implements Column {
	private String id;
	private String valueExpression;

	/**
	 * @param id
	 *            列名，如"name"，同时json表达式中的键名
	 * @param valueExpression
	 *            获取值的表达式
	 */
	public HiddenColumn(String id, String valueExpression) {
		this.id = id;
		this.valueExpression = valueExpression;
	}

	public String getId() {
		return id;
	}

	public Column setId(String id) {
		this.id = id;
		return this;
	}

	public String getValueExpression() {
		return valueExpression;
	}

	public Column setValueExpression(String valueExpression) {
		this.valueExpression = valueExpression;
		return this;
	}

	public Formater<? extends Object> getValueFormater() {
		return null;
	}

	public Column setValueFormater(Formater<? extends Object> valueFormater) {
		// do nothing
		return this;
	}

	public boolean isUseTitleFromLabel() {
		throw new RuntimeException("unsupport method");
	}

	public Column setUseTitleFromLabel(boolean useTitleFromLabel) {
		throw new RuntimeException("unsupport method");
	}

	public boolean isSortable() {
		throw new RuntimeException("unsupport method");
	}

	public Column setSortable(boolean sortable) {
		throw new RuntimeException("unsupport method");
	}

	public Direction getDir() {
		throw new RuntimeException("unsupport method");
	}

	public Column setDir(Direction dir) {
		throw new RuntimeException("unsupport method");
	}

	public int getWidth() {
		throw new RuntimeException("unsupport method");
	}

	public Column setWidth(int width) {
		throw new RuntimeException("unsupport method");
	}

	public String getLabel() {
		throw new RuntimeException("unsupport method");
	}

	public Column setLabel(String label) {
		throw new RuntimeException("unsupport method");
	}
}
