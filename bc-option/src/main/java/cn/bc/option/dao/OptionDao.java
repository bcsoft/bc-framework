package cn.bc.option.dao;

import java.util.List;
import java.util.Map;

import cn.bc.option.domain.OptionGroup;
import cn.bc.option.domain.OptionItem;

/**
 * 选项Dao接口
 * 
 * @author dragon
 * 
 */
public interface OptionDao {
	/**
	 * 获取选项条目的值
	 * 
	 * @param groupKey
	 *            选项分组的Key
	 * @param itemKey
	 *            选项条目的Key
	 * @return 如果选项不存在就返回null
	 */
	String getItemValue(String groupKey, String itemKey);

	/**
	 * 获取已配置的分类列表
	 * 
	 * @return
	 */
	List<OptionGroup> findOptionGroup();

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
	 * @param optionGroupValues
	 *            所属分类，对应OptionGroup属性value的值
	 * @return 返回结果的key为对应的OptionGroup的Key值，value为对应该key的OptionItem的列表
	 */
	Map<String, List<OptionItem>> findOptionItemByGroupValues(
			String[] optionGroupValues);

	/**
	 * 查找指定分类的选项条目列表
	 * 
	 * @param optionGroupValue
	 *            所属分类，对应OptionGroup属性key的值
	 * @return
	 */
	List<OptionItem> findOptionItemByGroupKey(String optionGroupKey);

	/**
	 * 批量查找指定分类的选项条目列表
	 * 
	 * @param optionGroupKeys
	 *            所属分类，对应OptionGroup属性Key的值
	 * @return 
	 *         返回结果的key为对应的OptionGroup的Key值，value为对应该key的OptionItem的Map列表，Map格式为：
	 *         <p>
	 *         key -- optionItem的key value -- optionItem的value
	 *         </p>
	 */
	Map<String, List<Map<String, String>>> findOptionItemByGroupKeys(
			String[] optionGroupKeys);

	/**
	 * 批量查找指定分类的正常状态的选项条目列表
	 * 
	 * @param optionGroupKeys
	 *            所属分类，对应OptionGroup属性Key的值
	 * @return 
	 *         返回结果的key为对应的OptionGroup的Key值，value为对应该key的OptionItem的Map列表，Map格式为：
	 *         <p>
	 *         key -- optionItem的key value -- optionItem的value
	 *         </p>
	 */
	Map<String, List<Map<String, String>>> findActiveOptionItemByGroupKeys(
			String[] optionGroupKeys);
}
