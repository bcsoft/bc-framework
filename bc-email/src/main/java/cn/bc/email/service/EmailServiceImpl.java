package cn.bc.email.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.email.dao.EmailDao;
import cn.bc.email.domain.Email;

/**
 * 邮件service接口的实现
 * 
 * @author dragon
 * 
 */
public class EmailServiceImpl extends DefaultCrudService<Email> implements
		EmailService {
	private EmailDao emailDao;

	@Autowired
	public void setEmailDao(EmailDao emailDao) {
		this.setCrudDao(emailDao);
		this.emailDao = emailDao;
	}
}
