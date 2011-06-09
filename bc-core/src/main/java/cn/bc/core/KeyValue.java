package cn.bc.core;

/**
 * 值名称的封装
 * 
 * @author dragon
 * 
 */
public class KeyValue {
	private String key;// 实际的值
	private String value;// 显示的名称

	public KeyValue() {
	}

	public KeyValue(String key, String value) {
		this();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
