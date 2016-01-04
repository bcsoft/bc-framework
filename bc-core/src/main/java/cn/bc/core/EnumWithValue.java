package cn.bc.core;

/**
 * 为 Enum 类型的属性提供指定特定持久化值的接口
 *
 * @author dragon 2016-01-04
 */
public interface EnumWithValue {
	/**
	 * 获取持久化值
	 */
	int value();
}