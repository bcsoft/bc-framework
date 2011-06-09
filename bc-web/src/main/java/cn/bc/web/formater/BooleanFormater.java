/**
 * 
 */
package cn.bc.web.formater;

/**
 * 布尔类型值的格式化
 * 
 * @author dragon
 * 
 */
public class BooleanFormater implements Formater {
	private String yes = "是";
	private String no = "否";

	public BooleanFormater() {

	}

	public BooleanFormater(String yes, String no) {
		this();
		this.yes = yes;
		this.no = no;
	}

	public String format(Object value) {
		if (value == null)
			return null;
		if (value instanceof Boolean)
			return ((Boolean) value).booleanValue() ? yes : no;
		else if (value instanceof String)
			return "true".equalsIgnoreCase((String) value) ? yes : no;
		else
			return value.toString();
	}
}
