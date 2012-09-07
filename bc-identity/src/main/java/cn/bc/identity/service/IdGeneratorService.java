package cn.bc.identity.service;


/**
 * 标识生成器Service接口
 * 
 * @author dragon
 * 
 */
public interface IdGeneratorService {
	/**
	 * 获取下一个值，该值是经过格式化后的值
	 * 
	 * @param type
	 *            类型
	 * @return
	 */
	String next(String type);

	/**
	 * 获取当前值，改值是经过格式化后的值
	 * 
	 * @param type
	 *            类型
	 * @return
	 */
	String current(String type);

	/**
	 * 获取下一个值，改值是未经任何格式的原始值
	 * 
	 * @param type
	 *            类型
	 * @return
	 */
	Long nextValue(String type);

	/**
	 * 获取当前值，改值是未经任何格式的原始值
	 * 
	 * @param type
	 * @return
	 */
	Long currentValue(String type);

	/**获取指定类型的月流水号(最后为4位数字流水号)，返回值的格式为“yyyyMM####”
	 * @param type 
	 * @param pattern 数字的格式化模式，参考类DecimalFormat的说明
	 * @return
	 */
	public String nextSN4Month(String type);

	/**获取指定类型的月流水号，返回值的格式为“yyyyMM####”
	 * @param type 
	 * @param pattern 数字的格式化模式，参考类DecimalFormat的说明
	 * @return
	 */
	public String nextSN4Month(String type, String pattern);

	/**获取指定类型的日流水号(最后为4位数字流水号)，返回值的格式为“yyyyMMdd####”
	 * @param type 
	 * @param pattern 数字的格式化模式，参考类DecimalFormat的说明
	 * @return
	 */
	public String nextSN4Day(String type);

	/**获取指定类型的日流水号，返回值的格式为“yyyyMMdd####”
	 * @param type 
	 * @param pattern 数字的格式化模式，参考类DecimalFormat的说明
	 * @return
	 */
	public String nextSN4Day(String type, String pattern);
}
