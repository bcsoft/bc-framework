package cn.bc.message.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.message.dao.MessageDao;
import cn.bc.message.domain.Message;

/**
 * 消息service接口的实现
 * 
 * @author dragon
 * 
 */
public class MessageServiceImpl extends
		DefaultCrudService<Message> implements MessageService {
	//private MessageDao messageDao;

	@Autowired
	public void setMessageDao(MessageDao messageDao) {
		//this.messageDao = messageDao;
		this.setCrudDao(messageDao);
	}
}
