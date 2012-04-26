package cn.bc;

/**
 * 平台常数定义
 * 
 * @author dragon
 * 
 */
public class BCConstants {
	/** 平台命名空间前缀 */
	public final static String NAMESPACE = "/bc";

	/** 状态：草稿 */
	public static final int STATUS_DRAFT = -1;
	/** 状态：正常 */
	public static final int STATUS_ENABLED = 0;
	/** 状态：已禁用 */
	public static final int STATUS_DISABLED = 1;
	/** 状态：已删除 */
	public static final int STATUS_DELETED = 2;

	/** 流水号连接符 */
	public static final String SN_SPLIT_SYMBOL = "-";
}
