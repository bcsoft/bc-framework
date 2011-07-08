/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.ArrayList;
import java.util.List;

import cn.bc.core.query.condition.Condition;

/**
 * 以指定的方式合并条件
 * 
 * @author dragon
 * 
 */
public abstract class MixCondition implements Condition {
	protected List<Condition> conditions = new ArrayList<Condition>();
	protected OrderCondition orderCondition;
	protected String misSymbol;
	private boolean addBracket;// 是否将表达式用括号括住

	public MixCondition(String misSymbol) {
		this.misSymbol = misSymbol;
	}

	public MixCondition(String misSymbol, Condition... conditions) {
		this(misSymbol);
		this.add(conditions);
	}

	public boolean isAddBracket() {
		return addBracket;
	}

	public MixCondition setAddBracket(boolean addBracket) {
		this.addBracket = addBracket;
		return this;
	}

	/**
	 * 以and(和)方式合并给定的条件
	 * 
	 * @param conditions
	 *            要合并的条件
	 * @return
	 */
	public MixCondition add(Condition... conditions) {
		if (conditions != null) {
			for (Condition condition : conditions) {
				if (condition instanceof OrderCondition)
					this.addOrder((OrderCondition) condition);
				else if (condition != null)
					this.conditions.add(condition);
			}
		}
		return this;
	}
	private void addOrder(OrderCondition condition){
		if(orderCondition == null)
			this.orderCondition = new OrderCondition();
		this.orderCondition.add(condition);
	}

	public String getExpression() {
		if (conditions.isEmpty() && orderCondition == null) {
			return "";
			// }else if(conditions.size() == 1){
			// return conditions.get(0).getExpression();
		} else {
			StringBuffer mc = new StringBuffer();
			boolean add = false;
			if (conditions != null && !conditions.isEmpty()) {
				add = conditions.size() > 1 && isAddBracket();
				if (add)
					mc.append("(");
				int i = 0;
				for (Condition condition : conditions) {
					mc.append((i == 0 ? "" : " " + this.misSymbol + " ")
							+ condition.getExpression());
					i++;
				}
				if (add)
					mc.append(")");
			}

			if ((!add && mc.length() > 0) || (add && mc.length() > 2)) {
				if (this.orderCondition != null) {
					String order = this.orderCondition.getExpression();
					if (order != null && order.length() > 0)
						mc.append(" order by " + order);
				}

				return mc.toString();
			} else {
				if (this.orderCondition != null) {
					String order = this.orderCondition.getExpression();
					if (order != null && order.length() > 0) {
						return "order by " + order;
					} else {
						return "";
					}
				}
				return "";
			}
		}
	}

	public List<Object> getValues() {
		List<Object> args = new ArrayList<Object>();
		for (Condition condition : conditions) {
			if (condition.getValues() != null)
				args.addAll(condition.getValues());
		}
		return args;
	}
}
