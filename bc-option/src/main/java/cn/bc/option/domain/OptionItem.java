/**
 * 
 */
package cn.bc.option.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.BCConstants;
import cn.bc.core.EntityImpl;

/**
 * 选项条目
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_OPTION_ITEM")
public class OptionItem extends EntityImpl {
	private static final long serialVersionUID = 1L;

	private OptionGroup optionGroup; // 所属分组
	private String value; // 值
	private String key; // 键
	private String orderNo; // 排序号
	private String icon; // 图标样式
	private String description; // 说明
	private int status = BCConstants.STATUS_ENABLED;

	public OptionItem() {

	}

	public OptionItem(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public OptionGroup getOptionGroup() {
		return optionGroup;
	}

	public void setOptionGroup(OptionGroup optionGroup) {
		this.optionGroup = optionGroup;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "VALUE_")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "KEY_")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 如果list中不存在对应currentKey的OptionItem项，自动添加该项到列表的最后
	 * <p>
	 * 如果currentKey和currentValue均为空(null或长度为零的字符串)，将被忽略不作处理
	 * </p>
	 * <p>
	 * 如果currentKey为空、currentValue不为空， 将设置currentKey为等于currentValue的值
	 * </p>
	 * <p>
	 * 如果currentKey不为空、currentValue为空， 将设置currentValue为等于currentKey的值
	 * </p>
	 * <p>
	 * 如果currentKey不为空，优先使用currentKey的值来判断选项是否存在， 否则使用currentValue的值来判断选项是否存在
	 * </p>
	 * 
	 * @param optionGroupKey
	 *            所属分类，对应OptionGroup属性key的值
	 * @param currentKey
	 *            当前选项的key
	 * @param currentValue
	 *            当前选项的value
	 * @return
	 */
	public static List<Map<String, String>> insertIfNotExist(
			List<Map<String, String>> list, String currentKey,
			String currentValue) {
		if (currentKey != null) {
			if (currentValue == null) {
				currentValue = currentKey;
			}
			boolean exist = false;
			for (Map<String, String> oi : list) {
				if (currentKey.equals(oi.get("key"))) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("key", currentKey);
				map.put("value", currentValue);
				list.add(map);
			}
		} else {
			if (currentValue != null) {
				boolean exist = false;
				for (Map<String, String> oi : list) {
					if (currentValue.equals(oi.get("value"))) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("key", currentValue);
					map.put("value", currentValue);
					list.add(map);
				}
			}
		}
		return list;
	}

	/**
	 * 将键值对的值列表转换为value(对应键值对的key)、label(对应键值对的value)的json数组
	 * 
	 * @param keyValues
	 *            键值对
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray toLabelValues(List<Map<String, String>> keyValues)
			throws JSONException {
		return toLabelValues(keyValues, null, null);
	}

	/**
	 * 将键值对的值列表转换为value(对应键值对的key)、label(对应键值对的value)的json数组
	 * 
	 * @param keyValues
	 *            键值对
	 * @param sameKey
	 *            从keyValues中获取label和value的值的key
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray toLabelValues(List<Map<String, String>> keyValues,
			String sameKey) throws JSONException {
		return toLabelValues(keyValues, sameKey, sameKey);
	}

	/**
	 * 将键值对的值列表转换为value(对应键值对的key)、label(对应键值对的value)的json数组
	 * 
	 * @param keyValues
	 *            键值对
	 * @param labelKey
	 *            从keyValues中获取label值的key
	 * @param valueKey
	 *            从keyValues中获取value值的key
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray toLabelValues(List<Map<String, String>> keyValues,
			String labelKey, String valueKey) throws JSONException {
		if (labelKey == null)
			labelKey = "value";
		if (valueKey == null)
			valueKey = "key";
		JSONArray dropDownArray = new JSONArray();
		if (keyValues != null) {
			JSONObject json;
			for (Map<String, String> map : keyValues) {
				json = new JSONObject();
				json.put("label", map.get(labelKey));
				json.put("value", map.get(valueKey));
				dropDownArray.put(json);
			}
		}
		return dropDownArray;
	}
}