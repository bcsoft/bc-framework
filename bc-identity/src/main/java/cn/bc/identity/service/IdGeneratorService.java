package cn.bc.identity.service;

import cn.bc.identity.domain.IdGenerator;

/**
 * 标识生成器Service接口
 * @author dragon
 *
 */
public interface IdGeneratorService {
	/**
	 * 获取下一个值，改值是经过格式化后的值
	 * @param type 类型
	 * @return
	 */
	String next(String type);
	
	/**
	 * 获取当前值，改值是经过格式化后的值
	 * @param type 类型
	 * @return
	 */
	String current(String type);
	
	/**
	 * 获取下一个值，改值是未经任何格式的原始值
	 * @param type 类型
	 * @return
	 */
	Long nextValue(String type);
	
	/**
	 * 获取当前值，改值是未经任何格式的原始值
	 * @param type
	 * @return
	 */
	Long currentValue(String type);
	
	/**
	 * 插入一个新的类型
	 * @param generator
	 */
	void save(IdGenerator generator);
}
