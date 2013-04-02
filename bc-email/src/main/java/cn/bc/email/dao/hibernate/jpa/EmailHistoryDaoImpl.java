package cn.bc.email.dao.hibernate.jpa;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.email.dao.EmailHistoryDao;
import cn.bc.email.domain.EmailHistory;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 邮件查看历史dao接口的实现
 * 
 * @author lbj
 * 
 */
public class EmailHistoryDaoImpl extends HibernateCrudJpaDao<EmailHistory> implements
		EmailHistoryDao {

	
	public int getEmailReceiverReadCount(Long emailId, String receiverCode) {
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("email.id", emailId));
		condition.add(new EqualsCondition("reader.code", receiverCode));
		return this.createQuery().condition(condition).list().size();
	}

}