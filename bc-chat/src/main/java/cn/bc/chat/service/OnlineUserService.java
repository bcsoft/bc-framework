package cn.bc.chat.service;

import java.util.List;

import cn.bc.chat.OnlineUser;

/**
 * 在线用户助手
 * 
 * @author dragon
 * 
 */
public interface OnlineUserService {

	/**
	 * 获取当前的所有在线用户
	 * 
	 * @return
	 */
	List<OnlineUser> getAll();

	/**
	 * 添加一个在线用户
	 * 
	 * @param onlineUser
	 */
	void add(OnlineUser onlineUser);

	/**
	 * 获取一个在线用户
	 * 
	 * @param sid 用户会话id
	 */
	OnlineUser get(String sid);

	/**
	 * 移除在线用户
	 * 
	 * @param sid
	 */
	public void remove(String sid);
}
