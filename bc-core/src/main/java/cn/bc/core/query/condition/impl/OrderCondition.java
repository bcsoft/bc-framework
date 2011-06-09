/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.List;
import java.util.regex.Pattern;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;


/**
 * 排序条件
 * 
 * @author dragon
 * 
 */
public class OrderCondition implements Condition {
	private String orderBy;

	//internal use
	protected OrderCondition() {
	}

	public OrderCondition(String name) {
		if (name == null)
			throw new CoreException("name can not be null.");
		this.add(name, Direction.Asc);
	}

	public OrderCondition(String name, Direction direction) {
		if (name == null)
			throw new CoreException("name can not be null.");
		this.add(name, direction);
	}

	public OrderCondition add(OrderCondition orderCondition) {
		if (orderCondition == null)
			throw new CoreException("orderCondition can not be null.");
		String _orderBy = orderCondition.getExpression();
		_orderBy = Pattern.compile("order\\s*by\\s", Pattern.CASE_INSENSITIVE)
				.matcher(_orderBy).replaceAll("");
		//System.out.println(_orderBy);
		if (_orderBy != null && _orderBy.length() > 0) {
			if (this.orderBy == null) {
				this.orderBy = "";
			} else {
				this.orderBy = this.orderBy + ",";
			}
			this.orderBy = this.orderBy + _orderBy;
		}
		return this;
	}

	public OrderCondition add(String name, Direction direction) {
		if (this.orderBy == null) {
			this.orderBy = "";
		} else {
			this.orderBy = this.orderBy + ",";
		}
		this.orderBy = this.orderBy + name + " " + direction.toSymbol();
		return this;
	}

	public String getExpression() {
		return this.orderBy == null ? "" : this.orderBy;
	}

	public List<Object> getValues() {
		return null;
	}
}
