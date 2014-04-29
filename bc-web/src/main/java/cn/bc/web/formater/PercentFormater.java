/**
 *
 */
package cn.bc.web.formater;

import java.text.DecimalFormat;

/**
 * 数字格式化为百分比，如"0.1234"格式化为"12.34%"
 *
 * @author dragon
 */
public class PercentFormater extends AbstractFormater<String> {
    protected DecimalFormat format;

    public PercentFormater() {
        format = new DecimalFormat("#0.00%");
    }

    public PercentFormater(String pattern) {
        format = new DecimalFormat(pattern);
    }

    public String format(Object context, Object value) {
        if (value == null)
            return null;
        if (value instanceof Number) {
            Number n = (Number) value;
            if (n.toString().equals("0"))
                return "0";
            else
                return format.format(n);

        } else {
            return value.toString();
        }
    }
}
