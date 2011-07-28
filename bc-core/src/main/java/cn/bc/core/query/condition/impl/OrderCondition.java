/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.ArrayList;
import java.util.List;

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
	private String name;
	private Direction direction;
	private List<OrderCondition> adds;

	// internal use
	protected OrderCondition() {
	}

	public OrderCondition(String name) {
		if (name == null)
			throw new CoreException("name can not be null.");
		this.name = name;
	}

	public OrderCondition(String name, Direction direction) {
		if (name == null)
			throw new CoreException("name can not be null.");
		this.name = name;
		this.direction = direction;
	}

	public OrderCondition add(OrderCondition orderCondition) {
		if (orderCondition == null)
			return this;// throw new
						// CoreException("orderCondition can not be null.");
		if (adds == null)
			adds = new ArrayList<OrderCondition>();
		adds.add(orderCondition);

		// String _orderBy = orderCondition.getExpression(alias);
		// _orderBy = Pattern.compile("order\\s*by\\s",
		// Pattern.CASE_INSENSITIVE)
		// .matcher(_orderBy).replaceAll("");
		// //System.out.println(_orderBy);
		// if (_orderBy != null && _orderBy.length() > 0) {
		// if (this.orderBy == null) {
		// this.orderBy = "";
		// } else {
		// this.orderBy = this.orderBy + ",";
		// }
		// this.orderBy = this.orderBy + _orderBy;
		// }
		return this;
	}

	public OrderCondition add(String name, Direction direction) {
		if (name == null)
			throw new CoreException("name can not be null.");
		this.add(new OrderCondition(name, direction));
		return this;
	}

	public String getExpression() {
		return getExpression(null);
	}

	public String getExpression(String alias) {
		String orderBy;
		if (this.name != null && this.name.length() > 0) {
			orderBy = this.name;
			if (alias != null && alias.length() > 0)
				orderBy = alias + "." + orderBy;

			if (this.direction == Direction.Asc
					|| this.direction == Direction.Desc)
				orderBy += " " + direction.toSymbol();
		}else{
			orderBy = "";
		}
		
		if (adds != null) {
			int i=0;
			for (OrderCondition o : adds) {
				if(i > 0 || orderBy.length() > 0)
					orderBy += "," + o.getExpression(alias);
				else
					orderBy += o.getExpression(alias);
				i++;
			}
		}

		return orderBy == null ? "" : orderBy;
	}

	public List<Object> getValues() {
		return null;
	}
}
