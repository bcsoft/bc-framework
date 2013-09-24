/**
 * 
 */
package cn.bc.db.jdbc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.OrderCondition;

/**
 * Sql查询对象的封装
 * 
 * @author dragon
 * 
 */
public class SqlObject<T extends Object> {
	private String sql;// sql查询语句,不能带where和order by语句
	private String select;// sql查询语句的"select ..."部分
	private String from;// sql查询语句的"from ..."部分
	private String where;// sql查询语句的"where ..."部分
	private String groupBy;// sql查询语句的"group by ..."部分
	private String orderBy;// sql查询语句的"order by ..."部分
	private List<Object> args;// 查询参数
	private RowMapper<T> rowMapper;// 数据映射器

	public SqlObject() {
	}

	public SqlObject(String sql, List<Object> args) {
		this.sql = sql;
		this.args = args;
	}

	public String getNativeSql(Condition condition) {
		if (condition == null)
			return getNativeSql();

		String expression = condition.getExpression();
		if (expression == null || expression.length() == 0)
			return getNativeSql();

		String t = "";
		if (sql != null) {
			if (condition instanceof OrderCondition) {
				t += sql + " order by " + expression;
			} else {
				if (expression.startsWith("order by")) {
					t += sql + " " + expression;
				} else {
					t += sql + " where " + expression;
				}
			}
		}else{
			return getSql(condition);
			//throw new CoreException("not implement!");
		}
		return t;
	}
	public String getNativeSql() {
		return sql;
	}

	/**
	 * 获取合并指定条件后的查询语句
	 * 
	 * @param condition
	 *            条件
	 * @return
	 */
	public String getSql(Condition condition) {
		if (condition == null)
			return getSql();

		String expression = condition.getExpression();
		if (expression == null || expression.length() == 0)
			return getSql();

		String t = "";
		if (sql != null) {
			String _sql = sql.replace("!!", "");
			if (condition instanceof OrderCondition) {
				t += _sql + " order by " + expression;
			} else {
				if (expression.startsWith("order by")) {
					t += _sql + " " + expression;
				} else {
					t += _sql + " where " + expression;
				}
			}
		} else {// 根据select、where、orderBy拼凑出sql
			if (select != null) {
				t += "select " + select;
			}
			if (from != null) {
				t += " from " + from;
			}
			if (condition instanceof OrderCondition) {
				if (where != null) {
					t += " where " + where;
				}
				if (orderBy != null) {
					t += " order by " + orderBy + "," + expression;
				} else {
					t += "order by " + expression;
				}
			} else {
				if (expression.startsWith("order by")) {
					if (where != null) {
						t += " where " + where;
					}
					if (orderBy != null) {
						t += " order by " + orderBy + ","
								+ expression.substring(8);
					} else {
						t += " " + expression;
					}
				} else {
					if (where != null) {
						t += " where " + where + " and " + expression;
					} else {
						t += " where " + expression;
					}
				}
			}
		}
		return t;
	}

	/**
	 * 获取整个查询语句
	 * 
	 * @return
	 */
	public String getSql() {
		if (sql != null) {
			return sql.replace("!!", "");
		}

		// 根据select、where、groupBy、orderBy拼凑出sql
		String t = "";
		if (select != null) {
			t += "select " + select;
		}
		if (from != null) {
			t += " from " + from;
		}
		if (where != null) {
			t += " where " + where;
		}
        if (groupBy != null) {
            t += " group by " + groupBy;
        }
        if (orderBy != null) {
            t += " order by " + orderBy;
        }

		return t;
	}

	public SqlObject<T> setSql(String sql) {
		this.sql = innerDealSql(sql);
		return this;
	}

	/**
	 * 将sql中的关键字select、from、where和order by不区分大小写转换为小写
	 * 
	 * @param sql
	 * @return
	 */
	private String innerDealSql(String sql) {
		if (sql == null)
			return sql;
		return sql.replaceAll("(?i)select", "select")
				.replaceAll("(?i)from", "from")
				.replaceAll("(?i)where", "where")
                .replaceAll("(?i)order by", "order by")
                .replaceAll("(?i)group by", "group by");
	}

	public List<Object> getArgs() {
		return args;
	}

	public SqlObject<T> setArgs(List<Object> args) {
		this.args = args;
		return this;
	}

	public RowMapper<T> getRowMapper() {
		return rowMapper;
	}

	public SqlObject<T> setRowMapper(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
		return this;
	}

	public SqlObject<T> setSelect(String select) {
		this.select = innerDealSql(select);
		return this;
	}

	public String getSelect() {
		return select;
	}

