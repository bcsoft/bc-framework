package cn.bc.core.query.condition;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.QueryOperator;
import cn.bc.core.query.condition.impl.SimpleAdvanceCondition;

import javax.json.Json;
import javax.json.JsonArray;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 高级条件配置
 * <p>
 * <p>用于隐藏实际的sql语句，对外仅暴露条件的标识符，由 dao 层进行条件过滤，避免 sql 攻击</p>
 *
 * @author dragon 2016-10-14
 */
public interface AdvanceCondition {
	/**
	 * 标识符
	 */
	String getId();

	/**
	 * 类型
	 */
	QueryOperator getOperator();

	/**
	 * @return 参数值
	 */
	Object getValue();

	/**
	 * 根据配置初始化一个条件实例
	 *
	 * @param one 单个条件的配置，数据结构为 [id, value[, type, operator]]
	 * @return 条件实例
	 */
	static AdvanceCondition toCondition(JsonArray one) {
		return new SimpleAdvanceCondition(one);
	}

	/**
	 * 根据配置初始化条件集
	 *
	 * @param multi 多个条件配置的 json 数组字符串，数据结构为 [[id, value[, type, operator]], ...]
	 * @return 条件集
	 */
	static List<AdvanceCondition> toConditions(String multi) {
		List<AdvanceCondition> all = new ArrayList<>();
		if (multi != null) try {
			Json.createReader(new StringReader(URLDecoder.decode(multi, "UTF-8"))).readArray()
					.forEach(item -> all.add(toCondition((JsonArray) item)));
		} catch (UnsupportedEncodingException e) {
			throw new CoreException(e.getMessage(), e);
		}
		return all;
	}
}
