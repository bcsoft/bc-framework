package cn.bc.spider.service;

import java.util.Map;

import cn.bc.spider.Result;
import cn.bc.spider.domain.SpiderConfig;

/**
 * 网络抓取Service接口
 * 
 * @author dragon
 * 
 */
public interface SpiderService {
	/**
	 * 获取指定编码的抓取配置
	 * 
	 * @param code
	 * @return 指定编码的抓取配置
	 */
	SpiderConfig loadConfig(String code);

	/**
	 * 抓取验证码图片并保存到本地，返回本地文件路径
	 * 
	 * @param group
	 * @param url
	 * @return 本地文件路径
	 */
	String getCaptcha(String group, String url);

	/**
	 * 执行抓取
	 * 
	 * @param config 配置
	 * @param params 附加的请求参数
	 * @return
	 */
	Result<Object> doSpide(SpiderConfig config, Map<String, String> params);
}
