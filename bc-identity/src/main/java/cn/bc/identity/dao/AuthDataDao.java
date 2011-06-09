package cn.bc.identity.dao;

import java.util.List;

import cn.bc.identity.domain.AuthData;

/**
 * 认证信息Dao接口
 * @author dragon
 *
 */
public interface AuthDataDao{
	/**
	 * 获取
	 * @param id 主键
	 * @return
	 */
	AuthData load(Long id);
	
	/**
	 * 批量获取
	 * @param ids 主键列表
	 * @return
	 */
	List<AuthData> find(Long[] ids);
	
	/**
	 * 批量更新密码
	 * @param ids
	 * @param password
	 */
	int updatePassword(Long[] ids, String password);
	
	/**
	 * 保存
	 * @param authData 
	 * @return
	 */
	AuthData save(AuthData authData);
	
	/**
	 * 删除
	 * @param id 
	 * @return
	 */
	void delete(Long id);
	
	/**
	 * 批量删除
	 * @param ids 
	 * @return
	 */
	void delete(Long[] ids);
}
