package cn.bc.email.service;

import cn.bc.core.service.CrudService;
import cn.bc.email.domain.Email;

/**
 * 邮件Service接口
 * 
 * @author dragon
 * 
 */
public interface EmailService extends CrudService<Email> {
	
	/**
	 * 根据邮件id和接收人id 修改邮件的查阅状态
	 * @param ids
	 * @param receiver_id
	 * @param read
	 */
	void mark(Long[] ids,Long receiver_id,boolean read);
	
	/**
	 * 根据接收人id 将未读的邮件全部标记为已读
	 * @param receiver_id
	 */
	void mark4read(Long receiver_id);
}