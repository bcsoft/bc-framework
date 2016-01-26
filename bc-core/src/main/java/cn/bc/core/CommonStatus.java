package cn.bc.core;

import java.util.LinkedHashMap;

/**
 * 常用状态
 *
 * @author dragon 2016-01-04
 */
public enum CommonStatus implements EnumWithValue {
	/**
	 * 草稿
	 */
	Draft(-1, "草稿"),

	/**
	 * 正常
	 */
	Enabled(0, "正常"),

	/**
	 * 已禁用
	 */
	Disabled(1, "禁用"),

	/**
	 * 已删除
	 */
	Deleted(2, "删除");

	private final int value;
	private final String label;

	CommonStatus(int value, String label) {
		this.value = value;
		this.label = label;
	}

	@Override
	public int value() {
		return value;
	}

	/**
	 * 获取持久化值对应的权举值
	 *
	 * @throws IllegalArgumentException 如果指定的值没有对应的权举值
	 */
	public static CommonStatus valueOf(int value) {
		for (CommonStatus status : CommonStatus.values()) {
			if (status.value() == value) return status;
		}
		throw new IllegalArgumentException("unsupport CommonStatus value: " + value);
	}

	/**
	 * 获取状态的显示值
	 */
	public String label() {
		return label;
	}

	/**
	 * 所有状态的 Map，key为 name()，value 为 label()
	 */
	public static LinkedHashMap<String, String> nameLabelMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		for (CommonStatus status : CommonStatus.values()) {
			map.put(status.name(), status.label());
		}
		return map;
	}

	/**
	 * 正常+禁用状态的 Map，key为 name()，value 为 label()
	 */
	public static LinkedHashMap<String, String> nameLabelMap4EnabledAndDisabled() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put(Enabled.name(), Enabled.label());
		map.put(Disabled.name(), Disabled.label());
		return map;
	}

	/**
	 * 所有状态的 Map，key为 value()，value 为 label()
	 */
	public static LinkedHashMap<String, String> valueLabelMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		for (CommonStatus status : CommonStatus.values()) {
			map.put(String.valueOf(status.value()), status.label());
		}
		return map;
	}

	/**
	 * 正常+禁用状态的 Map，key为 value()，value 为 label()
	 */
	public static LinkedHashMap<String, String> valueLabelMap4EnabledAndDisabled() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		map.put(String.valueOf(Enabled.value()), Enabled.label());
		map.put(String.valueOf(Disabled.value()), Disabled.label());
		return map;
	}
}