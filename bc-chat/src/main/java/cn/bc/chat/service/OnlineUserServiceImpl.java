package cn.bc.chat.service;

import java.util.ArrayList;
import java.util.List;

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
	private List<OnlineUser> onlineUsers = new ArrayList<OnlineUser>();

	public List<OnlineUser> getAll() {
		return onlineUsers;
	}

	public void add(OnlineUser onlineUser) {
		if (onlineUser == null)
			return;

		if (logger.isInfoEnabled()) {
			logger.info(onlineUser.getName() + "上线了：" + onlineUser.toString());
		}
		this.onlineUsers.add(onlineUser);
	}

	public void remove(Long id) {
		if (id == null)
			return;

		List<OnlineUser> toRemoves = new ArrayList<OnlineUser>();
		for (OnlineUser onlineUser : onlineUsers) {
			if (id.equals(onlineUser.getId())) {
				toRemoves.add(onlineUser);
			}
		}
		for (OnlineUser onlineUser : toRemoves) {
			if (logger.isInfoEnabled()) {
				logger.info(onlineUser.getName() + "下线了："
						+ onlineUser.toString());
			}
			onlineUsers.remove(onlineUser);
		}
	}
}
