/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.HashMap;
import java.util.Map;

import cn.bc.identity.domain.ActorDetail;
import cn.bc.web.formater.KeyValueFormater;

/**
 * 性别的格式化
 * 
 * @author dragon
 * 
 */
public class SexFormater extends KeyValueFormater {
	public SexFormater() {
		Map<String, String> kvs = new HashMap<String, String>();
		kvs.put(String.valueOf(ActorDetail.SEX_NONE), "未设");
		kvs.put(String.valueOf(ActorDetail.SEX_MAN), "男");
		kvs.put(String.valueOf(ActorDetail.SEX_WOMAN), "女");
		this.setKvs(kvs);
	}
}
