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
	protected OrderCondition orderCondition = new OrderCondition();
	protected String misSymbol;
	private boolean addBracket;//是否将表达式用括号括住

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

	public void setAddBracket(boolean addBracket) {
		this.addBracket = addBracket;
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
					orderCondition.add((OrderCondition) condition);
				else if (condition != null)
					this.conditions.add(condition);
			}
		}
		return this;
	}

	public String getExpression() {
		if (conditions.isEmpty()) {
			return "";
			// }else if(conditions.size() == 1){
			// return conditions.get(0).getExpression();
		} else {
			StringBuffer s = new StringBuffer();
			if (conditions.size() > 1 && isAddBracket())
				s.append("(");
			int i = 0;
			for (Condition condition : conditions) {
				s.append((i == 0 ? "" : " " + this.misSymbol + " ")
						+ condition.getExpression());
				i++;
			}
			if (conditions.size() > 1 && isAddBracket())
				s.append(")");
			String order = this.orderCondition.getExpression();
			if (order != null && order.length() > 0)
				s.append(" order by " + order);
			return s.toString();
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
