/**
 * 
 */
package cn.bc.spider.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.core.exception.CoreException;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 抓取配置
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_SPIDER_CFG")
public class SpiderConfig extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(SpiderConfig.class);

	private int status;// 状态：0-正常,1-禁用
	private String orderNo;// 排序号
	private String code;// 编码
	private String config;// json配置
	private JSONObject configJson;// json配置的对象化

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "CFG")
	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
		this.configJson = null;
	}

	/**
	 * 获取配置的json对象
	 * 
	 * @return
	 */
	@Transient
	public JSONObject getConfigJson() {
		if (configJson != null)
			return configJson;

		if (this.getConfig() == null || this.getConfig().length() == 0) {
			this.configJson = null;
			return this.configJson;
		}

		try {
			configJson = new JSONObject(this.getConfig().replaceAll("\\s", " "));// 替换换行、回车等符号为空格
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			this.configJson = null;
		}
		return configJson;
	}

	/**
	 * 获取指定配置键的值
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <V> V get(String key, Class<V> clazz) {
		try {
			if (has(key))
				return (V) getConfigJson().get(key);
			else
				return null;
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			throw new CoreException(e);
		}
	}

	/**
	 * 获取指定配置键的值
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		try {
			if (has(key))
				return getConfigJson().get(key);
			else
				return null;
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			throw new CoreException(e);
		}
	}

	/**
	 * 判断是否包含指定键的配置
	 * 
	 * @param key
	 * @return
	 */
	public boolean has(String key) {
		return getConfigJson().has(key);
	}
}