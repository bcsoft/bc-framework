/**
 * 
 */
package cn.bc.web.formater;

/**
 * html换行符的格式化
 * 
 * @author dragon
 * 
 */
public class BrFormater extends AbstractFormater<String> {
	private String br = "<br/>";
	private String seperator = ",";

	public BrFormater() {

	}

	public BrFormater(String seperator) {
		this();
		this.seperator = seperator;
	}

	public String format(Object context, Object value) {
		if (value == null)
			return null;
		else
			return value.toString().replaceAll(seperator, br);
	}
}
