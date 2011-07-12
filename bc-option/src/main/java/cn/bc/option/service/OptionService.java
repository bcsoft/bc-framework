package cn.bc.option.service;

import java.util.List;

import cn.bc.option.domain.OptionGroup;
import cn.bc.option.domain.OptionItem;

/**
 * 选项Service接口
 * 
 * @author dragon
 * 
 */
public interface OptionService {
	/**
	 * 查找指定分类的选项条目列表
	 * 
	 * @param optionGroupValue
	 *            所属分类，对应OptionGroup属性value的值
	 * @return
	 */
	List<OptionItem> findOptionItemByGroupValue(String optionGroupValue);
	
	/**
	 * 查找指定分类的选项条目列表
	 * 
	 * @param optionGroupValue
	 *            所属分类，对应OptionGroup属性value的值
	 * @param insertEmpty
	 *            是否在最前面添加空白选项
	 * @return
	 */
	List<OptionItem> findOptionItemByGroupValue(String optionGroupValue, boolean insertEmpty);

	/**
	 * 获取已配置的分类列表
	 * 
	 * @return
	 */
	List<OptionGroup> findOptionGroup();
}
