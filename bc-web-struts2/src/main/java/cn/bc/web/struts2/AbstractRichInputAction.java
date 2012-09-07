/**
 * 
 */
package cn.bc.web.struts2;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 可输入可选下拉框支持的抽象Action
 * 
 * @author dragon
 * 
 */
public abstract class AbstractRichInputAction<T extends Object> extends
		ActionSupport {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog("cn.bc.web.struts2.AbstractRichInputAction");

	public String json;
	private String term;
	public boolean ignore = true;// 是否不处理空值

	/**
	 * 获取用户当前输入的值
	 * 
	 * @return
	 */
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * 是否不处理空值
	 * 
	 * @return
	 */
	protected boolean ignoreEmptyValue() {
		return this.ignore;
	}

	@Override
	public String execute() throws Exception {
		JSONArray json = new JSONArray();

		if (logger.isInfoEnabled())
			logger.info("value=" + this.getTerm());

		// 参数判断
		if (this.ignoreEmptyValue() && (term == null || term.length() == 0)) {
			this.json = json.toString();
			return "json";
		}

		// 获取数据
		List<T> data = this.find(this.getTerm());
		convert2JsonArray(json, data);
		if (logger.isDebugEnabled())
			logger.debug("json=" + json);

		// 返回结果
		this.json = json.toString();
		return "json";
	}

	/**
	 * @param value
	 *            查询的条件
	 * @return
	 */
	protected abstract List<T> find(String value);

	/**
	 * 转换数据为JsonArray格式
	 * 
	 * @param array
	 * @param data
	 */
	protected void convert2JsonArray(JSONArray array, List<T> data) {
		for (T d : data) {
			array.put(this.convert2JsonObject(d));
		}
	}

	/**
	 * 转换对象为JsonObject格式
	 * 
	 * @param obj
	 * @return
	 */
	protected Object convert2JsonObject(T obj) {
		return obj;
	}
}