package cn.bc.web.struts.beanutils;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;

/**
 * 默认的转换器注册工具，封装一些默认转换器的注册
 * 
 * @author dragon
 * @since 2010-12-09
 * @see Converter
 */
public class ConverterUtils {
	/**
	 * <p>为{@link org.apache.commons.beanutils.ConvertUtils}类注册默认的转换器:</p>
	 * <p>1)字符串为空值""时,数字类型({@link Number})转换为null而不是0 (如{@link Integer}、{@link Long}、{@link Float}、{@link Double})</p>
	 * <p>2){@link String} --> {@link Date2Day} <-- {@link Date} 转换</p>
	 * <p>3){@link String} --> {@link Date2Minute} <-- {@link Date} 转换</p>
	 * <p>4){@link String} --> {@link Date2Second} <-- {@link Date} 转换</p>
	 */
	public static void registDefault() {
		//将""转换为null
		ConvertUtils.register(new cn.bc.web.struts.beanutils.StringConverter(), String.class);
		
		//字符串为空值""时,数字类型转换为null
		ConvertUtils.register(new IntegerConverter(null), Integer.class);
		ConvertUtils.register(new LongConverter(null), Long.class);
		ConvertUtils.register(new FloatConverter(null), Float.class);
		ConvertUtils.register(new DoubleConverter(null), Double.class);
		
		//自定义的日期转换器
		ConvertUtils.register(new Date2DayConverter(), Date2Day.class);
		ConvertUtils.register(new Date2MinuteConverter(), Date2Minute.class);
		ConvertUtils.register(new Date2SecondConverter(), Date2Second.class);
	}
}