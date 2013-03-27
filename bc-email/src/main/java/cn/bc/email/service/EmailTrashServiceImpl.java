package cn.bc.email.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.email.dao.EmailTrashDao;
import cn.bc.email.domain.EmailTrash;

/**
 * 垃圾邮件service接口的实现
 * 
 * @author lbj
 * 
 */
public class EmailTrashServiceImpl extends DefaultCrudService<EmailTrash> implements
		EmailTrashService {

	private EmailTrashDao emailTrashDao;

	@Autowired
	public void setEmailTrashDao(EmailTrashDao emailTrashDao) {
		this.setCrudDao(emailTrashDao);
		this.emailTrashDao = emailTrashDao;
	}

	public List<EmailTrash> find4resumableByOwnerId(Long ownerId) {
		return this.emailTrashDao.find4resumableByOwnerId(ownerId);
	}
}