package cn.bc.spider.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.spider.domain.SpiderConfig;

/**
 * 抓取配置Dao接口
 * 
 * @author dragon
 * 
 */
public interface SpiderConfigDao extends CrudDao<SpiderConfig> {
	/**
	 * 根据编码获取抓取配置
	 * 
	 * @param code
	 *            编码
	 * 
	 * @return 指定编码的模板对象
	 */
	public SpiderConfig loadByCode(String code);
}
