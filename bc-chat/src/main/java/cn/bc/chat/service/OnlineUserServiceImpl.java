package cn.bc.chat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.chat.OnlineUser;

/**
 * 在线用户助手
 * 
 * @author dragon
 * 
 */
public class OnlineUserServiceImpl implements OnlineUserService {
	private static Log logger = LogFactory.getLog(OnlineUserServiceImpl.class);
	private Map<String, OnlineUser> onlineUsers = new LinkedHashMap<String, OnlineUser>();

	public List<OnlineUser> getAll() {
		List<OnlineUser> all = new ArrayList<OnlineUser>(onlineUsers.values());
		Collections.reverse(all);// 按时间逆序排序
		return all;
	}

	public void add(OnlineUser onlineUser) {
		if (onlineUser == null)
			return;

		if (logger.isInfoEnabled()) {
			logger.info(onlineUser.getName() + "上线了：" + onlineUser.toString());
		}
		this.onlineUsers.put(onlineUser.getSid(), onlineUser);
	}

	public void remove(String sid) {
		if (sid == null || sid.length() == 0)
			return;

		OnlineUser onlineUser = onlineUsers.remove(sid);
		if (logger.isInfoEnabled() && onlineUser != null) {
			logger.info(onlineUser.getName() + "下线了：" + onlineUser.toString());
		}
	}

	public OnlineUser get(String sid) {
		return onlineUsers.get(sid);
	}
}
