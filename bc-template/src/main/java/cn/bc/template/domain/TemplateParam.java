/**
 * 
 */
package cn.bc.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 模板参数
 * 
 * @author lbj
 */
@Entity
@Table(name = "BC_TEMPLATE_Param")
public class TemplateParam extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(TemplateParam.class);
	private int status;// 状态：0-正常,1-禁用
	private String orderNo;// 排序号
	private String name;// 模板类型名称
	private String desc;// 备注
	
	
	/**
	 * 详细配置格式：json对象数组。
	 * 对象属性解释，type:返回的的类型，slq:数据库查询的语句,key:替换指定的键值
	 * 
	 * {type:"Map",sql:"select name as key1,company as key2,code as key3 from bs_car"}  
	 * 		-- sql查询只返回一行的数据，每一列数据对应一个key值，模板占位参数直接填写${key1},${key2},${key2}
	 * {type:"List<Object>",sql:"...",key:"key"}    	
	 * 		-- sql查询只返回一列数据，整个集合对应一个key值，模板占位参数应填写${key[0]},${key[1]},${key[2]}
	 * {type:"List<Map<String,Object>>",sql:"select name as key1,company as key2,code as key3 from bs_car",key:"key"}	 
	 * 		-- sql查询返回多列数据，每一行的数据为一个map的集合，整个集合对应一个key值，模板占位参数应填写${key[0].key1},${key[0].key2,${key[0].key3}
	 * {type:"List<list<Object>>",sql:"...",key:"key"}	
	 * 		-- sql查询返回多列数据，每一"行"的数据为一个list的集合，整个集合对应一个key值，模板占位参数应填写${key[0][0]},${key[0][1]}
	 * {type:"Object[]",sql:"...",key:"key"}		 	
	 * 		-- 与类型[List<Object>]情况一样。
	 * {type:"List<Object[]>",sql:"...",key:"key"}		
	 * 		-- 与类型[List<list<Object>>]情况一样。
	 * 
	 * {type:"Json2Map",sql:"...",key:"key"}		
	 * 		-- 与类型[Map]情况一样，sql必须返回只有一个字符串值
	 * {type:"Json2ListMap",sql:"...",key:"key"}		
	 * 		-- 与类型[List<Map<String,Object>>]，sql必须返回只有一个字符串值
	 * {type:"Json2Array",sql:"...",key:"key"}		
	 * 		-- 与类型[List<Object>]，sql必须返回只有一个字符串值
	 * {type:"Json2List",sql:"...",key:"key"}		
	 * 		-- 与类型[List<Object>]，sql必须返回只有一个字符串值
	 * 
	 * {type:"spel",sql:"...",key:"key"}根据spring表达式返回集合
	 *
	 * @param templateParam 模板参数
	 * 
	 * @param mapFormatSql 格式化sql上的占位符，sql集合的集合
	 * 
	 */
	private String config;//模板参数配置信息
	


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

	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
		this.configJson = null;
	}
	
	private JSONArray configJson;// 缓存变量
	
	/**
	 * 获取配置的json数组对象
	 * 
	 * @return
	 */
	@Transient
	public JSONArray getConfigJson() {
		if (configJson != null)
			return configJson;

		if (this.getConfig() == null || this.getConfig().length() == 0) {
			this.configJson = null;
			return this.configJson;
		}

		try {
			configJson = new JSONArray(this.getConfig().replaceAll("\\s", " "));// 替换换行、回车等符号为空格
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			this.configJson = null;
		}
		return configJson;
	}
}
