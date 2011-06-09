package cn.bc.core.util;

import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commontemplate.tools.TemplateRenderer;

/**
 * 模板工具类
 * 对字符串格式化
 * 
 * @author dragon
 */
public class TemplateUtils {
	protected static Log logger = LogFactory.getLog(TemplateUtils.class);
	private TemplateUtils() {}
	
    /**
     * 对字符串进行格式化，格式化的方式为：对源字符串(sourceString)中的{n}(其中n表示数字，从0开始)进行替换
     * @param tpl 源字符串
     * @param args 所要格式化的参数信息
     * @return 返回格式化后的字符串
     */
    public static String format(String tpl, Object[] args){
        if(tpl == null || tpl.length() == 0)
            return "";
        if(null == args || args.length == 0)
            return tpl;
        return MessageFormat.format(tpl, args);
    }
    
    /**
	 * 对字符串进行格式化，如源字符串为：国籍=${country}, 姓名=${name}, 身份证号码=${cardID}<br>
	 * 则在args中分别添加key为country、name和cardID的值，如<br>
	 * args.put("country", "中华人民共和国");<br>
	 * args.put("name", "伏羲氏");<br>
	 * args.put("cardID", "00000000000000000X");<br> 
	 * 则返回的值为：国籍=中华人民共和国, 姓名=伏羲氏, 身份证号码=00000000000000000X<br> 
	 * 另外，参数也接受类似:obj.property的格式
	 * @param tpl 所要格式化的字符串模板
	 * @param args 参数值
	 * @return 返回格式化好的字符串
	 */
	public static String format(String tpl, Map<String, Object> args){
		if(tpl == null || tpl.length() == 0)
			return "";
		if(null == args || args.isEmpty())
			return tpl;
		
		TemplateRenderer tplRender = new TemplateRenderer(tpl);
		tplRender.putAll(args);
		return tplRender.evaluate();
	}
}