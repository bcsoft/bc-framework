package cn.bc.remoting.service;


/**
 * 通用的服务接口
 * 
 * @author dragon
 * 
 */
public interface CommonService {
	/**
	 * 用于测试接口是否正常的方法
	 * 
	 * @param token
	 *            连接令牌
	 * @return
	 */
	boolean test(String token);
}