	public String getFrom() {
		return from;
	}

	public SqlObject<T> setFrom(String from) {
		this.from = innerDealSql(from);
		return this;
	}

	public SqlObject<T> setWhere(String where) {
		this.where = innerDealSql(where);
		return this;
	}

    public void setGroupBy(String groupBy) {
        this.groupBy = innerDealSql(groupBy);
    }

    public SqlObject<T> setOrderBy(String orderBy) {
		this.orderBy = innerDealSql(orderBy);
		return this;
	}

	public String getCountSql(Condition condition) {
		String sql = this.getNativeSql(condition);
		int selectIndex = sql.indexOf("!!select");
		int fromIndex = sql.indexOf("!!from");
		if (selectIndex != -1 && fromIndex != -1) {// 使用特殊标记的情况
			return removeOrderBy(sql.substring(0, selectIndex)
					+ " select count(*) " + sql.substring(fromIndex + 2));
		} else {
			return "select count(*) " + this.getFromWhereSql(condition);
		}
	}

	/**
	 * 获取合并指定条件后的"from...where..."部分的查询语句
	 * 
	 * @param condition
	 *            条件
	 * @return
	 */
	public String getFromWhereSql(Condition condition) {
		if (condition == null)
			return getFromWhereSql();

		String expression = condition.getExpression();
		if (expression == null || expression.length() == 0)
			return getFromWhereSql();

		String t = "";
		if (sql != null) {
			t = removeOrderBy(this.sql);
			if (t.startsWith("select")) {
				t = removeSelect(t);
			}
			if (expression.startsWith("order by")) {
				t += " " + expression;
			} else {
				t += " where " + expression;
			}
			t = removeOrderBy(t);
		} else {// 根据select、where、orderBy拼凑出sql
			if (from != null) {
				t += "from " + from;
			}
			if (condition instanceof OrderCondition) {
				if (where != null) {
					t += " where " + where;
				}
				if (orderBy != null) {
					t += " order by " + orderBy + "," + expression;
				} else {
					t += "order by " + expression;
				}
			} else {
				if (expression.startsWith("order by")) {
					if (where != null) {
						t += " where " + where;
					}
					if (orderBy != null) {
						t += " order by " + orderBy + ","
								+ expression.substring(8);
					} else {
						t += " " + expression;
					}
				} else {
					if (where != null) {
						t += " where " + where + " and " + expression;
					} else {
						t += " where " + expression;
					}
				}
			}
			t = removeOrderBy(t);
		}
		return t.replace("!!", "");
	}

	/**
	 * 获取查询语句的"from...where..."部分，不含"select..."和"order by..."部分
	 * <p>
	 * 是根据sql语句生成select count(*) ...的常用工具方法
	 * </p>
	 * 
	 * @return
	 */
	public String getFromWhereSql() {
		String t;
		if (sql != null) {
			t = removeOrderBy(this.sql);
			if (t.startsWith("select")) {
				t = removeSelect(t);
			}
		} else {
			t = "";
			if (from != null) {
				if (from.startsWith("from")) {
					t += from;
				} else {
					t += "from " + from;
				}
			}
			if (where != null) {
				if (where.startsWith("where")) {
					t += " " + where;
				} else {
					t += " where " + where;
				}
			}
		}
		return t.replace("!!", "");
	}

	/**
	 * 剔除查询语句中的排序语句
	 * 
	 * @param queryString
	 *            查询的语句
	 * @return 如果存在排序语句，则将排序语句剔除，否则返回原查询语句
	 */
	public static String removeOrderBy(String queryString) {
		int index = queryString.indexOf("!!order");
		if (index != -1) {// 使用特殊的标记区分order by的位置
			return queryString.substring(0, index);
		} else {
			Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
					Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(queryString);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, "");
			}
			m.appendTail(sb);
			return sb.toString();
		}
	}

	/**
	 * 剔除查询语句中的选择语句
	 * 
	 * @param queryString
	 *            查询的语句
	 * @return 如果存在选择语句，则将选择语句剔除，否则返回原查询语句
	 */
	public static String removeSelect(String queryString) {
		int index = queryString.indexOf("!!from");
		if (index != -1) {// 使用特殊的标记区分 from y的位置
			return queryString.substring(index + 2);
		} else {
			queryString = queryString.replaceAll("[\\f\\n\\r\\t\\v]", " ");// 替换所有制表符、换页符、换行符、回车符为空格
			String regex = "^select .* from ";
			String[] ss = queryString.split(regex);
			if (ss.length > 0) {
				return "from " + ss[1];
			} else {
				return queryString;
			}
		}
	}
}
