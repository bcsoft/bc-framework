package cn.bc.web.ui.html.grid;

import cn.bc.core.query.condition.Direction;
import cn.bc.web.formater.Formater;

public interface Column {
	/**
	 * @return 是否为可排序列
	 */
	boolean isSortable();
	Column setSortable(boolean sortable);
	
	/**
	 * @return 可排序列的排序方向
	 */
	Direction getDir();
	Column setDir(Direction dir);
	
	/**
	 * @return 列宽
	 */
	int getWidth();
	Column setWidth(int width);

	/**
	 * @return 列的标识ID
	 */
	String getId();
	Column setId(String id);

	/**
	 * @return 显示的名称
	 */
	String getLabel();
	Column setLabel(String label);
	
	/**
	 * @return 表达式
	 */
	String getExpression();
	Column setExpression(String expression);
	
	/**
	 * 值的格式化处理器
	 * @return
	 */
	Formater getFormater();
	Column setFormater(Formater formater);
}
