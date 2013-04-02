package cn.bc.email.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.email.dao.EmailHistoryDao;
import cn.bc.email.domain.EmailHistory;

/**
 * 邮件查看历史service接口的实现
 * 
 * @author lbj
 * 
 */
public class EmailHistoryServiceImpl extends DefaultCrudService<EmailHistory> implements
		EmailHistoryService {

	private EmailHistoryDao emailHistoryDao;

	@Autowired
	public void setEmailHistoryDao(EmailHistoryDao emailHistoryDao) {
		this.emailHistoryDao = emailHistoryDao;
		this.setCrudDao(emailHistoryDao);
	}

	public int getEmailReceiverReadCount(Long emailId, String receiverCode) {
		return this.emailHistoryDao.getEmailReceiverReadCount(emailId, receiverCode);
	}
	
	
}